package org.csps.backend.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.csps.backend.domain.dtos.request.StudentMigrationRequestDTO;
import org.csps.backend.domain.dtos.response.StudentResponseDTO;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.csps.backend.domain.enums.UserRole;
import org.csps.backend.exception.StudentAlreadyExistsException;
import org.csps.backend.mapper.StudentMapper;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.repository.UserAccountRepository;
import org.csps.backend.repository.UserProfileRepository;
import org.csps.backend.service.StudentMigrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentMigrationServiceImpl implements StudentMigrationService {

    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${csps.userNameformat}")
    private String userNameFormat;
    @Value("${csps.passwordformat}")
    private String passwordFormat;

    @Override
    @Transactional
    public List<StudentResponseDTO> migrateStudentsFromCsv(InputStream csvFile) {
        List<StudentResponseDTO> migratedStudents = new ArrayList<>();
        int successCount = 0;
        int skipCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile))) {
            String line;
            boolean skipHeader = true;

            while ((line = reader.readLine()) != null) {
                /* skip empty lines and header */
                if (line.trim().isEmpty()) continue;
                if (skipHeader || line.contains("YEAR_LEVEL")) {
                    skipHeader = false;
                    continue;
                }

                try {
                    /* parse CSV line: YEAR_LEVEL, LASTNAME, FIRSTNAME, ID_NUMBER */
                    String[] parts = line.split(",");
                    if (parts.length < 4) {
                        log.warn("Skipping malformed CSV line: {}", line);
                        skipCount++;
                        continue;
                    }

                    StudentMigrationRequestDTO dto = StudentMigrationRequestDTO.builder()
                            .yearLevel(Byte.parseByte(parts[0].trim()))
                            .lastName(parts[1].trim())
                            .firstName(parts[2].trim())
                            .studentId(parts[3].trim())
                            .build();

                    StudentResponseDTO migrated = migrateSingleStudent(dto);
                    migratedStudents.add(migrated);
                    successCount++;

                } catch (Exception e) {
                    log.warn("Failed to migrate student from line: {} - {}", line, e.getMessage());
                    skipCount++;
                }
            }
            log.info("CSV migration completed: {} success, {} skipped", successCount, skipCount);

        } catch (Exception e) {
            throw new RuntimeException("CSV parsing failed: " + e.getMessage(), e);
        }

        return migratedStudents;
    }

    @Override
    @Transactional
    public StudentResponseDTO migrateSingleStudent(StudentMigrationRequestDTO dto) {
        /* check if student already exists */
        if (studentRepository.existsById(dto.getStudentId())) {
            throw new StudentAlreadyExistsException("Student with ID " + dto.getStudentId() + " already exists");
        }


        /* PHASE 1: Create incomplete UserProfile */
        UserProfile userProfile = UserProfile.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(null)  // to be filled later
                .birthDate(null)   // to be filled later
                .email(null)       // to be filled later
                .isProfileComplete(false)  // flag as incomplete
                .build();

        userProfile = userProfileRepository.save(userProfile);

        String generatedUsername = userNameFormat + dto.getStudentId();
        String generatedPassword = passwordFormat + dto.getStudentId().substring(2);

        /* PHASE 2: Create UserAccount with temp password = studentId */
        UserAccount userAccount = UserAccount.builder()
                .username(generatedUsername)
                .password(passwordEncoder.encode(generatedPassword))  // temp password
                .role(UserRole.STUDENT)
                .isVerified(false)
                .userProfile(userProfile)
                .build();
        

        
        userAccount = userAccountRepository.save(userAccount);

        /* PHASE 3: Create Student entity */
        Student student = Student.builder()
                .studentId(dto.getStudentId())
                .yearLevel(dto.getYearLevel())
                .userAccount(userAccount)
                .build();

        student = studentRepository.save(student);
        log.info("Student migrated: {} - {}, {}", dto.getStudentId(), dto.getFirstName(), dto.getLastName());

        return studentMapper.toResponseDTO(student);
    }
}

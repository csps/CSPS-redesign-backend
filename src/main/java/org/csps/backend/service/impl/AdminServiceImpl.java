package org.csps.backend.service.impl;

import java.util.Optional;

import org.csps.backend.domain.dtos.request.AdminPostRequestDTO;
import org.csps.backend.domain.dtos.response.AdminResponseDTO;
import org.csps.backend.domain.entities.Admin;
import org.csps.backend.domain.entities.Student;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.domain.entities.UserProfile;
import org.csps.backend.domain.enums.AdminPosition;
import org.csps.backend.domain.enums.UserRole;
import org.csps.backend.exception.AdminNotFoundException;
import org.csps.backend.exception.PositionAlreadyTakenException;
import org.csps.backend.exception.StudentNotFoundException;
import org.csps.backend.mapper.AdminMapper;
import org.csps.backend.repository.AdminRepository;
import org.csps.backend.repository.StudentRepository;
import org.csps.backend.repository.UserAccountRepository;
import org.csps.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;


    @Value("${csps.adminUserFormat}")
    private String adminUserFormat;
    @Value("${csps.adminPasswordFormat}")
    private String adminPasswordFormat;

    @Override
    public Optional<Admin> findByAccountId(Long accountId) {
        return adminRepository.findByUserAccountUserAccountId(accountId);
    }

    @Override
    public Optional<AdminResponseDTO> findById(Long id) {
        return adminRepository.findById(id).map(adminMapper::toResponseDTO);
    }

    @Override
    public AdminResponseDTO createAdmin(AdminPostRequestDTO adminPostRequestDTO) {

        boolean isDeveloper = adminPostRequestDTO.getPosition() == AdminPosition.DEVELOPER;
        boolean isPositionTaken = adminRepository.existsByPosition(adminPostRequestDTO.getPosition());

        // load Student
        Student student = studentRepository.findById(adminPostRequestDTO.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        

        // Load Profile through Student
        UserProfile userProfile = student.getUserAccount().getUserProfile();

        if (!isDeveloper && isPositionTaken) {
            throw new PositionAlreadyTakenException("Position already taken: " + adminPostRequestDTO.getPosition());
        }

        UserAccount userAccount = UserAccount.builder()
                .userProfile(userProfile)
                .role(UserRole.ADMIN)
                .username(String.format("%s-%s%s%s%s",
                        adminUserFormat,
                        adminPostRequestDTO.getPosition(),
                        userProfile.getFirstName(),
                        userProfile.getLastName(),
                        userProfile.getUserId()))
                .password(String.format("%s-%s%s",
                        adminPasswordFormat,
                        userProfile.getLastName(),
                        userProfile.getUserId()))
                .build();

        userAccount = userAccountRepository.save(userAccount);

        // Create Admin linked to the new account
        Admin admin = adminMapper.toEntity(adminPostRequestDTO);
        admin.setUserAccount(userAccount);

        admin = adminRepository.save(admin);

        return adminMapper.toResponseDTO(admin);
    }

    @Override
    public AdminResponseDTO deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found"));
        adminRepository.delete(admin);
        return adminMapper.toResponseDTO(admin);
    }



}

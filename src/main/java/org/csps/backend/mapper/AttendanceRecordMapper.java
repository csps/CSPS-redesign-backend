package org.csps.backend.mapper;

import java.util.List;

import org.csps.backend.domain.dtos.response.AttendanceRecordResponseDTO;
import org.csps.backend.domain.entities.AttendanceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceRecordMapper {
    
    @Mapping(source = "record.attendanceId", target = "attendanceId")
    @Mapping(source = "record.eventParticipant.participantId", target = "participantId")
    @Mapping(source = "record.eventParticipant.student.studentId", target = "studentId")
    @Mapping(source = "record.eventParticipant.student.userAccount.userProfile.firstName", target = "studentName")
    @Mapping(source = "record.eventSession.sessionId", target = "sessionId")
    @Mapping(source = "record.eventSession.sessionName", target = "sessionName")
    @Mapping(source = "record.checkedInAt", target = "checkedInAt")
    AttendanceRecordResponseDTO toResponseDTO(AttendanceRecord record);
    
    List<AttendanceRecordResponseDTO> toResponseDTOList(List<AttendanceRecord> records);
}

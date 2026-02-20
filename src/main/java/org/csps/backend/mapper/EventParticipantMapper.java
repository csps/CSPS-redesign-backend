package org.csps.backend.mapper;

import java.util.List;

import org.csps.backend.domain.dtos.response.EventParticipantResponseDTO;
import org.csps.backend.domain.entities.EventParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventParticipantMapper {
    
    @Mapping(source = "participant.participantId", target = "participantId")
    @Mapping(source = "participant.student.studentId", target = "studentId")
    @Mapping(source = "participant.student.userAccount.userProfile.firstName", target = "studentName")
    @Mapping(source = "participant.participationStatus", target = "participationStatus")
    @Mapping(source = "participant.joinedDate", target = "joinedDate")
    EventParticipantResponseDTO toResponseDTO(EventParticipant participant);
    
    List<EventParticipantResponseDTO> toResponseDTOList(List<EventParticipant> participants);
}

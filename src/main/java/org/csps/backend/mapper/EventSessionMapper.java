package org.csps.backend.mapper;

import java.util.List;

import org.csps.backend.domain.dtos.response.EventSessionResponseDTO;
import org.csps.backend.domain.entities.EventSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventSessionMapper {
    
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "sessionName", target = "sessionName")
    @Mapping(source = "sessionDate", target = "sessionDate")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    @Mapping(source = "sessionStatus", target = "sessionStatus")
    @Mapping(source = "qrTokenCode", target = "qrTokenCode")
    EventSessionResponseDTO toResponseDTO(EventSession session);
    
    List<EventSessionResponseDTO> toResponseDTOList(List<EventSession> sessions);
}

package org.csps.backend.service;

import java.util.List;

import org.csps.backend.domain.dtos.response.AnnouncementResponseDTO;

public interface AnnouncementService {
    List<AnnouncementResponseDTO> getAllAnnouncements();
}

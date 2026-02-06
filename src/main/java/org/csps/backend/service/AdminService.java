package org.csps.backend.service;

import java.util.Optional;

import org.csps.backend.domain.dtos.request.AdminPostRequestDTO;
import org.csps.backend.domain.dtos.request.AdminUnsecureRequestDTO;
import org.csps.backend.domain.dtos.response.AdminResponseDTO;
import org.csps.backend.domain.entities.Admin;

public interface AdminService {
    Optional<Admin> findByAccountId(Long accountId);
    Optional<AdminResponseDTO> findById (Long Id);
    AdminResponseDTO createAdmin(AdminPostRequestDTO adminPostRequestDTO);
    AdminResponseDTO createAdminUnsecure(AdminUnsecureRequestDTO adminUnsecureRequestDTO);
    AdminResponseDTO deleteAdmin(Long adminId);


}

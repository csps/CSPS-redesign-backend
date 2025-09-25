package org.csps.backend.service.impl;

import java.util.Optional;

import org.csps.backend.domain.dtos.response.AdminResponseDTO;
import org.csps.backend.domain.entities.Admin;
import org.csps.backend.mapper.AdminMapper;
import org.csps.backend.repository.AdminRepository;
import org.csps.backend.service.AdminService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    @Override
    public Optional<Admin> findByAccountId(Long accountId) {
        return adminRepository.findByUserAccountUserAccountId(accountId);
    }

    @Override
    public Optional<AdminResponseDTO> findById(Long id) {
        return adminRepository.findById(id).map(adminMapper::toResponseDTO);
    }
}

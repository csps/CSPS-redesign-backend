package org.csps.backend.service;

import org.csps.backend.domain.dtos.response.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCartByStudentId(String studentId);
}

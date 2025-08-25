package org.csps.backend.service;

import org.csps.backend.domain.dtos.request.CreateUserDTO;
import org.csps.backend.domain.dtos.request.UserPatchDTO;
import org.csps.backend.domain.dtos.request.UserRequestDTO;
import org.csps.backend.domain.dtos.response.UserResponseDTO;
import org.csps.backend.domain.entities.User;

import java.util.List;

public interface UserService {
    User createUser(UserRequestDTO userRequestDTO);   // registration
    UserResponseDTO getUserById(Long userId);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO patchUser(Long userId, UserPatchDTO userPatchDTO);
    UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO);
    void deleteUser(Long userId);
}

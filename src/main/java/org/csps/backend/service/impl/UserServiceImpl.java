package org.csps.backend.service.impl;

import org.csps.backend.domain.dtos.request.CreateUserDTO;
import org.csps.backend.domain.dtos.request.UserPatchDTO;
import org.csps.backend.domain.dtos.request.UserRequestDTO;
import org.csps.backend.domain.dtos.response.UserResponseDTO;
import org.csps.backend.domain.entities.User;
import org.csps.backend.exception.UserAlreadyExistsException;
import org.csps.backend.exception.UserNotFoundException;
import org.csps.backend.mapper.UserMapper;
import org.csps.backend.repository.UserRespository;
import org.csps.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRespository userRespository;

    public UserServiceImpl(UserMapper userMapper, UserRespository userRespository) {
        this.userMapper = userMapper;
        this.userRespository = userRespository;
    }

    @Override
    public User createUser(UserRequestDTO userRequestDTO) {
        Long userId = userRequestDTO.getUserId();
        if (userRespository.existsById(userId)) {
            throw new UserAlreadyExistsException(userId);
        }
        User user = userMapper.toEntity(userRequestDTO);
        return userRespository.save(user);
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User existingUser = userRespository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userMapper.toResponseDTO(existingUser);
    }

    // TODO consider pagination
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRespository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO patchUser(Long userId, UserPatchDTO userPatchDTO) {
        User existingUser = userRespository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userMapper.updateEntityFromDto(userPatchDTO, existingUser);
        User patchedUser = userRespository.save(existingUser);
        return userMapper.toResponseDTO(patchedUser);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO) {
        User existingUser = userRespository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userMapper.updateEntityFromDto(userRequestDTO, existingUser);
        User updatedUser = userRespository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }


    @Override
    public void deleteUser(Long userId) {
        if (!userRespository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRespository.deleteById(userId);
    }
}

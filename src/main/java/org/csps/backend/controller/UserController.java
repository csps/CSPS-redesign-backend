package org.csps.backend.controller;

import java.util.List;

import org.csps.backend.domain.dtos.request.UpdateEmailSimpleRequestDTO;
import org.csps.backend.domain.dtos.response.GlobalResponseBuilder;
import org.csps.backend.domain.dtos.response.UserResponseDTO;
import org.csps.backend.mapper.UserMapper;
import org.csps.backend.service.UserAccountService;
import org.csps.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserAccountService userAccountService;

    private final UserMapper userMapper;





    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser() {
        // get all users
        List<UserResponseDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }


    @PatchMapping("/update-email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<GlobalResponseBuilder<String>> updateEmail(Authentication authentication, @Valid @RequestBody UpdateEmailSimpleRequestDTO request) {
        // get current user id from security context
        Long userId = (Long) authentication.getCredentials();
    
        userAccountService.updateUserEmail(userId, request.getEmail());
        
        return GlobalResponseBuilder.buildResponse("Email updated successfully", request.getEmail(), HttpStatus.OK);
    }

    
}

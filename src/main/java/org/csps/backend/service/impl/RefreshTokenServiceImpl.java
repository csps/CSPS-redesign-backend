package org.csps.backend.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.csps.backend.domain.dtos.request.SignInCredentialRequestDTO;
import org.csps.backend.domain.entities.RefreshToken;
import org.csps.backend.domain.entities.UserAccount;
import org.csps.backend.repository.RefreshTokenRepository;
import org.csps.backend.security.JwtService;
import org.csps.backend.service.RefreshTokenService;
import org.csps.backend.service.UserAccountService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service
@Builder
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final UserAccountService userAccountService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        UserAccount user = userAccountService.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user already has a refresh token
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserAccount(user);

        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            refreshToken = existingToken.get();
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate();
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUserAccount(user);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate();
        }

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByRefreshToken (String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    @Override
    public boolean isValidRefreshToken(RefreshToken token) {
        return token.getExpiryDate().isAfter(Instant.now());
    }

    @Override
    public void deleteByUserId (Long userId) {
        UserAccount user = userAccountService.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        refreshTokenRepository.deleteByUserAccount(user);
    }

    @Override
    public Optional<String> refreshAccessToken(String refreshTokenValue) {
        return refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .map(token -> {
                    if (!isValidRefreshToken(token)) {
                        deleteRefreshToken(token);
                        return null; 
                    }
                    UserAccount user = token.getUserAccount();
                    return jwtService.generateAccessToken(
                            new SignInCredentialRequestDTO(user.getUsername(), user.getPassword())
                    );
                });
    }

	@Override
	public void deleteRefreshToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
	}


}

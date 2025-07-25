package org.atechtrade.rent.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.atechtrade.rent.exception.EntityNotFoundException;
import org.atechtrade.rent.model.RefreshToken;
import org.atechtrade.rent.model.User;
import org.atechtrade.rent.repository.ContractorRepository;
import org.atechtrade.rent.repository.RefreshTokenRepository;
import org.atechtrade.rent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RefreshTokenService {

	@Value("${jwt.refresh-exp}")
	private Long refreshTokenDurationMs;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final ContractorRepository contractorRepository;

	public RefreshToken findByRefreshToken(final String token) {
		return refreshTokenRepository.findByRefreshToken(token);
	}

	public RefreshToken createRefreshToken(final Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, userId));

		return refreshTokenRepository.save(
				RefreshToken.builder()
						.user(user)
						.expDate(Instant.now().plusMillis(refreshTokenDurationMs))
						.refreshToken(UUID.randomUUID().toString()).build()
		);
	}

	@Transactional
	public RefreshToken verifyExpiration(final RefreshToken token) {
		if (token.getExpDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			return null;
		}

		return token;
	}

	@Transactional
	public Integer deleteByUser(final User user) {
		return refreshTokenRepository.deleteByUser(user);
	}

	@Transactional
	public void deleteByUserId(final Long id, final int type) {
		if (type == 0) {
			userRepository.findById(id).ifPresent(refreshTokenRepository::deleteByUser);
		} else {
			contractorRepository.findById(id).ifPresent(refreshTokenRepository::deleteByContractor);
		}
	}
	
}

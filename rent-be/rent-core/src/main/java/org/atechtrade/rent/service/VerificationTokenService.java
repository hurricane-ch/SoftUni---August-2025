package org.atechtrade.rent.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.exception.EntityNotFoundException;
import org.atechtrade.rent.model.VerificationToken;
import org.atechtrade.rent.repository.VerificationTokenRepository;
import org.atechtrade.rent.util.CommonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VerificationTokenService {

    private final VerificationTokenRepository repository;

    @Transactional(readOnly = true)
    public VerificationToken findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(VerificationToken.class, id));
    }

    public VerificationToken findByToken(final String token) {
        return repository.findByToken(token).orElseThrow(() -> new EntityNotFoundException(VerificationToken.class, token));
    }

    public VerificationToken findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    public VerificationToken findByEmail(final String email) {
        return repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(VerificationToken.class, email));
    }

    public VerificationToken findByUsernameAndEmail(final  String username, final String email) {
        return repository.findByUsernameAndEmail(username, email).orElseThrow(() -> new EntityNotFoundException(VerificationToken.class, email));
    }

    public VerificationToken findByUsernameAndEmailOrNull(final  String username, final String email) {
        return repository.findByUsernameAndEmail(username, email).orElse(null);
    }

    public VerificationToken findByEmailOrNull(final String email) {
        return StringUtils.hasText(email) ? repository.findByEmail(email).orElse(null) : null;
    }

    public boolean existsByEmail(final String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByIdentifier(final String identifier) {
        return repository.existsByIdentifier(identifier);
    }

    public boolean existsByUsernameIgnoreCase(final String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    public VerificationToken save(final VerificationToken verificationToken) {
        return repository.save(verificationToken);
    }

    public void delete(final VerificationToken verificationToken) {
        repository.delete(verificationToken);
    }

    @Transactional
    public VerificationToken createOrUpdateEmailVerificationCode(final String email, final String phone, final String fullName) {
        String randomNumber = String.valueOf(100_000 + new SecureRandom().nextInt(900_000));
        VerificationToken verificationToken = findByEmailOrNull(email);
        if (verificationToken == null) {
            verificationToken = VerificationToken.builder()
                    .username(email)
                    .fullName(fullName)
                    .phone(phone)
                    .email(email)
                    .build();
        }
        verificationToken.setToken(randomNumber);
        verificationToken.setExpDate(CommonUtils.calculateExpiryDate(VerificationToken.EXPIRATION_MILLIS));

        return repository.save(verificationToken);
    }
}

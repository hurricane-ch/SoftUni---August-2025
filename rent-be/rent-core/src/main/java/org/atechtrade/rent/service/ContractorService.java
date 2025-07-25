package org.atechtrade.rent.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.dto.ContractorDTO;
import org.atechtrade.rent.dto.RevisionMetadataDTO;
import org.atechtrade.rent.dto.SimpleSignUpRequest;
import org.atechtrade.rent.exception.EntityAlreadyExistException;
import org.atechtrade.rent.exception.EntityNotFoundException;
import org.atechtrade.rent.exception.UserAlreadyExistAuthenticationException;
import org.atechtrade.rent.model.Contractor;
import org.atechtrade.rent.model.Role;
import org.atechtrade.rent.model.VerificationToken;
import org.atechtrade.rent.repository.ContractorRepository;
import org.atechtrade.rent.repository.RoleRepository;
import org.atechtrade.rent.util.CommonUtils;
import org.atechtrade.rent.util.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ContractorService {

    private final ContractorRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

    @Transactional(readOnly = true)
    public Contractor findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Contractor.class, id));
    }

    @Transactional(readOnly = true)
    public ContractorDTO findDTOById(final Long id, final String language) {
        return ContractorDTO.of(findById(id), language);
    }

    @Transactional(readOnly = true)
    public Contractor findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<Contractor> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ContractorDTO> findAllDTO(final Pageable pageable, final String language) {
        return repository.findAll(pageable).map(c -> ContractorDTO.of(c, language));
    }

    @Transactional(readOnly = true)
    public Contractor findByUsername(final String username) {
        return repository.findByUsernameIgnoreCase(
                username.toLowerCase()).orElseThrow(() -> new EntityNotFoundException(Contractor.class, username)
        );
    }

    public Contractor findByUsernameOrNull(final String username) {
        return repository.findByUsernameIgnoreCase(username).orElse(null);
    }

    public Contractor findByEmail(final String email) {
        return repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(Contractor.class, email));
    }

    public Contractor findByEmailOrNull(final String email) {
        return StringUtils.hasText(email) ? repository.findByEmail(email).orElse(null) : null;
    }

    public Contractor findByIdentifier(final String identifier) {
        return repository.findByIdentifier(identifier).orElseThrow(() -> new EntityNotFoundException(Contractor.class, identifier));
    }

    public Contractor findByIdentifierOrNull(final String identifier) {
        return repository.findByIdentifier(identifier).orElse(null);
    }

    @Transactional
    public void register(final VerificationToken verificationToken, final String language) throws UserAlreadyExistAuthenticationException {
        if (verificationToken.getId() != null && repository.existsById(verificationToken.getId())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with id " + verificationToken.getId() + " already exist");
        } else if (repository.existsByIdentifier(verificationToken.getIdentifier()) ||
                verificationTokenService.existsByIdentifier(verificationToken.getIdentifier())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with identifier " + verificationToken.getIdentifier() + " already exist");
        } else if (repository.existsByEmail(verificationToken.getEmail()) ||
                verificationTokenService.existsByEmail(verificationToken.getEmail())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with email " + verificationToken.getEmail() + " already exist");
        } else if (repository.existsByUsernameIgnoreCase(verificationToken.getUsername()) ||
                verificationTokenService.existsByUsernameIgnoreCase(verificationToken.getUsername())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with username " + verificationToken.getUsername() + " already exist");
        }

        VerificationToken savedVerificationToken = verificationTokenService.save(
                VerificationToken.builder()
                        .identifier(verificationToken.getIdentifier())
                        .username(verificationToken.getUsername())
                        .password(passwordEncoder.encode(verificationToken.getPassword()))
                        .email(verificationToken.getEmail())
                        .fullName(verificationToken.getFullName())
                        .token(UUID.randomUUID().toString())
                        .expDate(CommonUtils.calculateExpiryDate(VerificationToken.EXPIRATION_MILLIS))
                        .build()
        );
//        mailService.sendRegisterUserVerificationToken(savedVerificationToken, language);
    }

    @Transactional
    public void createOrUpdateEmailVerificationCode(final String email, final String phone, final String fullName, final String language) {
        VerificationToken verificationToken = verificationTokenService
                .createOrUpdateEmailVerificationCode(email, phone, fullName);

//        mailService.sendEmailVerificationCode(verificationToken, language);
    }

    @Transactional
    public String confirmEmailVerificationCode(final SimpleSignUpRequest request) {
        final VerificationToken verificationToken = verificationTokenService.findByEmail(request.getEmail());

        if (!verificationToken.getToken().equals(request.getToken())) {
            return Constants.TOKEN_INVALID;
        }
//        final Calendar cal = Calendar.getInstance();
//        if ((verificationToken.getExpDate().getTime() - cal.getTime().getTime()) <= 0) {
//            return Constants.TOKEN_EXPIRED;
//        }
        if (verificationToken.getExpDate().toInstant().isBefore(Instant.now())) {
            return Constants.TOKEN_EXPIRED;
        }

        Contractor contractor = findByEmailOrNull(request.getEmail());
        if (contractor == null) {
            contractor = new Contractor();
            BeanUtils.copyProperties(verificationToken, contractor, "id");
            contractor.setMarketingTarget(true);
        }

        if (StringUtils.hasText(request.getPhone())) {
            contractor.setPhone(contractor.getPhone() + " | " + request.getPhone());
        }

        contractor.setEnabled(true);

        repository.save(contractor);

        return Constants.TOKEN_VALID;
    }

    @Transactional
    public boolean resendVerificationToken(final String existingVerificationToken, final String language) {
        VerificationToken vToken = verificationTokenService.findByToken(existingVerificationToken);
        if (vToken != null) {
            vToken.updateToken(UUID.randomUUID().toString());
//            mailService.sendRegisterUserVerificationToken(verificationTokenService.save(vToken), language);
            return true;
        }
        return false;
    }

    @Transactional
    public String validateRegisterContractorToken(final String token) {
        final VerificationToken verificationToken = verificationTokenService.findByToken(token);
        if (verificationToken == null) {
            return Constants.TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpDate().getTime() - cal.getTime().getTime()) <= 0) {
            return Constants.TOKEN_EXPIRED;
        }
        repository.save(
                Contractor.builder()
                        .identifier(verificationToken.getIdentifier())
                        .username(verificationToken.getUsername())
                        .password(verificationToken.getPassword())
                        .fullName(verificationToken.getFullName())
                        .email(verificationToken.getEmail())
                        .enabled(true)
                        .build()
        );
        verificationTokenService.delete(verificationToken);
        return Constants.TOKEN_VALID;
    }

    @Transactional
    public VerificationToken forgotPassword(final String email, final String language) {
        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email is required");
        }
        Contractor user = findByEmail(email);
        VerificationToken token = verificationTokenService.findByUsernameAndEmail(user.getUsername(), user.getEmail());
        final Calendar cal = Calendar.getInstance();
        if (token == null || (token.getExpDate().getTime() - cal.getTime().getTime()) <= 0) {
            // TOKEN_EXPIRED;
            token = verificationTokenService.save(
                    VerificationToken.builder()
                            .identifier(user.getIdentifier())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .token(UUID.randomUUID().toString())
                            .expDate(CommonUtils.calculateExpiryDate(VerificationToken.EXPIRATION_MILLIS))
                            .build()
            );
//            mailService.sendForgotPasswordVerificationToken(token, language);
        }

        return token;
    }

    @Transactional
    public String validateForgotPasswordToken(final String token, final String password) {
        final VerificationToken verificationToken = verificationTokenService.findByToken(token);
        if (verificationToken == null) {
            return Constants.TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpDate().getTime() - cal.getTime().getTime()) <= 0) {
            return Constants.TOKEN_EXPIRED;
        }
        Contractor user = findByUsername(verificationToken.getUsername());
        user.setPassword(passwordEncoder.encode(password));

        repository.save(user);
        verificationTokenService.delete(verificationToken);

        return Constants.TOKEN_VALID;
    }

    @Transactional
    public Contractor create(final Contractor contractor) {
        if (StringUtils.hasText(contractor.getUsername()) && findByUsernameOrNull(contractor.getUsername()) != null) {
            throw new EntityAlreadyExistException(Contractor.class, contractor.getUsername());
        }
        contractor.setPassword(StringUtils.hasText(contractor.getPassword())
                ? passwordEncoder.encode(contractor.getPassword())
                : null);

        if (!CollectionUtils.isEmpty(contractor.getRoles())) {
            Set<Role> roles = new HashSet<>();
            contractor.getRoles().forEach(r -> roles.add(roleRepository.findByName(r.getName())));
            contractor.setRoles(roles);
        }

        return repository.save(contractor);
    }

    @Transactional
    public ContractorDTO update(final Long id, final Contractor contractor, final String language) {
        if (id == null && contractor.getId() == null) {
            throw new RuntimeException("User ID is required");
        }
        Contractor current = findById(id != null && id >= 0 ? id : contractor.getId());

        current.setFullName(contractor.getFullName());
        current.setIdentifier(contractor.getIdentifier());
        current.setUsername(contractor.getUsername());
        current.setEnabled(contractor.getEnabled());

        current.getRoles().clear();
        contractor.getRoles().forEach(r -> current.getRoles().add(roleRepository.findByName(r.getName())));

        return ContractorDTO.of(repository.save(current), language);
    }

    @Transactional
    public Contractor updateRoles(final Long id, final List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            throw new RuntimeException("At least one role is required");
        }
        Contractor contractor = findById(id);
        contractor.getRoles().clear();
        roles.forEach(r -> contractor.getRoles().add(roleRepository.findByName(r)));

        return repository.save(contractor);
    }

    @Transactional(readOnly = true)
    public Page<ContractorDTO> search(final String param, final Pageable pageable, final String language) {
        return repository.search(param, pageable).map(c -> ContractorDTO.of(c, language));
    }

    @Transactional
    public Contractor setEnabled(final Long id, final Boolean enabled) {
        if (enabled == null) {
            throw new RuntimeException("Boolean param enabled is required");
        }
        Contractor contractor = findById(id);
        contractor.setEnabled(enabled);
        return repository.save(contractor);
    }

    @Transactional(readOnly = true)
    public List<ContractorDTO> findRevisions(final Long id, final String language) {
        List<ContractorDTO> revisions = new ArrayList<>();
        repository.findRevisions(id).get().forEach(r -> {
            ContractorDTO rev = ContractorDTO.of(r.getEntity(), language);
            rev.setRevisionMetadata(RevisionMetadataDTO.builder()
                    .revisionNumber(r.getMetadata().getRevisionNumber().orElse(null))
                    .revisionInstant(r.getMetadata().getRevisionInstant().orElse(null))
                    .revisionType(r.getMetadata().getRevisionType().name())
                    .createdBy(r.getEntity().getCreatedBy())
                    .createdDate(r.getEntity().getCreatedDate())
                    .lastModifiedBy(r.getEntity().getLastModifiedBy())
                    .lastModifiedDate(r.getEntity().getLastModifiedDate())
                    .build()
            );
            revisions.add(rev);
        });
//		Collections.reverse(revisions);
        return revisions;
    }
}

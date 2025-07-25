package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.VerificationToken;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Hidden
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByIdentifier(String identifier);
	boolean existsByEmail(String email);

	Optional<VerificationToken> findByToken(String token);
	Optional<VerificationToken> findByEmail(String email);
	Optional<VerificationToken> findByUsernameAndEmail(String Username, String email);

}

package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.Contractor;
import org.atechtrade.rent.model.RefreshToken;
import org.atechtrade.rent.model.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	RefreshToken findByRefreshToken(String token);

	@Modifying
	int deleteByUser(final User user);

	@Modifying
	int deleteByContractor(final Contractor contractor);

}

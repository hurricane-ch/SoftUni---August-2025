package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface UserRepository extends JpaRepository<User, Long>, RevisionRepository<User, Long, Long> {
	
	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByEmail(String email);
	
	Optional<User> findByUsernameIgnoreCase(String username);
	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u "
			+ "WHERE lower(u.username) like lower(concat('%', :param, '%')) "
			+ "OR lower(u.email) like lower(concat('%', :param, '%')) "
			+ "OR lower(u.fullName) like lower(concat('%', :param, '%')) "
			+ "OR lower(u.identifier) like lower(concat('%', :param, '%')) "
	)
	List<User> search(@Param("param") final String param);

	@Query("SELECT u FROM User u " +
			"WHERE (:username IS NOT NULL AND u.username = :username)" +
			"OR (:email IS NOT NULL AND u.email = :email)" +
			"OR (:identifier IS NOT NULL AND u.identifier = :identifier)"
	)
	User findByUsernameOrEmailOrIdentifier(
			@Param("username") String username
			,@Param("email") String email
			,@Param("identifier") String identifier);
}
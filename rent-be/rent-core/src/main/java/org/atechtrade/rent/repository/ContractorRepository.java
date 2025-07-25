package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.Contractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Long>, RevisionRepository<Contractor, Long, Long> {

	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByIdentifier(String identifier);
	boolean existsByEmail(String email);
	Optional<Contractor> findByUsernameIgnoreCase(String username);
	Optional<Contractor> findByEmail(String email);
	Optional<Contractor> findByIdentifier(String identifier);

	@Query("SELECT c FROM Contractor c "
			+ "WHERE lower(c.username) like lower(concat('%', :param, '%')) "
			+ "OR lower(c.email) like lower(concat('%', :param, '%')) "
			+ "OR lower(c.fullName) like lower(concat('%', :param, '%'))"
			+ "OR lower(c.identifier) like lower(concat('%', :param, '%'))"
	)
	Page<Contractor> search(@Param("param") final String param, final Pageable pageable);
}

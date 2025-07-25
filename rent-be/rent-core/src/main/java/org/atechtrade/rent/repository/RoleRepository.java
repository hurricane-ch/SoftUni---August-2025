package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.Role;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, RevisionRepository<Role, Long, Long> {

	Role findByName(String role);

	boolean existsByName(String name);
}

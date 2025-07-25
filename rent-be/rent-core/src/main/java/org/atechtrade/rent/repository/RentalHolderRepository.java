package org.atechtrade.rent.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.atechtrade.rent.model.RentalHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Hidden
@Repository
public interface RentalHolderRepository extends JpaRepository<RentalHolder, Long> {
}

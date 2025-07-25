package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.GeneratorSequence;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface GeneratorSequenceRepository extends JpaRepository<GeneratorSequence, Long> {

    @Query(value = "SELECT nextval('rentch.gen_sequence')", nativeQuery = true)
    Long nextVal();

}

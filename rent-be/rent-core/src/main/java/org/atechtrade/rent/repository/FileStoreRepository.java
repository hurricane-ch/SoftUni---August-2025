package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.File;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface FileStoreRepository extends JpaRepository<File, Long> {
}

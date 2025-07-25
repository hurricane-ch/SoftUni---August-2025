package org.atechtrade.rent.repository;

import org.atechtrade.rent.model.Language;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface LanguageRepository extends JpaRepository<Language, String> {

    Optional <Language> findAllByEnabledIsTrueAndMainIsTrue();

	List<Language> findAllByMainIsTrue();

    List<Language> findAllByEnabledIsTrue();
}
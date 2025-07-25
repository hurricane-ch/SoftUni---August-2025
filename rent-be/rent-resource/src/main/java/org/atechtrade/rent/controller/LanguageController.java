package org.atechtrade.rent.controller;

import org.atechtrade.rent.dto.ApiListResponse;
import org.atechtrade.rent.dto.LanguageDTO;
import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.service.LanguageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/${rent.api.version}/langs")
@SecurityRequirement(name = "rentapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LanguageController {

	private final LanguageService service;

	@GetMapping
	@Secured({RolesConstants.ROLE_ADMIN})
	public List<Language> findAll() {
		return service.findAll();
	}

	@GetMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public LanguageDTO findById(@PathVariable final String id) {
		return LanguageDTO.of(service.findById(id));
	}

	@PostMapping
	@Secured({RolesConstants.ROLE_ADMIN})
	public LanguageDTO create(@RequestBody @Valid final LanguageDTO dto) {
		return LanguageDTO.of(service.create(LanguageDTO.to(dto)));
	}

	@PutMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public LanguageDTO update(@PathVariable final String id, @RequestBody @Valid final LanguageDTO dto) {
		return LanguageDTO.of(service.update(id, dto));
	}

}

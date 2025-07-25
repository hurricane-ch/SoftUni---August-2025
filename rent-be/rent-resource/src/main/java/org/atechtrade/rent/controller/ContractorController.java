package org.atechtrade.rent.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.dto.ContractorDTO;
import org.atechtrade.rent.dto.LogoutRequest;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.service.ContractorService;
import org.atechtrade.rent.service.RefreshTokenService;
import org.atechtrade.rent.util.CommonUtils;
import org.atechtrade.rent.util.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static java.text.MessageFormat.format;

@Slf4j
@RestController
@RequestMapping("/contractors")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ContractorController {
	private final ContractorService service;
	private final RefreshTokenService refreshTokenService;

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/")
	public Page<ContractorDTO> findAll(final Pageable pageable,
									   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return service.findAllDTO(pageable, language);
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/me")
	public ContractorDTO getCurrentUser(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String language = CommonUtils.getLanguage(locale);
		return ContractorDTO.of(service.findByUsername(currentUser.getUsername()), language);
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/username/{username}")
	public ContractorDTO findByUsername(@PathVariable final String username,
											@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return ContractorDTO.of(service.findByUsername(username), language);
	}

	@GetMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ContractorDTO findById(@PathVariable final Long id,
									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return service.findDTOById(id, language);
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(params = {"q"})
	public Page<ContractorDTO> search(@RequestParam(name = "q", required = false) final String param,
									  final Pageable pageable,
									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return service.search(param, pageable, language);
	}

	@PostMapping("/signout")
	public ApiResponse logout(@RequestBody final LogoutRequest request) {
		refreshTokenService.deleteByUserId(request.getId(), 1);
		return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.DELETE));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}/roles")
	public ContractorDTO roles(@PathVariable @NotNull final Long id,
								   @RequestBody @NotNull final List<String> roles,
								   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return ContractorDTO.of(service.updateRoles(id, roles), language);
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}")
	public ApiResponse update(@PathVariable @NotNull final Long id,
									@RequestBody final ContractorDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		if (dto.getId() != null && !id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		String language = CommonUtils.getLanguage(locale);
		return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PUT),
				service.update(id, ContractorDTO.to(dto), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}/{enabled}")
	public ResponseEntity<?> setEnabled(@PathVariable @NotNull final Long id,
										@PathVariable @NotNull final Boolean enabled,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return ResponseEntity.ok().body(ContractorDTO.of(service.setEnabled(id, enabled), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(path = "/{id}/history")
	public ResponseEntity<?> history(@PathVariable final Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return ResponseEntity.ok(service.findRevisions(id, language));
	}

//	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//	@GetMapping(path = "/{id}/facility-history")
//	public ResponseEntity<?> facilityHistory(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
//											 @PathVariable final Long id) {
//		String language = CommonUtils.getLanguage(locale);
//		return ResponseEntity.ok(service.findRevisionsFacility(id, language));
//	}
}

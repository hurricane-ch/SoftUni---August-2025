package org.atechtrade.rent.controller;

import org.atechtrade.rent.dto.ApiListResponse;
import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.dto.MessageResourceDTO;
import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.model.MessageResource;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.util.CommonUtils;
import org.atechtrade.rent.service.LanguageService;
import org.atechtrade.rent.service.MessageResourceService;
import org.atechtrade.rent.util.Constants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/${rent.api.version}/message-resources")
@SecurityRequirement(name = "rentapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MessageResourceController {

	private final MessageResourceService service;
	private final LanguageService languageService;

	@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, required = true, example = Language.BG)
	@GetMapping
	@Secured({RolesConstants.ROLE_ADMIN})
	public Page<MessageResourceDTO> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
											final Pageable pageable) {
		String language = CommonUtils.getLanguage(locale);
		return service.findDTOAll(language, pageable);
	}

	@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, required = true, example = Language.BG)
	@GetMapping(path = "/{code}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public MessageResourceDTO findByCode(@PathVariable final String code,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return MessageResourceDTO.of(
			service.findById(new MessageResource.MessageResourceIdentity(code, language))
		);
	}

	@GetMapping(path = "/{code}/all-languages")
	@Secured({RolesConstants.ROLE_ADMIN})
	public List<MessageResourceDTO> findAllByCode(@PathVariable final String code) {
		return MessageResourceDTO.of(service.findAllByCode(code), languageService.findAll());
	}

	@Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, required = true, example = Language.BG)
	@PutMapping(path = "/{code}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ApiResponse update(@PathVariable final String code,
									@RequestBody @Valid final MessageResourceDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		String language = CommonUtils.getLanguage(locale);
		return ApiResponse.ok(Constants.SUCCESSFULLY_UPDATED,
				service.update(new MessageResource.MessageResourceIdentity(code,
						language), dto).getMessageResourceIdentity());
	}

	@PutMapping
	@Secured({RolesConstants.ROLE_ADMIN})
	public ApiResponse update(@RequestBody @NotNull @Valid final List<MessageResourceDTO> msgResources) {
		for (MessageResourceDTO dto : msgResources) {
			service.updateOrCreate(MessageResource.MessageResourceIdentity.builder().code(dto.getCode()).languageId(dto.getLanguageId()).build(), dto);
		}
		return ApiResponse.ok("All message resources has been updated");
	}

}

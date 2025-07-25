package org.atechtrade.rent.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.dto.ReservationDTO;
import org.atechtrade.rent.enums.ReservationStatus;
import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.service.ReservationService;
import org.atechtrade.rent.util.CommonUtils;
import org.atechtrade.rent.util.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

@RestController
@RequestMapping("/api/${rent.api.version}/reservations")
@SecurityRequirement(name = "rentapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ReservationController {

    private final ReservationService service;

    @GetMapping("/{id}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ReservationDTO findById(@PathVariable final Long id) {
        return service.findDTOById(id);
    }

    @GetMapping("/rental-items/{rentalItemId}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public List<ReservationDTO> findAllByRentalHolderId(@PathVariable final Long rentalItemId) {
        return service.findAllByRentalItemId(rentalItemId);
    }

    @GetMapping("/search")
    @Secured(RolesConstants.ROLE_ADMIN)
    public List<ReservationDTO> findAllByStatusAndYear(
            @RequestParam(required = false) final ReservationStatus status,
            @RequestParam(required = false) final Integer year) {
        if (status == null && year == null) {
            throw new IllegalArgumentException("You must specify at least one status or year");
        }
        return service.findAllByStatusAndYear(status, year);
    }

    @PostMapping("/rental-items/{rentalItemId}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, required = true, example = Language.BG)
    public ApiResponse create(@PathVariable final Long rentalItemId,
                              @RequestBody @Valid @NonNull final ReservationDTO dto,
                              @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale
    ) {
        String language = CommonUtils.getLanguage(locale);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.POST), service.create(rentalItemId, dto, language));
    }

    @PutMapping("/{id}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse update(@PathVariable final Long id,
                              @RequestBody @Valid @NonNull final ReservationDTO dto) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PUT), service.update(id, dto));
    }

    @PatchMapping("/{id}/confirm")
    @Secured(RolesConstants.ROLE_ADMIN)
    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, required = true, example = Language.BG)
    public ApiResponse confirm(
            @PathVariable final Long id,
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale
    ) {
        String language = CommonUtils.getLanguage(locale);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PATCH), service.confirm(id, language));
    }

    @PatchMapping("/{id}/reject")
    @Secured(RolesConstants.ROLE_ADMIN)
    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, required = true, example = Language.BG)
    public ApiResponse reject(@PathVariable final Long id,
                              @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale
    ) {
        String language = CommonUtils.getLanguage(locale);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PATCH), service.reject(id, language));
    }

    @PatchMapping("/{id}/set-paid")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse setPaid(@PathVariable final Long id,
                               @RequestParam final Double paid) {
        service.setPaid(id, paid);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PATCH), id);
    }

}

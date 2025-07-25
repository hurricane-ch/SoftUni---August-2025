package org.atechtrade.rent.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.dto.RentalHolderDTO;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.service.RentalHolderService;
import org.atechtrade.rent.util.Constants;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.text.MessageFormat.format;

@RestController
@RequestMapping("/api/${rent.api.version}/rental-holders")
@SecurityRequirement(name = "rentapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RentalHolderController {

    private final RentalHolderService service;

    @GetMapping("/{id}")
    public RentalHolderDTO findById(@PathVariable final Long id) {
        return service.findDTOById(id);
    }

    @GetMapping
    @Secured(RolesConstants.ROLE_ADMIN)
    public List<RentalHolderDTO> findAll() {
        return service.findAllDTO();
    }

    @PostMapping
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse create(@RequestBody @Valid @NonNull final RentalHolderDTO dto) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.POST), service.create(dto));
    }

    @PutMapping("/{id}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse update(@PathVariable final Long id,
                              @RequestBody @Valid @NonNull final RentalHolderDTO dto) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PUT), service.update(id, dto));
    }

    @PatchMapping("/{id}/attach")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ApiResponse attach(
            @PathVariable final Long id,
            @RequestParam(required = false) final String description,
            @RequestParam final boolean main,
            @NotNull @RequestParam("file") final MultipartFile file
    ) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PATCH),
                service.attach(id, description, file, main));
    }

    @DeleteMapping("/{id}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse delete(@PathVariable final Long id) {
        service.delete(id);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.DELETE));
    }
}

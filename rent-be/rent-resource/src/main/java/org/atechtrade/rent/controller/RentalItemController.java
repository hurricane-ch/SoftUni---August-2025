package org.atechtrade.rent.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.dto.FileDTO;
import org.atechtrade.rent.dto.RentalItemDTO;
import org.atechtrade.rent.enums.ReservationStatus;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.service.RentalItemService;
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
@RequestMapping("/api/${rent.api.version}/rental-items")
@SecurityRequirement(name = "rentapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RentalItemController {

    private final RentalItemService service;

    @GetMapping("/{id}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public RentalItemDTO findById(@PathVariable final Long id) {
        return service.findDTOById(id);
    }

    @GetMapping
    @Secured(RolesConstants.ROLE_ADMIN)
    public List<RentalItemDTO> findAllOrderById() {
        return service.findAllSortedById();
    }

    @GetMapping("/reserved/{year}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public List<RentalItemDTO> findAllConfirmedByReservationStatusAndYear(@PathVariable final Integer year) {
        return service.findAllByStatusAndYear(ReservationStatus.CONFIRMED, year);
    }

    @PostMapping
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse create(@RequestBody @Valid @NonNull final RentalItemDTO dto) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.POST), service.create(dto));
    }

    @PutMapping("/{id}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse update(@PathVariable final Long id,
                              @RequestBody @Valid @NonNull final RentalItemDTO dto) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PUT), service.update(id, dto));
    }

//    @PutMapping("/{id}/attach")
//    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//    public ApiResponse attach(
//            @PathVariable final Long id,
//            @RequestParam(required = false) final String description,
//            @RequestParam (required = false) final boolean main,
//            @Valid @NotNull @RequestParam(value = "file") final MultipartFile multipartFile
//    ) {
//        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PUT),
//                service.attach(id, description, multipartFile, main));
//    }

    @PatchMapping("/{id}/disable")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse disable(@PathVariable final Long id) {
        service.disable(id);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PATCH));
    }

    @PatchMapping("/{id}/enable")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse enable(@PathVariable final Long id) {
        service.enable(id);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PATCH));
    }

    @DeleteMapping("/{id}")
    @Secured(RolesConstants.ROLE_ADMIN)
    public ApiResponse delete(@PathVariable final Long id) {
        service.delete(id);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.DELETE));
    }


    //==========================================================================
    @PostMapping("/{id}/files")
    public ApiResponse uploadFile(
            @PathVariable @NotNull final Long id,
            @NotNull @RequestParam("description") final String description,
            @NotNull @RequestParam("file") final MultipartFile file,
            @NotNull @RequestParam("main") final boolean main
    ) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.POST), service.uploadFile(id, description, file, main));
    }

    @DeleteMapping("/{id}/files/{fileId}")
    public ApiResponse deleteFile(@PathVariable @NotNull final Long id, @PathVariable @NotNull final Long fileId) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.DELETE), service.deleteFile(id, fileId));
    }

    @GetMapping("/{id}/files")
    public List<FileDTO> findAllFile(@PathVariable @NotNull final Long id) {
        return service.findAllFiles(id);
    }

    @PatchMapping("/{id}/files/{fileId}")
    public ApiResponse setMain(@PathVariable @NotNull final Long id,
                               @PathVariable @NotNull final Long fileId,
                               @RequestParam @NotNull final boolean main) {
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PATCH), service.setMain(id, fileId, main));
    }
}

package org.atechtrade.rent.controller;

import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.dto.LogoutRequest;
import org.atechtrade.rent.dto.UserDTO;
import org.atechtrade.rent.dto.common.UserBaseDTO;
import org.atechtrade.rent.model.Role;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.service.RefreshTokenService;
import org.atechtrade.rent.service.UserService;
import org.atechtrade.rent.util.Constants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/${rent.api.version}/users")
@SecurityRequirement(name = "rentapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    //	@RolesAllowed({"ADMIN"})
    @GetMapping
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public Page<UserDTO> findAll(final Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping(path = "/search", params = {"q"})
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public List<UserBaseDTO> search(@RequestParam(name = "q", required = false) final String param) {
        return userService.search(param);
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser() {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserDTO.of(userService.findByUsername(currentUser.getUsername()));
    }

    @GetMapping("/username/{username}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public UserBaseDTO findByUsername(@PathVariable final String username) {
        return UserBaseDTO.of(userService.findByUsername(username));
    }

    @GetMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public UserBaseDTO findById(@PathVariable final Long id) {
        return userService.findDTOById(id);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestBody final LogoutRequest request) {
        Integer affectedRecords = userService.signout(request.getId());
        return ResponseEntity.ok().body(affectedRecords != null && affectedRecords > 0
                ? "User signed out"
                : "Error during signing out");
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @GetMapping("/roles")
    public List<String> findAllRoles() {
        return userService.roles().stream().map(Role::getName).toList();
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @PutMapping("/{id}/roles")
    public ApiResponse roles(@PathVariable final Long id, @RequestBody @NotNull final List<String> roles) {
        return ApiResponse.ok(Constants.SUCCESSFULLY_UPDATED, userService.updateRoles(id, roles));
    }

    @PutMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ApiResponse update(@PathVariable final Long id, @RequestBody final UserDTO dto) {
        return ApiResponse.ok(Constants.SUCCESSFULLY_UPDATED, userService.update(id, dto));
    }

    @PutMapping("/{id}/{enabled}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ApiResponse setEnabled(@PathVariable final Long id, @PathVariable final Boolean enabled) {
        return ApiResponse.ok(Constants.SUCCESSFULLY_UPDATED, userService.setEnabled(id, enabled));
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @GetMapping(path = "/{id}/history")
    public List<UserDTO> history(@PathVariable final Long id) {
        return userService.findRevisions(id);
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @PostMapping
    public ApiResponse create(@RequestBody @Valid final UserDTO dto) {
        return ApiResponse.ok(Constants.SUCCESSFULLY_CREATED, userService.create(dto));
    }
}

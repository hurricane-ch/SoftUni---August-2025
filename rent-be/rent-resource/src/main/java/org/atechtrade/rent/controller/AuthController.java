package org.atechtrade.rent.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.dto.BuildInfoDTO;
import org.atechtrade.rent.dto.JwtResponse;
import org.atechtrade.rent.dto.LanguageDTO;
import org.atechtrade.rent.dto.RefreshTokenResponse;
import org.atechtrade.rent.dto.RentalHolderDTO;
import org.atechtrade.rent.dto.RentalItemDTO;
import org.atechtrade.rent.dto.ReservationDTO;
import org.atechtrade.rent.dto.SimpleSignUpRequest;
import org.atechtrade.rent.dto.UserDetailsDTO;
import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.model.RefreshToken;
import org.atechtrade.rent.model.Role;
import org.atechtrade.rent.model.User;
import org.atechtrade.rent.model.VerificationToken;
import org.atechtrade.rent.security.JwtUtils;
import org.atechtrade.rent.service.ContractorService;
import org.atechtrade.rent.service.LanguageService;
import org.atechtrade.rent.service.MessageResourceService;
import org.atechtrade.rent.service.RefreshTokenService;
import org.atechtrade.rent.service.RentalHolderService;
import org.atechtrade.rent.service.RentalItemService;
import org.atechtrade.rent.service.ReservationService;
import org.atechtrade.rent.util.CommonUtils;
import org.atechtrade.rent.util.Constants;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Slf4j
@RestController
@RequestMapping("/api/${rent.api.version}/auth")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final BuildProperties buildProperties;
    private final LanguageService languageService;
    private final MessageResourceService msgRes;
    private final ContractorService contractorService;
    private final RentalHolderService rentalHolderService;
    private final RentalItemService rentalItemService;
    private final ReservationService reservationService;

    @GetMapping("/build-info")
    public BuildInfoDTO buildInfo() {
        return BuildInfoDTO.of(buildProperties);
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> authenticateUser(final String username, final String password) {
        return authenticate(username + " ", password, "0");
    }

//    @PostMapping(path = "/signin", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
//    public ResponseEntity<?> authenticateContractor(final String username, final String password) {
//        return authenticate(username, password, "1");
//    }

    private ResponseEntity<?> authenticate(final String username, final String password, final String type) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ResponseEntity.badRequest().body("Incorrect credentials!");
        }
        Authentication auth;

        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Incorrect credentials!");
        }

        UserDetailsDTO userDetailsDTO = (UserDetailsDTO) auth.getPrincipal();
        final String jwt = jwtUtils.generateToken(userDetailsDTO);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetailsDTO.getId());
        List<String> roles = userDetailsDTO.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return ResponseEntity.ok(JwtResponse.builder()
                .type(type)
                .token(jwt)
                .refreshToken(refreshToken.getRefreshToken())
                .userId(userDetailsDTO.getId())
                .branchId(userDetailsDTO.getBranchId())
                .email(userDetailsDTO.getEmail())
                .username(userDetailsDTO.getUsername())
                .fullName(userDetailsDTO.getFullName())
                .roles(roles).build()
        );
    }

//    @PostMapping("/signup")
//    public ApiResponse registerContractor(@Valid @RequestBody final SignUpRequest request,
//                                                @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        String language = CommonUtils.getLanguage(locale);
//        try {
//            contractorService.register(SignUpRequest.buildVerificationToken(request), language);
//        } catch (UserAlreadyExistAuthenticationException e) {
//            log.error("Exception Occurred", e);
//            return ApiResponse.err(msgRes.get("forgot.password.email.in.use", language));
//        }
//        return ApiResponse.ok(MessageFormat.format(msgRes.get("forgot.password.success", language),
//                        request.getEmail(), VerificationToken.EXPIRATION_MILLIS / 60 / 1000));
//    }

//    @PutMapping("/signup-confirm")
//    public ApiResponse registerContractorConfirm(@NotNull @RequestBody final TokenConfirmRequest request) {
//        return ApiResponse.ok(contractorService.validateRegisterContractorToken(request.getToken()));
//    }

    //TODO to send new code if make new request
    @PostMapping("/create-email-verification-code")
    public ApiResponse createEmailVerificationCode(@Valid @RequestBody final SimpleSignUpRequest request,
                                                   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        String language = CommonUtils.getLanguage(locale);
//        contractorService.createOrUpdateEmailVerificationCode(request.getEmail(), request.getPhone(), request.getFullName(), language);

        return ApiResponse.ok(MessageFormat.format(msgRes.get("create.email.verification.code.success", language),
                request.getEmail(), VerificationToken.EXPIRATION_MILLIS / 60 / 1000));
    }

    @PutMapping("/confirm-email-verification-code")
    public ApiResponse confirmEmailVerificationCode(@NotNull @RequestBody final SimpleSignUpRequest request) {
        return ApiResponse.ok(contractorService.confirmEmailVerificationCode(request));
    }


//    @PostMapping("/forgot-password")
//    public ApiResponse forgotPassword(@NotNull @RequestBody final ForgotPasswordRequest request,
//                                            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        String language = CommonUtils.getLanguage(locale);
//        try {
//            contractorService.forgotPassword(request.getEmail(), language);
//        } catch (UserAlreadyExistAuthenticationException e) {
//            log.error("Exception Occurred", e);
//            return ApiResponse.err(msgRes.get("global.something.went.wrong", language));
//        }
//
//        return ApiResponse.ok(MessageFormat.format(msgRes.get("forgot.password.success", language),
//                        request.getEmail(), VerificationToken.EXPIRATION_MILLIS / 60 / 1000));
//    }

//    @PutMapping("/forgot-password-confirm")
//    public ApiResponse forgotPasswordConfirm(@Valid @RequestBody final ForgotPasswordConfirmRequest request) {
//        return ApiResponse.ok(contractorService.validateForgotPasswordToken(request.getToken(), request.getPassword()));
//    }

//    @ResponseBody
//    @PostMapping("/token/resend")
//    public ApiResponse resendRegistrationToken(@NotEmpty @RequestBody final Map<String, String> request,
//                                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        String language = CommonUtils.getLanguage(locale);
//        if (!contractorService.resendVerificationToken(request.get("token"), language)) {
//            return ApiResponse.err(msgRes.get("global.token.not.found", language));
//        }
//        return ApiResponse.ok(Constants.SUCCESS);
//    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody final Map<String, String> request) {
        RefreshToken token = refreshTokenService.findByRefreshToken(request.get("token"));

        if (token != null && refreshTokenService.verifyExpiration(token) != null) {
            User user = token.getUser();
            Map<String, Object> claims = new HashMap<>();
            claims.put("ROLES", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            String jwt = jwtUtils.createToken(claims, user.getId(), user.getUsername());

            return ResponseEntity.ok(new RefreshTokenResponse("Bearer", jwt, request.get("token")));
        }

        return ResponseEntity.badRequest().body("The token has expired");
    }

    @GetMapping(path = "/languages")
    public List<LanguageDTO> findAllLanguages() {
        return languageService.findAllDTO();
    }

    @GetMapping("/rental-holder/{id}")
    public RentalHolderDTO findById(@PathVariable final Long id) {
        return rentalHolderService.findDTOById(id);
    }

    @GetMapping("/rental-items")
    public List<RentalItemDTO> findAll() {
        return rentalItemService.findAll();
    }

    @GetMapping("/rental-items/{rentalItemId}/reservations")
    public RentalItemDTO findRentalItemByIdWithReservations(@PathVariable final Long rentalItemId,
                                                            @RequestParam final Integer year) {
        return rentalItemService.findRentalItemByIdWithReservations(rentalItemId, year);
    }

    @GetMapping("/reservations/{rentalItemId}/{year}")
    public List<ReservationDTO> findAllReservationsByRentalItemIdAndYear(
            @PathVariable final Long rentalItemId,
            @PathVariable final Integer year
    ) {
        return reservationService.findAllByRentalItemIdAndStatusAndYear(rentalItemId, year);
    }

    @PostMapping("/reservations/rental-items/{rentalItemId}")
    @Parameter(in = ParameterIn.HEADER, name = HttpHeaders.ACCEPT_LANGUAGE, required = true, example = Language.BG)
    public ApiResponse create(
            @PathVariable final Long rentalItemId,
            @RequestBody @Valid @NonNull final ReservationDTO dto,
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale
    ) {
        if (!dto.getTermsAccepted()) {
            throw new IllegalArgumentException("You must accept terms and conditions!");
        }
        String language = CommonUtils.getLanguage(locale);
        return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.POST),
                reservationService.create(rentalItemId, dto, language));
    }
}

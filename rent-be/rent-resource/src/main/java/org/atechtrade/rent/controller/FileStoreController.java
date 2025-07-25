package org.atechtrade.rent.controller;

import org.atechtrade.rent.dto.ApiResponse;
import org.atechtrade.rent.security.RolesConstants;
import org.atechtrade.rent.service.FileStoreService;
import org.atechtrade.rent.util.Constants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static java.text.MessageFormat.format;

@RestController
@RequestMapping("/api/${rent.api.version}/files")
@SecurityRequirement(name = "rentapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FileStoreController {

	private final FileStoreService service;

	@GetMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> findById(@PathVariable final Long id) {
		return ResponseEntity.ok().body(service.get(id));
	}

	@GetMapping(path = "/{id}/file")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<byte[]> getFile(@PathVariable final Long id) {
		return service.getFile(id);
	}

//	@PostMapping(path = "/{docTypeCode}")
//	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//	public ResponseEntity<?> uploadFile(@PathVariable final String docTypeCode,
//									@Valid @RequestPart(value = "file") final MultipartFile multipartFile) {
//		service.create(docTypeCode, multipartFile);
//		// TODO Add the message to the message resource with translation
//		return ResponseEntity.ok(new ApiResponse(true, "The file has been uploaded successfully"));
//	}

	@PutMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ApiResponse update(@PathVariable final Long id,
							  @Valid @RequestPart(value = "file") final MultipartFile multipartFile) {
		return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.PUT), service.update(id, multipartFile));
	}

	@DeleteMapping (path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ApiResponse delete(@PathVariable final Long id) {
		return ApiResponse.ok(format(Constants.SUCCESS_MSG, HttpMethod.DELETE), service.delete(id));
	}
}

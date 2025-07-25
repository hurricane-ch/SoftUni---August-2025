package org.atechtrade.rent.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSignUpRequest {

	private String token;
	@NotEmpty
	private String email;
	@NotEmpty
	private String phone;
	@NotEmpty
	private String fullName;

	private String identifier;
	private String username;

	public static SimpleSignUpRequest of(final ReservationDTO dto) {
		final SimpleSignUpRequest request = new SimpleSignUpRequest();
		BeanUtils.copyProperties(dto.getContractor(), request);
		request.setToken(dto.getToken());
		return request;
	}
}

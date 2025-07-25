package org.atechtrade.rent.security;

import org.atechtrade.rent.dto.UserDetailsDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	@Value("${jwt.exp}")
	private int JWT_EXP;

	public String generateToken(UserDetailsDTO userDetailsDTO) {
		List<String> roles = userDetailsDTO.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		Map<String, Object> claims = new HashMap<>();
		claims.put("ROLES", roles);
		claims.put("branchId", userDetailsDTO.getBranchId());
		claims.put("branchName", userDetailsDTO.getBranchName());

		return createToken(claims, userDetailsDTO.getId(), userDetailsDTO.getUsername());
	}

	public String createToken(final Map<String, Object> claims, final Long id, final String subject) {
		return Jwts.builder()
				.claims(claims)
				.id(id.toString())
				.subject(subject)
				.audience().add("rent-fe").and()
				.issuer("rent-be")
				.issuedAt(new Date((new Date()).getTime()))
				.expiration(new Date((new Date()).getTime() + JWT_EXP))
				.signWith(getSigningKey(), Jwts.SIG.HS256)
				.compact();
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(new SecretKeySpec(
				this.SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.toString()
		).getEncoded());
	}

	public boolean validateJwtToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			log.error("Invalid token", ex);
		} catch (ExpiredJwtException ex) {
			log.error("Expired token", ex);
		}
		return false;
	}

	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public List<String> extractRoles(String token) {
		return (List<String>) extractClaim(token).get("ROLES");
	}
	
	public Claims extractClaim(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

}

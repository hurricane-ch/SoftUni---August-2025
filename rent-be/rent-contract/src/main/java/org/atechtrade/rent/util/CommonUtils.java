package org.atechtrade.rent.util;

import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.model.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CommonUtils {

	public static String getLanguage(final Locale locale) {
		return (locale != null && StringUtils.hasText(locale.getLanguage()))
				? locale.getLanguage() : Language.BG;
	}

	public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Role> roles) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}

	public static Date calculateExpiryDate(final long expiryTimeInMillis) {
		return new Date(System.currentTimeMillis() + expiryTimeInMillis);
	}

	public static boolean isValidId(Long id) {
		return id != null && id > 0;
	}

}

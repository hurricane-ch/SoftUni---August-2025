package org.atechtrade.rent.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Comment("Таблицата съхранява потребители")
public class User extends BaseEntity {

	@Column(name = "email", unique = true)
	@Comment("Уникална електронна поща")
	private String email;

	@Column(name = "full_name")
	@Comment("Пълно име")
	private String fullName;

	@Column(name = "username", unique = true)
	@Comment("Потребителско име")
	private String username;

	@Column(name = "password")
	@Comment("Парола")
	private String password;

	@Column(name = "identifier", unique = true)
	@Comment("Уникален идентификатор (ЕГН)")
	private String identifier;

	@Column(name = "position")
	@Comment("Позиция")
	private String position;

	@Column(name = "enabled")
	@Comment("Флаг указващ дали записа е активен")
	private Boolean enabled;

	@Builder.Default
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();
}

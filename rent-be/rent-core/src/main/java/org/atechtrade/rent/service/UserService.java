package org.atechtrade.rent.service;

import org.atechtrade.rent.dto.RevisionMetadataDTO;
import org.atechtrade.rent.dto.UserDTO;
import org.atechtrade.rent.dto.UserDetailsDTO;
import org.atechtrade.rent.dto.common.BaseDTO;
import org.atechtrade.rent.dto.common.UserBaseDTO;
import org.atechtrade.rent.exception.EntityAlreadyExistException;
import org.atechtrade.rent.exception.EntityNotFoundException;
import org.atechtrade.rent.exception.InvalidUserDataException;
import org.atechtrade.rent.model.Role;
import org.atechtrade.rent.model.User;
import org.atechtrade.rent.repository.RoleRepository;
import org.atechtrade.rent.repository.UserRepository;
import org.atechtrade.rent.util.CommonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserService {

	private final UserRepository repository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;

	public User findById(final Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class, id));
	}

	public Role findRoleById(final Long id) {
		return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Role.class, id));
	}

	public Role findRoleByIdOrNull(final Long id) {
		return roleRepository.findById(id).orElse(null);
	}

	public User findByIdOrNull(final Long id) {
		return CommonUtils.isValidId(id)
				? repository.findById(id).orElse(null)
				: null;
	}

	public User findByIdOrNull(final BaseDTO dto) {
		return dto != null ? findByIdOrNull(dto.getId()) : null;
	}

	@Transactional(readOnly = true)
	public UserBaseDTO findDTOById(final Long id) {
		return UserBaseDTO.of(findById(id));
	}

	@Transactional(readOnly = true)
	public Page<UserDTO> findAll(Pageable pageable) {
		return repository.findAll(pageable).map(UserDTO::of);
	}

	public User findByUsername(final String username) {
		return repository.findByUsernameIgnoreCase(
				username.toLowerCase()).orElseThrow(() -> new EntityNotFoundException(User.class, username)
		);
	}

	@Transactional(readOnly = true)
	public UserDetailsDTO findUserDetailsDTOByUsername(final String username) {
		return UserDetailsDTO.of(findByUsername(username));
	}

	public User findByUsernameOrNull(final String username) {
		return repository.findByUsernameIgnoreCase(username).orElse(null);
	}

	public User findByEmail(final String email) {
		return repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(User.class, email));
	}

	@Transactional
	public User create(final UserDTO dto) {
		if (dto == null || !StringUtils.hasText(dto.getUsername())) {
			throw new InvalidUserDataException("User is required");
		}

		User existingUser = findByUsernameOrNull(dto.getUsername());
		if (existingUser != null) {
			throw new EntityAlreadyExistException(User.class, existingUser.getId());
		}

		User user = UserDTO.to(dto);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		if (!CollectionUtils.isEmpty(dto.getRoles())) {
			dto.getRoles().forEach(r -> user.getRoles().add(roleRepository.findByName(r)));
		}

        return save(user);
	}

	public Integer signout(final Long id) {
		return refreshTokenService.deleteByUser(findById(id));
	}

	@Transactional
	public User update(final Long id, final UserDTO user) {
		if (id == null || id <= 0) {
			throw new RuntimeException("ID field is required");
		}
		if (!id.equals(user.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}

		User current = findById(id);

		if (StringUtils.hasText(user.getFullName())) {
			current.setFullName(user.getFullName());
		}
		if (StringUtils.hasText(user.getIdentifier())) {
			current.setIdentifier(user.getIdentifier());
		}
		if (StringUtils.hasText(user.getPassword())) {
			current.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		current.setEnabled(user.getEnabled());

		current.getRoles().clear();
		user.getRoles().forEach(r -> current.getRoles().add(roleRepository.findByName(r)));

		return save(current);
	}

	@Transactional
	public User updateRoles(final Long id, final List<String> roles) {
		if (CollectionUtils.isEmpty(roles)) {
			throw new RuntimeException("At least one role is required");
		}
		User user = findById(id);
		user.getRoles().clear();
		roles.forEach(r -> user.getRoles().add(roleRepository.findByName(r)));

		return save(user);
	}

	@Transactional(readOnly = true)
    public List<UserBaseDTO> search(String param) {
		return UserBaseDTO.baseOf(repository.search(param));
    }

	@Transactional
	public User setEnabled(final Long id, final Boolean enabled) {
		if (enabled == null) {
			throw new RuntimeException("Boolean param enabled is required");
		}
		User user = findById(id);
		user.setEnabled(enabled);
		return save(user);
	}

	@Transactional(readOnly = true)
	public List<Role> roles() {
		return roleRepository.findAll();
	}

	@Transactional
	public Role createRole(final Role role) {
		if (role == null || !StringUtils.hasText(role.getName())) {
			throw new InvalidUserDataException("Role or role name is empty!");
		}
		return roleRepository.save(role);
	}

	@Transactional(readOnly = true)
	public List<UserDTO> findRevisions(final Long id) {
		List<UserDTO> revisions = new ArrayList<>();
		repository.findRevisions(id).get().forEach(r -> {
			UserDTO rev = UserDTO.of(r.getEntity());
			rev.setRevisionMetadata(RevisionMetadataDTO.builder()
					.revisionNumber(r.getMetadata().getRevisionNumber().orElse(null))
					.revisionInstant(r.getMetadata().getRevisionInstant().orElse(null))
					.revisionType(r.getMetadata().getRevisionType().name())
					.createdBy(r.getEntity().getCreatedBy())
					.createdDate(r.getEntity().getCreatedDate())
					.lastModifiedBy(r.getEntity().getLastModifiedBy())
					.lastModifiedDate(r.getEntity().getLastModifiedDate())
					.build()
			);
			revisions.add(rev);
		});
		return revisions;
	}

	private User save(final User user) {
		return repository.save(user);
	}
}

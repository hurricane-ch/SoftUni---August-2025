package org.atechtrade.rent.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.atechtrade.rent.dto.LanguageDTO;
import org.atechtrade.rent.exception.EntityNotFoundException;
import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.model.User;
import org.atechtrade.rent.repository.LanguageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LanguageService {

	private final LanguageRepository repository;

	public Language findById(final String languageId) {
		return !StringUtils.hasText(languageId)
				? repository.findAllByEnabledIsTrueAndMainIsTrue().orElseThrow(() -> new EntityNotFoundException(User.class, languageId))
				: repository.findById(languageId).orElseThrow(() -> new EntityNotFoundException(User.class, languageId));
	}

	public Language findByIdOrNull(final String languageId) {
		return repository.findById(languageId).orElse(null);
	}

	public List<Language> findAll() {
		return repository.findAll();
	}

	public List<LanguageDTO> findAllDTO() {
		return LanguageDTO.of(repository.findAll());
	}

	public Language getMainLanguage() {
		return findById(null);
	}

	@Transactional
	public Language create(final Language entity) {
		if (entity == null || !StringUtils.hasText(entity.getLanguageId())) {
			throw new RuntimeException("Language id is required");
		}

		if (repository.existsById(entity.getLanguageId())) {
			throw new RuntimeException("Language with id " + entity.getLanguageId() + " already exists");
		}

		if (Boolean.TRUE.equals(entity.getMain())) {
			removeMain();
		}
		return repository.save(entity);
	}

	@Transactional
	public Language update(final String languageId, final LanguageDTO dto) {
		if (!StringUtils.hasText(languageId) || dto == null || !languageId.equals(dto.getLanguageId())) {
			throw new RuntimeException("Language id is not equal to dto language id");
		}
		Language entity = findById(languageId);
		if (Boolean.TRUE.equals(entity.getMain())) {
			if (!dto.getMain() || !dto.getEnabled()) {
				throw new RuntimeException("Choose another language as main language before updating this one");
			}
			removeMain();
		} else if (Boolean.TRUE.equals(dto.getMain()) && dto.getEnabled()) {
			removeMain();
		}

		entity.setMain(dto.getMain());
		entity.setName(dto.getName());
		entity.setLocale(dto.getLocale());
		entity.setDescription(dto.getDescription());
		entity.setEnabled(dto.getEnabled());

		return repository.save(entity);
	}

	private void removeMain() {
		List<Language> mains = repository.findAllByMainIsTrue();
		if (!CollectionUtils.isEmpty(mains)) {
			mains.forEach(m -> m.setMain(false));
		}
		repository.saveAll(mains);
	}
}

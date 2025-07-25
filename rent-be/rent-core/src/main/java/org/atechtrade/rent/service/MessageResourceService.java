package org.atechtrade.rent.service;

import org.atechtrade.rent.dto.MessageResourceDTO;
import org.atechtrade.rent.exception.EntityNotFoundException;
import org.atechtrade.rent.model.Language;
import org.atechtrade.rent.model.MessageResource;
import org.atechtrade.rent.repository.MessageResourceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MessageResourceService {

	private final MessageResourceRepository repository;

	public MessageResource findById(final MessageResource.MessageResourceIdentity id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageResource.class, id));
	}

	public String get(final String code, final String language) {
		return findById(new MessageResource.MessageResourceIdentity(code, language)).getMessage();
	}

	public String get(final String code, final String language, final Object... params) {
		String message = get(code, language);
		return (params == null || params.length == 0) ? message : MessageFormat.format(message, params);
	}


	public MessageResource findByIdOrNull(final MessageResource.MessageResourceIdentity id) {
		return repository.findById(id).orElse(null);
	}

//	public List<MessageResource> findAllDTO() {
//		return repository.findAllDTO();
//	}

	public Page<MessageResource> findAll(final String languageId, final Pageable pageable) {
		return repository.findAllByMessageResourceIdentityLanguageIdOrderByMessageResourceIdentity_code(languageId, pageable);
	}

	@Transactional(readOnly = true)
	public Page<MessageResourceDTO> findDTOAll(final String languageId, final Pageable pageable) {
		return findAll(languageId, pageable).map(MessageResourceDTO::of);
	}

	public List<MessageResource> findAllByCode(final String code) {
		return repository.findAllByMessageResourceIdentity_Code(code);
	}

	public String findEnumTranslation(final Enum<?>anEnum, String language) {
		String code = "enum." + anEnum.getClass().getSimpleName() + "." + anEnum.name();
		return findById(new MessageResource.MessageResourceIdentity(code, language)).getMessage();
	}

	@Transactional
	public MessageResource update(final MessageResource.MessageResourceIdentity identity, final MessageResourceDTO dto) {
		if (identity == null || !StringUtils.hasText(identity.getCode()) || dto == null || !identity.getCode().equals(dto.getCode())) {
			throw new RuntimeException("Language id is not equal to dto language id");
		}
		MessageResource entity = findById(identity);

		entity.setMessage(dto.getMessage());

		return repository.save(entity);
	}

	@Transactional
	public MessageResource updateOrCreate(final MessageResource.MessageResourceIdentity identity, final MessageResourceDTO dto) {
		if (identity == null || !StringUtils.hasText(identity.getCode()) || dto == null || !identity.getCode().equals(dto.getCode())) {
			throw new RuntimeException("Language id is not equal to dto language id");
		}
		MessageResource entity = findByIdOrNull(identity);

		if (entity == null) {
			entity = MessageResource.builder().messageResourceIdentity(identity).build();
		}

		entity.setMessage(dto.getMessage());

		return repository.save(entity);
	}

}

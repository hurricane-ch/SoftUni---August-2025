package org.atechtrade.rent.service;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.dto.FileDTO;
import org.atechtrade.rent.dto.RentalHolderDTO;
import org.atechtrade.rent.model.File;
import org.atechtrade.rent.model.RentalHolder;
import org.atechtrade.rent.repository.RentalHolderRepository;
import org.atechtrade.rent.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RentalHolderService {

    private final RentalHolderRepository repository;
    private final FileStoreService fileStoreService;

    public RentalHolder findById(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public RentalHolderDTO findDTOById(final Long id) {
        RentalHolder entity = findById(id);

        RentalHolderDTO dto = RentalHolderDTO.of(entity);

        if (!CollectionUtils.isEmpty(entity.getAttachments())) {
            for (File file : entity.getAttachments()) {
                try {
                    FileDTO fileDTO = fileStoreService.get(file);
                    String base64Encoded = Base64.getEncoder().encodeToString(fileDTO.getResource());

                    if (file.isMain()) {
                        dto.setMainAttachment(base64Encoded);
                    } else {
                        dto.getAttachments().add(base64Encoded);
                    }
                } catch (IOException e) {
                    log.error("Error encoding file to Base64 for rental item with id: {}", id, e);
                }
            }
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<RentalHolderDTO> findAllDTO() {
        return RentalHolderDTO.of(repository.findAll());
    }

    @Transactional
    public RentalHolder create(RentalHolderDTO dto) {
        return repository.save(RentalHolderDTO.to(dto));
    }

    @Transactional
    public RentalHolder update(Long id, RentalHolderDTO dto) {
        if (!CommonUtils.isValidId(id)) {
            throw new IllegalArgumentException("Path variable id is not valid");
        }
        if (!id.equals(dto.getId())) {
            throw new IllegalArgumentException("Path variable must be equal to id");
        }
        RentalHolder entity = findById(id);
        BeanUtils.copyProperties(dto, entity);
        return repository.save(entity);
    }

    @Transactional
    public void delete(final Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public RentalHolder attach(final Long id, final String description, final MultipartFile multipartFile, boolean main) {
        RentalHolder entity = findById(id);

        if (entity.getAttachments().size() >= 20) {
            throw new IllegalArgumentException("Too many pictures");
        }

        if (main) {
            entity.getAttachments().forEach(a -> a.setMain(false));
        }

        entity.getAttachments().add(fileStoreService.create(entity.getClass(), entity.getId(), description, multipartFile, main));
        return repository.save(entity);
    }
}

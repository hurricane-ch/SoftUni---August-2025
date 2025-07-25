package org.atechtrade.rent.service;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.dto.FileDTO;
import org.atechtrade.rent.dto.RentalItemDTO;
import org.atechtrade.rent.dto.ReservationDTO;
import org.atechtrade.rent.enums.ReservationStatus;
import org.atechtrade.rent.model.File;
import org.atechtrade.rent.model.RentalItem;
import org.atechtrade.rent.repository.RentalItemRepository;
import org.atechtrade.rent.repository.ReservationRepository;
import org.atechtrade.rent.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RentalItemService {

    private final RentalItemRepository repository;
    private final ReservationRepository reservationRepository;
    private final FileStoreService fileStoreService;

    public RentalItem findById(final Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<RentalItemDTO> findAllByStatusAndYear(final ReservationStatus status, final Integer year) {
        return RentalItemDTO.of(repository.findAllByReservationsStatusAndYear(status, year));
    }

//    @Transactional(readOnly = true)
//    public List<RentalItemDTO> getAll(final ReservationStatus status, final Integer year) {
//        List<RentalItem> result = repository.findAll();
//
//        List<RentalItem> filtered = result.stream()
//                .peek(item -> item.getReservations().removeIf(r ->
//                        !r.getStatus().equals(status) ||
//                                (year != null && r.getFromDate().getYear() != year)
//                ))
//                .filter(item -> !item.getReservations().isEmpty())
//                .toList();
//
//        return RentalItemDTO.of(filtered);
//    }

    @Transactional(readOnly = true)
    public RentalItemDTO findDTOById(final Long id) {
        RentalItem entity = findById(id);

        RentalItemDTO dto = RentalItemDTO.of(entity);

        for (File file : entity.getFiles()) {
            try {
                FileDTO fileDTO = fileStoreService.get(file);
                String base64Encoded = Base64.getEncoder().encodeToString(fileDTO.getResource());

                if (file.isMain()) {
                    dto.setMainFile(base64Encoded);
                } else {
                    dto.getFiles().add(base64Encoded);
                }
            } catch (IOException e) {
                log.error("Error encoding file to Base64 for rental item with id: {}", id, e);
            }
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<RentalItemDTO> findAllSortedById() {
        return RentalItemDTO.of(repository.findAllByOrderByIdAsc());
    }

    @Transactional(readOnly = true)
    public RentalItemDTO findRentalItemByIdWithReservations(final Long id, final Integer year) {
        RentalItem entity = findById(id);
        RentalItemDTO dto = RentalItemDTO.of(entity);

        dto.setReservations(ReservationDTO.of(reservationRepository.findAllByRentalItemIdAndYear(id,year)));
        processFiles(entity, dto);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<RentalItemDTO> findAll() {
        List<RentalItem> entities = repository.findAllByOrderByRecommendedVisitorsAsc();

        List<RentalItemDTO> dtos = new ArrayList<>();

        for (RentalItem entity : entities) {
            RentalItemDTO dto = RentalItemDTO.of(entity);

            File file = entity.getFiles().stream().filter(File::isMain).findFirst().orElse(null);

            if (file != null) {
                try {
                    FileDTO fileDTO = fileStoreService.get(file);
                    String base64Encoded = Base64.getEncoder().encodeToString(fileDTO.getResource());
                    dto.setMainFile(base64Encoded);
                } catch (IOException e) {
                    log.error("Error encoding file to Base64 for rental item with id: {}", entity.getId(), e);
                }
            }
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public RentalItem create(final RentalItemDTO dto) {
        RentalItem item = RentalItemDTO.to(dto);
        item.setEnabled(true);
        return repository.save(item);
    }

    @Transactional
    public RentalItem update(final Long id, final RentalItemDTO dto) {
        if (!CommonUtils.isValidId(id)) {
            throw new IllegalArgumentException("Path variable id is not valid");
        }
        if (!id.equals(dto.getId())) {
            throw new IllegalArgumentException("Path variable must be equal to id");
        }
        RentalItem item = findById(id);
        BeanUtils.copyProperties(dto, item);

        return repository.save(item);
    }

    @Transactional
    public void disable(final Long id) {
        RentalItem item = findById(id);
        item.setEnabled(false);
        repository.save(item);
    }

    @Transactional
    public void enable(final Long id) {
        RentalItem item = findById(id);
        item.setEnabled(true);
        repository.save(item);
    }

    @Transactional
    public void delete(final Long id) {
        RentalItem item = findById(id);
        if (CollectionUtils.isEmpty(item.getReservations()) &&
                CollectionUtils.isEmpty(item.getFiles())) {
            repository.delete(item);
        } else {
            throw new IllegalArgumentException("Rental item has reservations and/or attachments");
        }
    }

    private void processFiles(RentalItem entity, RentalItemDTO dto) {
        if (entity.getFiles() == null || entity.getFiles().isEmpty()) {
            return;
        }

        entity.getFiles().forEach(file -> {
            try {
                FileDTO fileDTO = fileStoreService.get(file);
                String base64Encoded = Base64.getEncoder().encodeToString(fileDTO.getResource());

                if (file.isMain()) {
                    dto.setMainFile(base64Encoded);
                } else {
                    dto.getFiles().add(base64Encoded);
                }
            } catch (IOException e) {
                log.error("Error encoding file to Base64 for rental item with id: {}", entity.getId(), e);
            }
        });
    }

    //=======================================

    @Transactional
    public File uploadFile(final Long id, final String description, final MultipartFile multipartFile, boolean main) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("The file is empty");
        }
        RentalItem entity = findById(id);

        if (main) {
            entity.getFiles().forEach(f -> f.setMain(false));
        }

        File file = fileStoreService.create(RentalItem.class, id, description, multipartFile, main);
        entity.getFiles().add(file);

        return repository.save(entity).getFiles().stream()
                .filter(f -> file.getId().equals(f.getId())).findFirst().orElse(null);
    }

    @Transactional
    public boolean deleteFile(final Long id, final Long fileId) {
        RentalItem entity = findById(id);
        if (fileStoreService.delete(fileId)) {
            entity.getFiles().removeIf(f -> fileId.equals(f.getId()));
            repository.save(entity);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<FileDTO> findAllFiles(final Long id) {
        return FileDTO.of(files(id));
    }

    public List<File> files(final Long id) {
        return findById(id).getFiles();
    }

    @Transactional
    public RentalItem setMain(final Long id, final Long fileId, final boolean main) {
        RentalItem entity = findById(id);

        if (main) {
            entity.getFiles().forEach(f -> f.setMain(false));
            entity.getFiles().stream()
                    .filter(f -> f.getId().equals(fileId))
                    .findFirst()
                    .ifPresent(f -> f.setMain(true));
        } else {
            entity.getFiles().stream()
                    .filter(f -> f.getId().equals(fileId))
                    .findFirst()
                    .ifPresent(f -> f.setMain(false));
        }
        return entity;
    }
}

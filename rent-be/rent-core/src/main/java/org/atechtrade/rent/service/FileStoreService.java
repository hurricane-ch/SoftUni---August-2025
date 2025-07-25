package org.atechtrade.rent.service;

import org.atechtrade.rent.dto.FileDTO;
import org.atechtrade.rent.exception.EntityNotFoundException;
import org.atechtrade.rent.exception.FileStorageException;
import org.atechtrade.rent.model.File;
import org.atechtrade.rent.repository.FileStoreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FileStoreService {

    private final FileStoreRepository repository;

    @Value("${rent.file-store.base-path}")
    private String basePath;
    @Value("${rent.file-store.single-backup}")
    private boolean singleBackup;

    public File findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(File.class, id));
    }

    public File findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    public ResponseEntity<byte[]> getFile(final Long id) {
        File file = findById(id);
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(file.getFilePath()));
//        ByteArrayResource resource = new ByteArrayResource(fileBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
            headers.add("File-Name", file.getFileName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileBytes.length)
                    .contentType(file.getContentType() != null ? MediaType.valueOf(file.getContentType()) : null)
                    .body(fileBytes);
        } catch (IOException e) {
            log.error("Error getting file with id: {}", id, e);
        }
        return ResponseEntity.ofNullable(null);
    }

    public FileDTO get(final Long id) {
        return get(findById(id));
    }

    public FileDTO get(final File file) {
        FileDTO dto = FileDTO.of(file);
        try {
            dto.setResource(Files.readAllBytes(Paths.get(file.getFilePath())));
        } catch (IOException e) {
            log.error("Error getting file with id: " + file.getId(), e);
        }
        return dto;
    }

    @Transactional
    public File create(final Class entityClass, final Long entityId, final String description, final MultipartFile multipartFile, final boolean main) {
        File file = buildFile(multipartFile, description);

        file.setFilePath(
                basePath + java.io.File.separator + entityClass.getSimpleName().toUpperCase() + java.io.File.separator + entityId +
                        java.io.File.separator + System.currentTimeMillis() + "_" + UUID.randomUUID() + "." +
                        FilenameUtils.getExtension(multipartFile.getOriginalFilename())
        );

        file.setMain(main);

        return update(file, multipartFile);
    }

    private File buildFile(final MultipartFile multipartFile, final String description) {
        return buildFile(multipartFile.getOriginalFilename(), multipartFile.getContentType(), description);
    }

    private File buildFile(final String fileName, final String contentType, final String description) {
        return File.builder()
                .fileName(fileName)
                .contentType(contentType)
                .description(description)
                .build();
    }

    @Transactional
    public File update(final Long id, final MultipartFile file) {
        return update(findById(id), file);
    }

    private File update(final File file, final MultipartFile multipartFile) {
        if (file == null) {
            throw new RuntimeException("Can't update FileStore null");
        }
        if (multipartFile == null) {
            throw new RuntimeException("Can't update FileStore multipartFile is null");
        }
        if (!StringUtils.hasText(file.getFilePath())) {
            throw new RuntimeException("File path is empty");
        }
        try {
            repository.save(file);
            store(file, multipartFile);
        } catch (Exception e) {
            log.error("Could not store file: {}. Please try again.", file.getFilePath(), e);
            throw new FileStorageException("Could not store file " + file.getFileName()
                    + ". Please try again.", e);
        }
        return file;
    }

    private File update(final File file, final InputStream fis) {
        if (file == null) {
            throw new RuntimeException("Can't update FileStore file is null");
        }
        if (fis == null) {
            throw new RuntimeException("Can't update FileStore fis is null");
        }
        if (!StringUtils.hasText(file.getFilePath())) {
            throw new RuntimeException("File path is empty");
        }
        try {
            repository.save(file);
            store(file, fis);
        } catch (Exception e) {
            log.error("Could not store file: {}. Please try again.", file.getFilePath(), e);
            throw new FileStorageException("Could not store file " + file.getFileName()
                    + ". Please try again.", e);
        }
        return file;
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    private void store(final File file, final MultipartFile multipartFile) throws IOException {
        store(file, multipartFile.getInputStream());
    }

    private void store(final File file, final InputStream fis) throws IOException {
        Files.createDirectories(
                Paths.get(file.getFilePath().substring(0, ordinalIndexOf(file.getFilePath(), java.io.File.separator, 5)))
        );
        if (singleBackup && Files.exists(Paths.get(file.getFilePath()))) {
            Files.move(Paths.get(file.getFilePath()), Paths.get(file.getFilePath() + ".back"), StandardCopyOption.REPLACE_EXISTING);
        }

        FileUtils.writeByteArrayToFile(new java.io.File(file.getFilePath()), fis.readAllBytes());
//        Files.copy(fis, Paths.get(file.getFilePath()), StandardCopyOption.REPLACE_EXISTING);
    }

    @Transactional
    public boolean delete(final Long id) {
        boolean success;
        File file = findById(id);
        if (!StringUtils.hasText(file.getFilePath())) {
            throw new RuntimeException("File path is empty");
        }
        try {
            if (!StringUtils.hasText(file.getFilePath())) {
                throw new RuntimeException("File path is empty");
            }
            repository.deleteById(file.getId());
            Files.delete(Paths.get(file.getFilePath()));
            success = true;
        } catch (IOException e) {
            log.error("Could not deleteFile file: {}.", file.getFilePath(), e);
            throw new FileStorageException("Could not deleteFile file: " + file.getFileName()
                    + ". Please try again.", e);
        }
        return success;
    }

}
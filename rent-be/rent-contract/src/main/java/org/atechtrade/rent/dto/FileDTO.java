package org.atechtrade.rent.dto;

import org.atechtrade.rent.model.File;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FileDTO {
    private Long id;
    private String fileName;
    private String description;
    private String contentType;
    private String mimeType;
    private boolean main;
    private byte[] resource;

    public static FileDTO of(final File source) {
        if (source == null) {
            return null;
        }
        FileDTO dto = new FileDTO();
        BeanUtils.copyProperties(source, dto);
        return dto;
    }

    public static List<FileDTO> of(final List<File> files) {
        return files.stream().map(FileDTO::of).collect(Collectors.toList());
    }
}

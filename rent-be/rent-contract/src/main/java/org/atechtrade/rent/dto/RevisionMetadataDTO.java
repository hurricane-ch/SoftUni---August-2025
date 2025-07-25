package org.atechtrade.rent.dto;

import org.atechtrade.rent.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RevisionMetadataDTO {
    private Number revisionNumber;
    private Instant revisionInstant;
    private String revisionType;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATETIME_PATTERN)
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DEFAULT_DATETIME_PATTERN)
    private LocalDateTime lastModifiedDate;
}

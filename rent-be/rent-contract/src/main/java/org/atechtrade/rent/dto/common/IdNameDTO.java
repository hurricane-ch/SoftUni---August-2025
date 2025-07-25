package org.atechtrade.rent.dto.common;

import org.atechtrade.rent.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class IdNameDTO extends BaseDTO {
    private String name;

    public static IdNameDTO of(BaseEntity source, final String name) {
        return source != null
                ? IdNameDTO.builder().id(source.getId()).name(name).build()
                : null;
    }
}

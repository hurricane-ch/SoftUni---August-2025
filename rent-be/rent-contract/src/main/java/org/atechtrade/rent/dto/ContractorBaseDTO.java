package org.atechtrade.rent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.atechtrade.rent.dto.common.BaseDTO;
import org.atechtrade.rent.enums.EntityType;
import org.atechtrade.rent.model.Contractor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorBaseDTO extends BaseDTO {

    private String fullName;
    private EntityType entityType;
    private String email;
    private String phone;

    public static Contractor to(final ContractorBaseDTO source) {
        Contractor entity = new Contractor();
        BeanUtils.copyProperties(source, entity, "id");

        return entity;
    }

    public static ContractorBaseDTO of(final UserDetailsDTO source) {
        ContractorBaseDTO dto = new ContractorBaseDTO();
        BeanUtils.copyProperties(source, dto);

        return dto;
    }

    public static ContractorBaseDTO of(final Contractor source) {
        if (source == null) {
            return null;
        }
        ContractorBaseDTO dto = new ContractorBaseDTO();
        BeanUtils.copyProperties(source, dto);

        return dto;
    }

    public static List<ContractorBaseDTO> of(final List<Contractor> source) {
        return source.stream().map(ContractorBaseDTO::of).collect(Collectors.toList());
    }
}

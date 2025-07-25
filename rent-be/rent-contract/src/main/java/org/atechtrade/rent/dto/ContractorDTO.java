package org.atechtrade.rent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.atechtrade.rent.model.Contractor;
import org.atechtrade.rent.model.Role;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorDTO extends ContractorBaseDTO {

    private String description;
    private String type; // 1 for contractor; 0 for User
    private String username;
    private String password;
    private String identifier;
    private List<String> roles= new ArrayList<>();;
    private Long branchId;
    private Boolean enabled;

    private RevisionMetadataDTO revisionMetadata;

    public static Contractor to(final ContractorDTO source) {
        Contractor entity = new Contractor();
        BeanUtils.copyProperties(source, entity);

        if (!CollectionUtils.isEmpty(source.getRoles())) {
            source.getRoles().forEach(r ->
                    entity.getRoles()
                            .add(Role.builder().name(r).contractors(List.of(entity)).build()));
        }

        if (source.getEntityType() != null) {
            entity.setEntityType(source.getEntityType());
        }

        return entity;
    }

    public static ContractorDTO of(final UserDetailsDTO source) {
        ContractorDTO dto = new ContractorDTO();
        BeanUtils.copyProperties(source, dto);

        if (!CollectionUtils.isEmpty(source.getAuthorities())) {
            dto.setRoles(source.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        return dto;
    }

    public static ContractorDTO of(final Contractor source, final String language) {
        ContractorDTO dto = new ContractorDTO();
        BeanUtils.copyProperties(source, dto);
        dto.setType("1");

        if (!CollectionUtils.isEmpty(source.getRoles())) {
            dto.setRoles(source.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        }
        if (source.getEntityType() != null) {
            dto.setEntityType(source.getEntityType());
        }

        return dto;
    }

    public static List<ContractorDTO> of(final List<Contractor> source, final String language) {
        return source.stream().map(c -> of(c, language)).collect(Collectors.toList());
    }
}

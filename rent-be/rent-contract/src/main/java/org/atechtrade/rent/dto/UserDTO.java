package org.atechtrade.rent.dto;

import org.atechtrade.rent.model.Role;
import org.atechtrade.rent.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String fullName;
    @NotNull
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String identifier;
    private String position;
    private Boolean enabled;
    private List<String> roles;
    private RevisionMetadataDTO revisionMetadata;
    private List<Long> inspections;

    public static User to(final UserDTO source) {
        if (source == null) {
            return null;
        }
        User entity = new User();
        BeanUtils.copyProperties(source, entity);

        return entity;
    }

    public static UserDTO of(final UserDetailsDTO userDetailsDTO) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(userDetailsDTO, dto);

        if (!CollectionUtils.isEmpty(userDetailsDTO.getAuthorities())) {
            dto.setRoles(userDetailsDTO.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        return dto;
    }

    public static UserDTO of(final User source) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(source, dto);

        if (!CollectionUtils.isEmpty(source.getRoles())) {
            dto.setRoles(source.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        }

        return dto;
    }

    public static List<UserDTO> of(final List<User> sources) {
        return !CollectionUtils.isEmpty(sources)
                ? sources.stream().map(UserDTO::of).collect(Collectors.toList())
                : null;
    }
}

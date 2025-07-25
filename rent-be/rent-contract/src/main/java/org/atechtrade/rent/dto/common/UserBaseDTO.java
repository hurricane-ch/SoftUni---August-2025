package org.atechtrade.rent.dto.common;

import org.atechtrade.rent.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class UserBaseDTO extends BaseDTO {

    private String email;
    private String fullName;
    private String position;

    public static User to(final UserBaseDTO source) {
        if (source == null) {
            return null;
        }
        User entity = new User();
        BeanUtils.copyProperties(source, entity);

        return entity;
    }

    public static UserBaseDTO of(final User source) {
        if (source == null) {
            return null;
        }
        UserBaseDTO dto = new UserBaseDTO();
        BeanUtils.copyProperties(source, dto);

        return dto;
    }

    public static List<UserBaseDTO> baseOf(final List<User> sources) {
        return !CollectionUtils.isEmpty(sources)
                ? sources.stream().map(UserBaseDTO::of).collect(Collectors.toList())
                : null;
    }
}

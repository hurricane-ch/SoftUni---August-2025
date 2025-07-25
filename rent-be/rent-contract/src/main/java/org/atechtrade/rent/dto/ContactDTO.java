package org.atechtrade.rent.dto;

import org.atechtrade.rent.enums.ContactType;
import org.atechtrade.rent.model.Contact;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ContactDTO {

    private Long id;
    private ContactType contactType;
    private String name;
    private String description;

    public static Contact to(final ContactDTO source) {
        Contact entity = new Contact();
        BeanUtils.copyProperties(source, entity, "id");
        return entity;
    }

    public static List<Contact> to(final List<ContactDTO> source) {
        return source.stream().map(ContactDTO::to).collect(Collectors.toList());
    }

    public static ContactDTO of(final Contact source) {
        final ContactDTO dto = new ContactDTO();

        BeanUtils.copyProperties(source, dto);

        return dto;
    }

    public static List<ContactDTO> of(final List<Contact> source) {
        return !CollectionUtils.isEmpty(source)
                ? source.stream().map(ContactDTO::of).collect(Collectors.toList())
                : null;
    }
}

package org.atechtrade.rent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atechtrade.rent.model.RentalHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RentalHolderDTO {

    private Long id;
    private String name;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String description;
    private String address;
    private String longitude;
    private String latitude;
    private String phone;
    private String email;
    private String facebookUrl;
    private String instagramUrl;
    private String tiktokUrl;
    private String foodPlacesDescription;
    private String entertainmentPlacesDescription;
    private String mainAttachment;
    private List<String> attachments = new ArrayList<>();

    public static RentalHolder to(final RentalHolderDTO source) {
        RentalHolder entity = new RentalHolder();
        BeanUtils.copyProperties(source, entity, "id");

        return entity;
    }

    public static List<RentalHolder> to(final List<RentalHolderDTO> source) {
        return source.stream().map(RentalHolderDTO::to).collect(Collectors.toList());
    }

    public static RentalHolderDTO of(final RentalHolder source) {
        if (source == null) {
            return null;
        }
        RentalHolderDTO dto = new RentalHolderDTO();
        BeanUtils.copyProperties(source, dto);
        return dto;
    }

    public static List<RentalHolderDTO> of(final List<RentalHolder> source) {
        return !CollectionUtils.isEmpty(source)
                ? source.stream().map(RentalHolderDTO::of).collect(Collectors.toList())
                : null;
    }
}

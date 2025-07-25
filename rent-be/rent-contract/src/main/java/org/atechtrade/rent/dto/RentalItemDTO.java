package org.atechtrade.rent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atechtrade.rent.enums.RentalItemType;
import org.atechtrade.rent.model.RentalItem;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

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
public class RentalItemDTO {

    private Long id;
    private RentalItemType rentalItemType;
    private String name;
    private Integer room;
    private Double price;
    private Integer recommendedVisitors;
    private String mainFile;
    private boolean enabled;
    private List<String> files = new ArrayList<>();
    private List<ReservationDTO> reservations = new ArrayList<>();

    public static RentalItem to(final RentalItemDTO source) {
        RentalItem entity = new RentalItem();
        BeanUtils.copyProperties(source, entity, "id");

        return entity;
    }

    public static List<RentalItem> to(final List<RentalItemDTO> source) {
        return source.stream().map(RentalItemDTO::to).collect(Collectors.toList());
    }

    public static RentalItemDTO of(final RentalItem source) {
        if (source == null) {
            return null;
        }
        RentalItemDTO dto = new RentalItemDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setReservations(ReservationDTO.of(source.getReservations()));

        return dto;
    }

    public static List<RentalItemDTO> of(final List<RentalItem> source) {
        return !CollectionUtils.isEmpty(source)
                ? source.stream().map(RentalItemDTO::of).collect(Collectors.toList())
                : null;
    }
}

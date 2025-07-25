package org.atechtrade.rent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.atechtrade.rent.dto.common.BaseDTO;
import org.atechtrade.rent.enums.ReservationStatus;
import org.atechtrade.rent.model.Reservation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ReservationDTO extends BaseDTO {

    private String token;
    private String reservationNumber;
    private LocalDate reservationDate;
    private Double price;
    private Double paid;
    private ReservationStatus status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean termsAccepted;

    private ContractorBaseDTO contractor;

    public static Reservation to(final ReservationDTO source) {
        Reservation entity = new Reservation();
        BeanUtils.copyProperties(source, entity, "id");
        return entity;
    }

    public static List<Reservation> to(final List<ReservationDTO> source) {
        return source.stream().map(ReservationDTO::to).collect(Collectors.toList());
    }

    public static ReservationDTO of(final Reservation source) {
        if (source == null) {
            return null;
        }
        ReservationDTO dto = new ReservationDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setContractor(ContractorBaseDTO.of(source.getContractor()));

        return dto;
    }

    public static List<ReservationDTO> of(final List<Reservation> source) {
        return !CollectionUtils.isEmpty(source)
                ? source.stream().map(ReservationDTO::of).collect(Collectors.toList())
                : null;
    }
}

package org.atechtrade.rent.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atechtrade.rent.dto.ReservationDTO;
import org.atechtrade.rent.enums.ReservationStatus;
import org.atechtrade.rent.model.Contractor;
import org.atechtrade.rent.model.RentalItem;
import org.atechtrade.rent.model.Reservation;
import org.atechtrade.rent.repository.ContractorRepository;
import org.atechtrade.rent.repository.GeneratorSequenceRepository;
import org.atechtrade.rent.repository.ReservationRepository;
import org.atechtrade.rent.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ReservationService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String adminEmail = "rayat_ahtopol@abv.bg";

    private final ReservationRepository repository;
    private final RentalItemService rentalItemService;
    private final GeneratorSequenceRepository generatorSequenceRepository;
    private final ContractorRepository contractorRepository;

    public Reservation findById(final Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> findAllByStatusAndYear(final ReservationStatus status, final Integer year) {
        return ReservationDTO.of(repository.findAllByStatusAndYear(status, year));
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> findAllByRentalItemIdAndStatusAndYear(final Long rentalItemId, final Integer year) {
        return ReservationDTO.of(repository.findAllByRentalItemIdStatusAndYear(ReservationStatus.CONFIRMED, rentalItemId, year));
    }

    @Transactional(readOnly = true)
    public ReservationDTO findDTOById(final Long id) {
        return ReservationDTO.of(findById(id));
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> findAllByRentalItemId(final Long rentalItem) {
        return ReservationDTO.of(repository.findAllByRentalItem_Id(rentalItem));
    }

    @Transactional
    public Reservation create(final Long rentalItemId, final ReservationDTO dto, final String language) {
        if (!dto.getTermsAccepted()) {
            throw new IllegalArgumentException("You must accept terms and conditions!");
        }

        Contractor contractor = new Contractor();
        if(dto.getContractor().getEmail() != null) {
             contractor = contractorRepository.findByEmail(dto.getContractor().getEmail()).orElse(null);
        }
        if (contractor == null) {
            contractor = new Contractor();
            BeanUtils.copyProperties(dto.getContractor(), contractor, "id");
            contractor.setMarketingTarget(true);
        }

        contractor.setEnabled(true);

        contractorRepository.save(contractor);

        RentalItem rentalItem = rentalItemService.findById(rentalItemId);
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(dto, reservation, "id");

        reservation.setReservationNumber(generateUniqueReservationNumber());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setRentalItem(rentalItem);
        reservation.setContractor(contractor);
        rentalItem.getReservations().add(reservation);

        reservation = repository.save(reservation);

        return reservation;
    }

    @Transactional
    public Reservation update(final Long id, final ReservationDTO dto) {
        if (!CommonUtils.isValidId(id)) {
            throw new IllegalArgumentException("Path variable id is not valid");
        }
        if (!id.equals(dto.getId())) {
            throw new IllegalArgumentException("Path variable id must match DTO id");
        }

        // TODO check if the new reservation period is available

        Reservation reservation = findById(id);
        BeanUtils.copyProperties(dto, reservation);

        return repository.save(reservation);
    }

    @Transactional
    public Reservation confirm(final Long id, final String language) {
        if (!CommonUtils.isValidId(id)) {
            throw new IllegalArgumentException("Path variable id is not valid");
        }
        Reservation reservation = findById(id);

        if (isReservationAvailable(reservation.getRentalItem().getId(), ReservationStatus.CONFIRMED, reservation.getFromDate(), reservation.getToDate()) == 0) {
            reservation.setStatus(ReservationStatus.CONFIRMED);

            reservation = repository.save(reservation);

//            mailService.sendConfirmReservation(reservation, language);

            return reservation;
        } else {
            throw new IllegalArgumentException("The selected date range is unavailable for reservation.");
        }
    }

    @Transactional
    public Reservation reject(final Long id, final String language) {
        if (!CommonUtils.isValidId(id)) {
            throw new IllegalArgumentException("Path variable id is not valid");
        }

        Reservation reservation = findById(id);
        reservation.setStatus(ReservationStatus.REJECTED);

//        mailService.sendRejectReservation(reservation, language);

        return repository.save(reservation);
    }

    @Transactional
    public void setPaid(final Long id, final Double paid) {
        Reservation reservation = findById(id);
        reservation.setPaid(paid);
        repository.save(reservation);
    }

    public Long isReservationAvailable(
            final Long rentalItemId,
            final ReservationStatus status,
            final LocalDate fromDate,
            final LocalDate toDate
    ) {
        return repository.countReservationWithFromDateInRange(rentalItemId, status, fromDate, toDate);
    }

    public String generateUniqueReservationNumber() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                generatorSequenceRepository.nextVal();
    }
}

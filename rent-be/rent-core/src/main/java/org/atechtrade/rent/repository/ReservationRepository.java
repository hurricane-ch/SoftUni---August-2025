package org.atechtrade.rent.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.atechtrade.rent.enums.ReservationStatus;
import org.atechtrade.rent.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Hidden
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByReservationNumber(String reservationNumber);

    List<Reservation> findAllByRentalItem_Id(Long rentalItemId);

    @Query("SELECT r FROM Reservation r WHERE r.rentalItem.id = :rentalItemId AND YEAR(r.fromDate) = :year")
    List<Reservation> findAllByRentalItemIdAndYear(@Param("rentalItemId") Long rentalItemId, @Param("year") Integer year);

    @Query("SELECT r FROM Reservation r " +
            "WHERE (:status IS NULL OR r.status = :status) " +
            "AND (:year IS NULL OR YEAR(r.reservationDate) = :year) " +
            "ORDER BY r.id ASC")
    List<Reservation> findAllByStatusAndYear(@Param("status") ReservationStatus status,
                                             @Param("year") Integer year);

    @Query("SELECT r FROM Reservation r " +
            "WHERE  r.status = :status " +
            "AND r.rentalItem.id = :rentalItemId " +
            "AND (:year IS NULL OR YEAR(r.reservationDate) = :year) " +
            "ORDER BY r.rentalItem.id ASC")
    List<Reservation> findAllByRentalItemIdStatusAndYear(@Param("status") ReservationStatus status,
                                                         @Param("rentalItemId") Long rentalItemId,
                                                         @Param("year") Integer year);

//    @Query("SELECT COUNT(r) FROM Reservation r " +
//            "WHERE r.rentalItem.id = :rentalItemId " +
//            "AND r.status = :status " +
//            "AND (r.fromDate BETWEEN :fromDate AND :toDate " +
//            "OR r.toDate BETWEEN :fromDate AND :toDate " +
//            "OR (r.fromDate <= :fromDate AND r.toDate >= :toDate))")
//    long countReservationWithFromDateInRange(
//            @Param("rentalItemId") Long rentalItemId,
//            @Param("status") ReservationStatus status,
//            @Param("fromDate") LocalDate fromDate,
//            @Param("toDate") LocalDate toDate);
//}

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.rentalItem.id = :rentalItemId " +
            "AND r.status = :status " +
            "AND (r.fromDate < :toDate AND r.toDate > :fromDate)")
    long countReservationWithFromDateInRange(
            @Param("rentalItemId") Long rentalItemId,
            @Param("status") ReservationStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}

//    @Query("""
//        SELECT r
//        FROM Reservation r
//        WHERE r.rentalItem.id = :rentalItemId
//        AND r.status = :status
//        AND r.fromDate < :endDate
//        AND r.toDate > :startDate
//        """)
//    List<Reservation> findReservationsWithFromDateInRange(
//            @Param("rentalItemId") Long rentalItemId,
//            @Param("status") ReservationStatus status,
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate);
//
//}

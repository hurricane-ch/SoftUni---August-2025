package org.atechtrade.rent.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.atechtrade.rent.enums.ReservationStatus;
import org.atechtrade.rent.model.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Hidden
@Repository
public interface RentalItemRepository extends JpaRepository<RentalItem, Long> {
    List<RentalItem> findAllByOrderByIdAsc();

    List<RentalItem> findAllByOrderByRecommendedVisitorsAsc();

    @Query("SELECT DISTINCT i FROM RentalItem i " +
            "JOIN i.reservations r " +
            "WHERE r.status = :status " +
            "AND (:year IS NULL OR YEAR(r.fromDate) = :year)"
    )
    List<RentalItem> findAllByReservationsStatusAndYear(@Param("status") ReservationStatus status,
                                                        @Param("year") Integer year);

}

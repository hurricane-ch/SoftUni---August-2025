package org.atechtrade.rent.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.atechtrade.rent.enums.RentalItemType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rental_items")
public class RentalItem extends BaseEntity {

    @Column(name = "rental_item_type")
    @Enumerated(EnumType.STRING)
    private RentalItemType rentalItemType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "room", nullable = false)
    @PositiveOrZero
    private Integer room;

    @Column(name = "price")
    @PositiveOrZero
    private Double price;

    @Column(name = "recommended_visitors")
    private Integer recommendedVisitors;

    @Column(name = "enabled")
    private boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(max = 10)
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "rentalItem", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
}

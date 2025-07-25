package org.atechtrade.rent.model;

import org.atechtrade.rent.enums.ContactType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Audited
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contacts")
public class Contact extends BaseEntity {

    @Column(name = "contact_type")
    @Enumerated(EnumType.STRING)
    private ContactType contactType;

    @Column(name = "name")
    private String name;

    @Column(name = "description", length = 5000)
    private String description;

}

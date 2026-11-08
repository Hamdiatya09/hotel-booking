package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** Optional paid service added to a stay (breakfast, airport transfer, spa...). */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Extra name is required")
    @Column(unique = true)
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;

    // Inverse side of the ManyToMany
    @ManyToMany(mappedBy = "extras")
    @JsonIgnore
    private Set<Reservation> reservations = new HashSet<>();
}

package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Catégorie de chambre (simple, double, suite...) avec son prix de base et sa capacité.
 * <p>
 * Relation OneToMany avec {@link Room} : un type de chambre est partagé par plusieurs chambres.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RoomType {

    /** Identifiant technique généré automatiquement par la base (auto-incrément). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du type : obligatoire et unique. */
    @NotBlank(message = "Room type name is required")
    @Column(unique = true)
    private String name;

    /** Description libre (facultative). */
    private String description;

    /** Prix de base par nuit : obligatoire et strictement positif. */
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be positive")
    private BigDecimal basePrice;

    /** Nombre maximum de personnes : obligatoire, au moins 1. */
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    /**
     * Chambres de ce type (côté inverse, la clé étrangère room_type_id est dans Room).
     * {@code @JsonIgnore} évite une boucle infinie en JSON.
     */
    @OneToMany(mappedBy = "roomType")
    @JsonIgnore
    private List<Room> rooms = new ArrayList<>();
}

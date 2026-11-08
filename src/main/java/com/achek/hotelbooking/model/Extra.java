package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Service supplémentaire payant ajouté à un séjour (petit-déjeuner, transfert aéroport, spa...).
 * <p>
 * Relation ManyToMany avec {@link Reservation} : une réservation peut avoir plusieurs extras
 * et un extra peut figurer dans plusieurs réservations. Cette classe est le côté inverse
 * (mappedBy) ; la table de jointure {@code reservation_extra} est définie dans Reservation.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Extra {

    /** Identifiant technique généré automatiquement par la base (auto-incrément). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du service : obligatoire et unique (pas deux extras avec le même nom). */
    @NotBlank(message = "Extra name is required")
    @Column(unique = true)
    private String name;

    /** Prix du service : obligatoire, ne peut pas être négatif. */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;

    /**
     * Côté inverse de la relation ManyToMany : mappedBy = "extras" indique que la jointure
     * est gérée par le champ {@code extras} de Reservation.
     * {@code @JsonIgnore} évite une boucle infinie lors de la sérialisation en JSON.
     */
    @ManyToMany(mappedBy = "extras")
    @JsonIgnore
    private Set<Reservation> reservations = new HashSet<>();
}

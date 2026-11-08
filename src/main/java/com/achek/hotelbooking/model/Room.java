package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Chambre d'un hôtel.
 * <p>
 * Relations : ManyToOne vers {@link Hotel} et vers {@link RoomType}, OneToMany vers {@link Reservation}.
 * La contrainte d'unicité (hotel_id, number) interdit deux chambres avec le même numéro dans un même hôtel.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Room {

    /** Identifiant technique généré automatiquement par la base (auto-incrément). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numéro de la chambre (texte, ex. "101") : obligatoire, unique au sein d'un hôtel. */
    @NotBlank(message = "Room number is required")
    private String number;

    /** Étage : obligatoire, ne peut pas être négatif (0 = rez-de-chaussée). */
    @NotNull(message = "Floor is required")
    @Min(value = 0, message = "Floor cannot be negative")
    private Integer floor;

    /** État de la chambre (voir {@link RoomStatus}) : ACTIVE par défaut. */
    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.ACTIVE;

    /** Hôtel de la chambre. Plusieurs chambres -> un hôtel ; la clé étrangère hotel_id est ici. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id")
    @NotNull(message = "Hotel is required")
    private Hotel hotel;

    /** Type de la chambre (simple, double...). Plusieurs chambres -> un type ; clé étrangère room_type_id. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_type_id")
    @NotNull(message = "Room type is required")
    private RoomType roomType;

    /**
     * Réservations de cette chambre (côté inverse, la clé étrangère est dans Reservation).
     * {@code @JsonIgnore} évite une boucle infinie en JSON.
     */
    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();
}

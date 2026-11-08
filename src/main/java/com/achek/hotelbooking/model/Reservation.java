package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Réservation d'une chambre par un client pour une période donnée.
 * <p>
 * C'est l'entité centrale du modèle :
 * <ul>
 *   <li>ManyToOne vers {@link Guest} (le client) et vers {@link Room} (la chambre) ;</li>
 *   <li>ManyToMany vers {@link Extra} (services supplémentaires), via la table {@code reservation_extra} ;</li>
 *   <li>OneToMany vers {@link Payment} (les paiements).</li>
 * </ul>
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Reservation {

    /** Identifiant technique généré automatiquement par la base (auto-incrément). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Date d'arrivée : obligatoire. */
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    /** Date de départ : obligatoire (le service vérifie qu'elle est après l'arrivée). */
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    /** Statut de la réservation (voir {@link ReservationStatus}), stocké en texte en base. */
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    /** Prix total, calculé par le service : nombre de nuits x prix de base de la chambre + extras. */
    private BigDecimal totalPrice;

    /** Client qui réserve. Plusieurs réservations -> un client ; la clé étrangère guest_id est ici. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "guest_id")
    @NotNull(message = "Guest is required")
    private Guest guest;

    /** Chambre réservée. Plusieurs réservations -> une chambre ; la clé étrangère room_id est ici. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    @NotNull(message = "Room is required")
    private Room room;

    /**
     * Côté propriétaire de la relation ManyToMany avec Extra : c'est ici que la table de jointure
     * {@code reservation_extra} (colonnes reservation_id et extra_id) est définie.
     * {@code FetchType.EAGER} charge les extras avec la réservation (petite collection,
     * évite une LazyInitializationException à la sérialisation en JSON).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "reservation_extra",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "extra_id"))
    private Set<Extra> extras = new HashSet<>();

    /**
     * Paiements de cette réservation (côté inverse, la clé étrangère est dans Payment).
     * {@code @JsonIgnore} évite une boucle infinie en JSON.
     */
    @OneToMany(mappedBy = "reservation")
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();
}

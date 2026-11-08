package com.achek.hotelbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Paiement effectué pour une réservation. Une réservation peut être réglée
 * en plusieurs paiements (relation ManyToOne vers {@link Reservation}).
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Payment {

    /** Identifiant technique généré automatiquement par la base (auto-incrément). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Montant payé : obligatoire et strictement positif (inclusive = false exclut 0). */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    /** Moyen de paiement (voir {@link PaymentMethod}), stocké en texte en base. */
    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    /** Date et heure du paiement. */
    private LocalDateTime paymentDate;

    /**
     * Réservation concernée. Plusieurs paiements -> une réservation : ce côté porte
     * la clé étrangère reservation_id. {@code optional = false} la rend obligatoire.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    @NotNull(message = "Reservation is required")
    private Reservation reservation;
}

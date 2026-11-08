package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Client de l'hôtel : la personne qui effectue une ou plusieurs réservations.
 * <p>
 * Relation OneToMany avec {@link Reservation} : un client peut avoir plusieurs réservations.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Guest {

    /** Identifiant technique généré automatiquement par la base (auto-incrément). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Prénom du client : obligatoire. */
    @NotBlank(message = "First name is required")
    private String firstName;

    /** Nom de famille du client : obligatoire. */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /** Adresse e-mail : obligatoire, au format valide et unique en base. */
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(unique = true)
    private String email;

    /** Téléphone (facultatif) : 8 à 15 chiffres ou espaces, avec un + optionnel au début. */
    @Pattern(regexp = "^\\+?[0-9 ]{8,15}$", message = "Phone number format is invalid")
    private String phone;

    /**
     * Côté inverse de la relation : le client n'a pas de clé étrangère, c'est la table
     * Reservation qui porte la colonne guest_id (mappedBy = "guest").
     * {@code @JsonIgnore} évite une boucle infinie en JSON.
     */
    @OneToMany(mappedBy = "guest")
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();
}

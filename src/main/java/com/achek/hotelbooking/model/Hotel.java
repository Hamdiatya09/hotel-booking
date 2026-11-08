package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Hôtel de la chaîne.
 * <p>
 * Relation OneToMany avec {@link Room} : un hôtel possède plusieurs chambres.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Hotel {

    /** Identifiant technique généré automatiquement par la base (auto-incrément). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de l'hôtel : obligatoire. */
    @NotBlank(message = "Hotel name is required")
    private String name;

    /** Ville où se trouve l'hôtel : obligatoire (utilisée pour la recherche par ville). */
    @NotBlank(message = "City is required")
    private String city;

    /** Adresse postale : obligatoire. */
    @NotBlank(message = "Address is required")
    private String address;

    /** Nombre d'étoiles : obligatoire, compris entre 1 et 5. */
    @NotNull(message = "Stars are required")
    @Min(value = 1, message = "Stars must be between 1 and 5")
    @Max(value = 5, message = "Stars must be between 1 and 5")
    private Integer stars;

    /**
     * Un hôtel -> plusieurs chambres (côté inverse, la clé étrangère hotel_id est dans Room).
     * {@code @JsonIgnore} évite une boucle infinie en JSON.
     * {@code CascadeType.REMOVE} : supprimer l'hôtel supprime aussi ses chambres.
     */
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Room> rooms = new ArrayList<>();
}

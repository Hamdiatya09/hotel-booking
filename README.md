# Hotel Booking API – DS1 Développement Web (Spring Boot)

API REST de **gestion de réservations d'hôtel** : hôtels, chambres, clients, réservations, services supplémentaires et paiements.

## 1. Contexte métier

Une chaîne d'hôtels veut gérer ses chambres et ses réservations. Un **client** réserve une **chambre** d'un **hôtel** pour une période donnée, peut ajouter des **services supplémentaires** (petit-déjeuner, transfert aéroport…) et règle sa réservation par un ou plusieurs **paiements**.

Règles métier implémentées dans la couche service :

- Check-out strictement après check-in, check-in non passé à la création.
- Une chambre ne peut pas être réservée deux fois sur des dates qui se chevauchent (409).
- Une chambre en maintenance n'est pas réservable.
- Prix total = nombre de nuits × prix de base du type de chambre + somme des extras.
- Un paiement ne peut pas dépasser le reste à payer ni concerner une réservation annulée.
- Quand la réservation est payée en totalité, son statut passe de `PENDING` à `CONFIRMED`.
- Une réservation commencée ou déjà annulée ne peut pas être annulée.

## 2. Technologies

Java 17 · Spring Boot 3.3 · Spring Data JPA (Hibernate) · Bean Validation · MySQL · Lombok · Postman · JUnit 5 / Mockito

## 3. Modèle de données (7 entités)

| Entité | Attributs principaux | Relations |
| --- | --- | --- |
| `Hotel` | name, city, address, stars | OneToMany → Room |
| `RoomType` | name, description, basePrice, capacity | OneToMany → Room |
| `Room` | number, floor, status | ManyToOne → Hotel, ManyToOne → RoomType, OneToMany → Reservation |
| `Guest` | firstName, lastName, email (unique), phone | OneToMany → Reservation |
| `Reservation` | checkIn, checkOut, status, totalPrice | ManyToOne → Guest, ManyToOne → Room, **ManyToMany ↔ Extra**, OneToMany → Payment |
| `Extra` | name, price | ManyToMany ↔ Reservation (table `reservation_extra`) |
| `Payment` | amount, method, paymentDate | ManyToOne → Reservation |

```
Hotel 1──* Room *──1 RoomType
            │1
            *
Guest 1──* Reservation *──* Extra
            │1
            *
         Payment
```

## 4. Architecture

```
src/main/java/com/achek/hotelbooking/
 ├─ model/        entités JPA + enums
 ├─ repository/   Spring Data JPA + requêtes personnalisées
 ├─ service/      logique métier (CrudService générique + services spécialisés)
 ├─ controller/   API REST (CrudController générique + endpoints spécifiques)
 ├─ exception/    exceptions métier + @RestControllerAdvice
 ├─ dto/          projections pour les requêtes statistiques
 ├─ config/       DataInitializer (jeu de données de démarrage)
 └─ HotelBookingApplication.java
```

## 5. Endpoints

CRUD standard (même contrat pour chaque ressource) : `GET /api/{res}`, `GET /api/{res}/{id}`, `POST /api/{res}` (201), `PUT /api/{res}/{id}`, `DELETE /api/{res}/{id}` (204)
avec `{res}` ∈ `hotels`, `room-types`, `rooms`, `guests`, `reservations`, `extras`, `payments`.

Endpoints spécifiques (requêtes personnalisées) :

| Méthode | URL | Besoin métier | Requête |
| --- | --- | --- | --- |
| GET | `/api/hotels/city/{city}` | Hôtels d'une ville | dérivée `findByCityIgnoreCase` |
| GET | `/api/rooms/search?city=&capacity=` | Chambres d'une ville pouvant accueillir N personnes | dérivée (propriétés imbriquées) |
| GET | `/api/rooms/available?hotelId=&checkIn=&checkOut=` | Chambres libres sur une période | JPQL + `NOT EXISTS` |
| GET | `/api/reservations/guest/{id}` | Historique d'un client | dérivée |
| GET | `/api/reservations/period?from=&to=` | Réservations débutant dans une période | dérivée `Between` |
| GET | `/api/guests/top?limit=3` | Clients les plus actifs | JPQL + `GROUP BY` + projection |
| GET | `/api/payments/stats/revenue-by-hotel` | Chiffre d'affaires par hôtel | JPQL + jointures + `SUM` |
| GET | `/api/payments/reservation/{id}` | Paiements d'une réservation | dérivée |
| PATCH | `/api/reservations/{id}/cancel` | Annuler une réservation | logique service |

### Exemple : créer une réservation

```json
POST /api/reservations
{
  "checkIn": "2027-03-10",
  "checkOut": "2027-03-13",
  "guest": { "id": 1 },
  "room": { "id": 1 },
  "extras": [ { "id": 1 } ]
}
```

## 6. Gestion des erreurs

Toutes les erreurs renvoient le même JSON `{timestamp, status, error, message, details}` :

| Code | Cas |
| --- | --- |
| 400 | validation (`@NotBlank`, `@Min`, `@Email`…), JSON/date/enum invalide, règle métier violée |
| 404 | ressource ou référence (hôtel, client, chambre…) introuvable |
| 409 | chambre déjà réservée, doublon (email, numéro de chambre…), suppression d'une entité encore référencée |

## 7. Lancer le projet

Prérequis : JDK 17+, Maven 3.9+, MySQL 8 démarré en local.

1. Vérifier `spring.datasource.username/password` dans `src/main/resources/application.properties` (la base `hotel_db` est créée automatiquement).
2. `mvn spring-boot:run`
3. L'API écoute sur `http://localhost:8080`. Au premier démarrage, `DataInitializer` insère 2 hôtels, 3 types de chambre, 5 chambres, 2 clients et 3 extras.

Tests unitaires : `mvn test`

> Pour PostgreSQL : remplacer la dépendance `mysql-connector-j` par `org.postgresql:postgresql` et adapter l'URL/driver dans `application.properties`.

## 8. Tests Postman

1. Importer `postman/HotelBooking.postman_collection.json`.
2. Lancer l'application (base vierge pour que les IDs du jeu de données correspondent).
3. Exécuter le **Collection Runner** : les dossiers 1 à 7 s'enchaînent, les IDs créés sont mémorisés dans des variables, et chaque requête contient un test (code HTTP, total calculé, statut). Les requêtes `ERROR - ...` vérifient les cas 400 / 404 / 409.
4. Faire des captures d'écran des tests réussis et les placer dans `docs/screenshots/`.

## 9. Auteur

## Équipe et répartition du travail

| Membre | Partie |
|---|---|
| Mohamed Hamdi Atya | Configuration (pom.xml, application.properties, DataInitializer), README, gestion du dépôt GitHub |
| Skander Ben Amara | Modèle de données : entités, annotations JPA et relations |
| Ibrahim Khalil Achek | Repositories, requêtes personnalisées et services (règles métier) |
| Karim Ben Amor | Controllers REST, gestion des erreurs et tests Postman |

## Compatibilité

Le projet utilise Spring Boot 3.5.7 et Lombok 1.18.42 pour fonctionner avec Java 17 à Java 25.

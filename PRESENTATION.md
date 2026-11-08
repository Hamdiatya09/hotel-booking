# Présentation (5 min) – plan de 5 diapositives

**Répartition suggérée pour un groupe : un orateur par diapositive.**

## 1. Contexte et objectif (45 s)
- Besoin : gérer chambres et réservations d'une chaîne d'hôtels.
- Livrable : API REST Spring Boot, MySQL, testée avec Postman.
- Phrase clé : « Une réservation ne doit jamais être possible si la chambre est déjà prise. »

## 2. Modèle de données (1 min)
- 7 entités : Hotel, RoomType, Room, Guest, Reservation, Extra, Payment.
- OneToMany / ManyToOne : Hotel→Room, Guest→Reservation, Reservation→Payment…
- **ManyToMany : Reservation ↔ Extra** (table de jointure `reservation_extra`).
- Choix : côté inverse en `@JsonIgnore` pour éviter les boucles JSON infinies ; `mappedBy` pour n'avoir qu'une clé étrangère.

## 3. Architecture en couches (1 min)
- model → repository → service → controller.
- `CrudService` / `CrudController` génériques : le CRUD n'est écrit qu'une fois, chaque service ajoute ses règles.
- Les règles métier vivent dans le service, jamais dans le controller.

## 4. Requêtes personnalisées et règles métier (1 min 15 s)
- Chambres disponibles : JPQL avec `NOT EXISTS` (chevauchement : `checkIn < :fin AND checkOut > :début`).
- Clients les plus actifs : `GROUP BY` + projection d'interface.
- Chiffre d'affaires par hôtel : jointures + `SUM`.
- Prix total calculé côté serveur ; paiement plafonné au reste à payer ; réservation `CONFIRMED` quand tout est payé.

## 5. Démo Postman et erreurs (1 min)
- Créer une réservation → 201 avec `totalPrice`.
- Même chambre, mêmes dates → **409**.
- Hôtel avec `stars = 9` → **400** avec la liste des champs invalides.
- Hôtel inexistant → **404**.
- Paiement trop élevé → **400**, paiement final → statut `CONFIRMED`.

## Questions probables
- Pourquoi `FetchType.EAGER` sur `extras` ? → petite collection, évite `LazyInitializationException` à la sérialisation.
- Pourquoi `@Transactional(readOnly = true)` sur le service ? → optimisation Hibernate en lecture, les écritures surchargent avec `@Transactional`.
- Différence `@ManyToOne` / `@OneToMany(mappedBy)` ? → le côté `ManyToOne` détient la clé étrangère.
- Pourquoi charger `guest`/`room` depuis la base dans le service ? → le JSON ne contient que `{ "id": 1 }`, il faut l'entité complète (prix, statut) pour les règles métier.
- Pourquoi `ddl-auto=update` ? → pratique en développement ; en production on utiliserait Flyway/Liquibase.

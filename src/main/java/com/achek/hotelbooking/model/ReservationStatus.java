package com.achek.hotelbooking.model;

/**
 * Cycle de vie d'une réservation :
 * PENDING (en attente de paiement) -> CONFIRMED (entièrement payée) -> CHECKED_IN (séjour commencé)
 * -> CHECKED_OUT (séjour terminé) ; CANCELLED si elle est annulée.
 */
public enum ReservationStatus { PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED }

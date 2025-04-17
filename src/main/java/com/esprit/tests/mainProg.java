package com.esprit.tests;

import com.esprit.modules.Materiel;
import com.esprit.modules.Espace;
import com.esprit.modules.ReservationEspace;
import com.esprit.services.MaterielService;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;

import java.util.Date;

public class mainProg {
    public static void main(String[] args) {
        // Service pour les matériels
        MaterielService materielService = new MaterielService();

        Materiel m1 = new Materiel(1, "Matériel 1", "Type 1", 10, "Description 1", true);
        Materiel m2 = new Materiel(2, "Matériel 2", "Type 2", 20, "Description 2", false);

        // Test des méthodes sur le matériel
       // materielService.add(m1);
         materielService.update(m2);
         System.out.println(materielService.get());
        // materielService.delete(m2);

        // Service pour les espaces
        EspaceService espaceService = new EspaceService();
        Espace es1 = new Espace(1, "Espace 1", "Type 1", 1000.0, 10, "Localisation 1");
        Espace es2 = new Espace(2, "Espace 2", "Type 2", 2000.0, 20, "Localisation 2");

        // Test des méthodes sur l'espace
        // espaceService.add(es1);
        // espaceService.add(es2);
        // System.out.println(espaceService.get());
        // espaceService.delete(es1);

        // Service pour les réservations d'espaces
        ReservationEspaceService reservationEspaceService = new ReservationEspaceService();

        // Exemple : userId = 1 (doit exister dans la table `user`)
        ReservationEspace re1 = new ReservationEspace(es1, new Date(), new Date(), 1, null);
        ReservationEspace re2 = new ReservationEspace(es2, new Date(), new Date(), 1, null);

        // Test des méthodes sur la réservation d'espaces
        // reservationEspaceService.add(re1);
        // reservationEspaceService.add(re2);
        // System.out.println(reservationEspaceService.get());
        // reservationEspaceService.delete(re2);
    }
}

package com.esprit.tests;

import com.esprit.models.Espace;
import com.esprit.models.ReservationEspace;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;

import java.time.LocalDate;

public class MainProg {
    public static void main(String[] args) {

        // Service pour les espaces
        EspaceService espaceService = new EspaceService();

        Espace es1 = new Espace(1, "Salle des Fêtes Evencia Tunis", "Mariage", 400, "Tunis", 150.0, true);  // Salle ouverte
        Espace es2 = new Espace(2, "Centre de Conférences Evencia Sfax", "Conférence professionnelle", 250, "Sfax", 120.0, true);  // Salle ouverte
        Espace es3 = new Espace(3, "Espace Evencia Hammamet", "Événement culturel", 300, "Hammamet", 100.0, false);  // Salle fermée
        Espace es4 = new Espace(4, "Salle Evencia Sousse", "Anniversaire", 200, "Sousse", 80.0, true);  // Salle ouverte
        Espace es5 = new Espace(5, "Salle des Fêtes Evencia Nabeul", "Événement familial", 500, "Nabeul", 200.0, false);  // Salle fermée

        // Test des méthodes sur l'espace
       // espaceService.add(es1);
        //espaceService.add(es2);
       // espaceService.add(es3);
        //espaceService.add(es4);
      //  espaceService.add(es5);
       //System.out.println(espaceService.getAll());
      // espaceService.delete(es5.getId());

        // Service pour les réservations d'espaces
        ReservationEspaceService ReservationEspaceService  = new ReservationEspaceService();

        //ReservationEspace re1 = new ReservationEspace(1, es1.getId(), "Ali Ben Ali", LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 12));  // Mariage à la Salle des Fêtes Evencia Tunis
        //ReservationEspace re2 = new ReservationEspace(2, es2.getId(), "Amira Zghal", LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 6));  // Conférence professionnelle au Centre de Conférences Evencia Sfax
        //ReservationEspace re3 = new ReservationEspace(3, es3.getId(), "Hassan Tounsi", LocalDate.of(2025, 7, 15), LocalDate.of(2025, 7, 17));  // Événement culturel à l'Espace Evencia Hammamet
        //ReservationEspace re4 = new ReservationEspace(4, es4.getId(), "Sana Belhaj", LocalDate.of(2025, 8, 20), LocalDate.of(2025, 8, 21));  // Anniversaire à la Salle Evencia Sousse
        //ReservationEspace re5 = new ReservationEspace(5, es5.getId(), "Mounir Ziani", LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 12));  // Événement familial à la Salle des Fêtes Evencia Nabeul

        // Test des méthodes sur la réservation
       //ReservationEspaceService.add(re1);
      // ReservationEspaceService.add(re2);
      //  ReservationEspaceService.add(re3);
       //  ReservationEspaceService.add(re4);
      //   ReservationEspaceService.add(re5);
      //System.out.println(ReservationEspaceService.getAll());
       // ReservationEspaceService.delete (re2.getReservationId());
    }

}

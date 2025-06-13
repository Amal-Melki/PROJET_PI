package com.esprit.tests;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import com.esprit.modules.Fournisseur;
import com.esprit.services.ServiceFournisseur;
import com.esprit.modules.ReservationMateriel;
import com.esprit.services.ServiceReservationMateriel;

import java.sql.Date;


public class Main {
    public static void main(String[] args) {
        ServiceMateriel serviceMateriel = new ServiceMateriel();
        ServiceFournisseur servicefournisseur = new ServiceFournisseur();
        ServiceReservationMateriel servicereservation = new ServiceReservationMateriel();
        // Ajouter un matériel
      //  Materiels materiel = new Materiels("Enceinte JBL", "Audio", 10, "DISPONIBLE", "Enceinte Bluetooth puissante");
       //serviceMateriel.ajouter(materiel);

        // Afficher tous les matériels
        System.out.println("Liste des matériels :");
        for (Materiels m : serviceMateriel.recuperer()) {
          System.out.println(m);
       }
        Fournisseur f = new Fournisseur("TechPro", "contact@techpro.com", "22554411", "Tunis, Centre Urbain Nord");
        servicefournisseur.ajouter(f);

        // Affichage test
        for (Fournisseur fournisseur : servicefournisseur.recuperer()) {
            System.out.println(fournisseur);
        }

        ReservationMateriel r = new ReservationMateriel(1,
                1, // ID d’un matériel déjà existant
                Date.valueOf("2024-04-01").toLocalDate(),
                Date.valueOf("2024-04-03").toLocalDate(),
                3,
                "rahma testing"
        );
        servicereservation.modifier(r);

        // Affichage
        for (ReservationMateriel res : servicereservation.recuperer()) {
            System.out.println(res);
        }






    }
}


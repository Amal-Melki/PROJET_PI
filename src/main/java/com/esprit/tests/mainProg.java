package com.esprit.tests;

import com.esprit.modules.Equipement;
import com.esprit.modules.ReservationEspace;
import com.esprit.services.EquipementService;
import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;
import com.esprit.services.ReservationEspaceService;
import java.util.Date;

public class mainProg {
    public static void main(String[] args) {
        EquipementService equipementService = new EquipementService();
        Equipement eq1 = new Equipement(1, "equipement 1", "type 1", 10, "description 1", true);
        Equipement eq2 = new Equipement(2, "equipement 2", "type 2", 20, "description 2", false);

        // equipementService.add(eq1);
       // equipementService.update(eq2);
      //  System.out.println(equipementService.get());
       // equipementService.delete(eq2);

        EspaceService espaceService = new EspaceService();
        Espace es1 = new Espace(1, "espace 1", "type 1", 1000.0, 10, "localisation 1");
        Espace es2 = new Espace(2, "espace 2", "type 2", 2000.0, 20, "localisation 2");
        //espaceService.add(es1);
        //espaceService.add(es2);
        //System.out.println(espaceService.get());
        //espaceService.delete(es1);

        ReservationEspaceService reservationEspaceService = new ReservationEspaceService();
        ReservationEspace re1 = new ReservationEspace(1, es1, new Date(), new Date(), "ahmed", null);
        ReservationEspace re2 = new ReservationEspace(2, es2, new Date(), new Date(), "ahmed", null);
       // reservationEspaceService.add(re1);
       // reservationEspaceService.add(re2);
      // System.out.println(reservationEspaceService.get());
       //reservationEspaceService.delete(re2);
    }
}

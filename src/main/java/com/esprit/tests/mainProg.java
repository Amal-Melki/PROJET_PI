package com.esprit.tests;

import com.esprit.modules.Equipement;
import com.esprit.services.EquipementService;
import com.esprit.services.EspaceService;

public class mainProg {
    public static void main(String[] args) {
         EquipementService eqipmentService = new EquipementService();
        Equipement eq1 = new Equipement(1, "Type 1", "car", 1, "aaaa", true);
        Equipement eq2 = new Equipement( 2, "Type 2", "moto", 2, "bbbb", false);
        //eqipmentService.add(eq1);
        //eqipmentService.add(eq2);
        //System.out.println(eqipmentService.get());
        eqipmentService.delete(eq1);



    }
}

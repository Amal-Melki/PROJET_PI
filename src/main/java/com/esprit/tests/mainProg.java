package com.esprit.tests;

import com.esprit.modules.Equipement;
import com.esprit.services.EquipementService;
import com.esprit.modules.Espace;
import com.esprit.services.EspaceService;

public class mainProg {
    public static void main(String[] args) {
        EquipementService equipementService = new EquipementService();
        Equipement eq1 = new Equipement(1, "Type 1", "car", 1, "aaaa", true);
        Equipement eq2 = new Equipement( 2, "Type 2", "moto", 2, "bbbb", false);
        //equipementService.add(eq1);
        //equipementService.add(eq2);
        //System.out.println(equipementService.get());
        equipementService.delete(eq1);

        EspaceService espaceService = new EspaceService();
        Espace es1 = new Espace(1, "espace 1", "salon", 1500, 50, "Hammamet");
        Espace es2 = new Espace( 2, "espace 2", "maison D'hotes", 2500, 100, "Tunis");
        //espaceService.add(es1);
        //espaceService.add(es2);
        //System.out.println(espaceService.get());
        espaceService.delete(es1);


    }
}

package com.esprit.tests;

import com.esprit.models.Personne;
import com.esprit.services.PersonneService;
import com.esprit.utils.DataSource;

public class MainProg {
    public static void main(String[] args) {
        PersonneService ps = new PersonneService();
//        ps.ajouter(new Personne("Fedi", "Ali"));
//        ps.modifier(new Personne(2,"Youssef", "Ahmed"));
//        ps.supprimer(new Personne(2,"", ""));
        System.out.println(ps.rechercher());
    }
}

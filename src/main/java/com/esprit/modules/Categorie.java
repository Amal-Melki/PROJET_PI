package com.esprit.modules;

public class Categorie {

        private int id_categorie;
        private String nom;
        private String description;


        public Categorie(int id_categorie, String nom, String description) {
            this.id_categorie = id_categorie;
            this.nom = nom;
            this.description = description;
        }

    public Categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }
    // Getters & Setters
    }


package com.esprit.modules.gestionespaces.entities;

    public class Espace {
        private int id;
        private String nom;
        private String description;
        private int capacite;
        private String localisation;

        // Constructeur
        public Espace(int id, String nom, String description, int capacite, String localisation) {
            this.id = id;
            this.nom = nom;
            this.description = description;
            this.capacite = capacite;
            this.localisation = localisation;
        }

        // Getters et Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCapacite() {
            return capacite;
        }

        public void setCapacite(int capacite) {
            this.capacite = capacite;
        }

        public String getLocalisation() {
            return localisation;
        }

        public void setLocalisation(String localisation) {
            this.localisation = localisation;
        }

        // Méthode toString
        @Override
        public String toString() {
            return "Espace [id=" + id + ", nom=" + nom + ", description=" + description + ", capacite=" + capacite
                    + ", localisation=" + localisation + "]";
        }
    }



}
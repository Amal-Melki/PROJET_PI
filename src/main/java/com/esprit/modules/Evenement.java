package com.esprit.modules;
import java.util.Date;
public class Evenement {



    public class Evenement {
        private int id;
        private String titre;
        private String description;
        private Date dateDebut;
        private Date dateFin;
        private String lieu;

        public Evenement(int id, String titre, String description, Date dateDebut, Date dateFin, String lieu) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.lieu = lieu;
        }

        public Evenement(String titre, String description, Date dateDebut, Date dateFin, String lieu) {
            this.titre = titre;
            this.description = description;
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.lieu = lieu;
        }

        // Getters et Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Date getDateDebut() { return dateDebut; }
        public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

        public Date getDateFin() { return dateFin; }
        public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

        public String getLieu() { return lieu; }
        public void setLieu(String lieu) { this.lieu = lieu; }
    }

}

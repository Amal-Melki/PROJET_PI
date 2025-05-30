# 🎉 Evencia Event Planner – Projet PI

**Evencia Event Planner** est une application de gestion d’événements complète, conçue pour permettre aux utilisateurs de planifier, organiser et analyser leurs événements de manière efficace. Elle repose sur une architecture modulaire intégrant **six modules interconnectés**, assurant une gestion fluide de toutes les dimensions liées à l’organisation d’événements.

---

## 📁 Table des matières

* [📝 Description du projet](#-description-du-projet)
* [📦 Modules du projet](#-modules-du-projet)
* [🛠️ Prérequis](#️-prérequis)
* [⚙️ Installation](#️-installation)
* [📁 Structure du projet](#-structure-du-projet)
* [💻 Technologies utilisées](#-technologies-utilisées)
* [🌟 Logiques métiers](#-logique-métier)
* [🔌 API avancées](#-api-avancées)

---

## 📝 Description du projet

Evencia Event Planner centralise la gestion de tous les aspects d’un événement, de sa **planification** à son **analyse**. Grâce à son architecture modulaire, chaque fonctionnalité peut fonctionner de manière indépendante tout en collaborant avec les autres modules, garantissant ainsi une **expérience utilisateur fluide** et **efficace**.

---

## 📦 Modules du projet

L’application est composée des 6 modules suivants :

* **Gestion des événements**
  ➔ Création, modification, affichage et suppression d’événements
  ➔ Détails : date, heure, lieu, participants, description...

* **Gestion des utilisateurs**
  ➔ Création de comptes
  ➔ Attribution de rôles (administrateur, organisateur, participant)
  ➔ Gestion des profils

* **Gestion des espaces**
  ➔ Réservation de lieux : salles de conférence, auditoriums...
  ➔ Gestion de la disponibilité

* **Gestion de blog**
  ➔ Publication d’articles, actualités, mises à jour
  ➔ Communication avec les participants

* **Gestion des matériels**
  ➔ Suivi des équipements, décorations, mobilier
  ➔ Réservation et état du stock

* **Gestion des produits dérivés**
  ➔ Vente de t-shirts, mugs, etc.
  ➔ Suivi des commandes et gestion du stock

---

## 🛠️ Prérequis

Assurez-vous d’avoir les outils suivants installés sur votre machine :

* Java JDK 17 ou plus
* Apache Maven
* XAMPP (MySQL)
* IntelliJ IDEA
* Scene Builder

---

## ⚙️ Installation

1. **Cloner le dépôt :**

   ```bash
   git clone https://github.com/Amal-Melki/PROJET_PI.git
   ```

2. **Configurer la base de données :**

   * Ouvrir XAMPP et démarrer MySQL
   * Créer une base de données nommée `evencia_db`
   * Importer le fichier `evencia.sql` fourni dans MySQL

3. **Modifier les fichiers de configuration :**

   * `database.properties` ou `application.properties` :

   ```properties
   db.url=jdbc:mysql://localhost:3306/evencia_db
   db.username=root
   db.password=
   ```

4. **Lancer l’application :**

   * Ouvrir le projet dans IntelliJ IDEA
   * Exécuter la classe `Main.java` (point d’entrée)

---

## 📁 Structure du projet

```bash
├── controllers/          # Contrôleurs JavaFX
├── models/               # Entités (utilisateur, événement, matériel...)
├── services/             # Logique métier
├── utils/                # Classes utilitaires
├── resources/            # FXML, images, fichiers SQL
└── Main.java             # Point d’entrée de l’application
```

## 💻 Technologies utilisées

* **Java 17** – Développement de l’application
* **JavaFX** – Interface utilisateur
* **Scene Builder** – Conception visuelle de l’UI
* **Maven** – Gestion de projet et dépendances
* **MySQL** – Base de données relationnelle
* **XAMPP** – Serveur local pour MySQL
* **IntelliJ IDEA** – Environnement de développement

---

## 🌟 Logique métier

La logique métier regroupe les fonctionnalités clés gérées par l’application, notamment :

* Gestion des événements : création, modification, suppression
* Gestion des utilisateurs et attribution des rôles
* Réservation et gestion des espaces
* Gestion des matériels et suivi des stocks
* Vente et gestion des produits dérivés
* Analyse des données et génération de rapports
* Sécurité des données et gestion des accès
* Séparation entre interface utilisateur et logique métier pour une architecture claire

---

## 🔌 API avancées

Evencia Event Planner intègre ou prévoit d’intégrer les API et outils suivants :

* **JavaFX Charts API** : création de graphiques dynamiques pour l’analyse
* **Java Time API (`java.time`)** : gestion des dates, durées, intervalles
* **ObservableLists (JavaFX)** : pour des listes interactives en temps réel
* **FXML + CSS** : personnalisation de l’interface / séparation logique métier
* **PreparedStatements (JDBC)** : sécurité contre les injections SQL
* **Prévision statistique** : API personnalisée ou externe pour l’analyse des tendances
* **API REST ** : synchronisation multi-utilisateur en temps réel
* **JavaMail ou API mail** : pour notifications par e-mail
* **API Google Calendar** : pour intégration de calendrier
* **API de géolocalisation** : affichage des emplacements via carte interactive

---

## 📌 Remarques

Ce projet a été développé dans le cadre du **Projet Intégré (PI)** à l’**ESPRIT**, par une équipe de 6 personnes. Il illustre des compétences en :

* travail collaboratif
* conception modulaire
* gestion d’interface utilisateur
* analyse de données et tableaux de bord

---

## 🔗 Liens utiles

* Dépôt GitHub : [github.com/Amal-Melki/PROJET\_PI](https://github.com/Amal-Melki/PROJET_PI)

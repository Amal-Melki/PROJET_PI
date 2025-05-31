
# 🎉 Evencia Event Planner – Projet PI

Evencia Event Planner est une application de gestion d’événements complète, conçue pour permettre aux utilisateurs de planifier, organiser et analyser leurs événements efficacement. Elle repose sur une architecture modulaire avec six modules interconnectés :

- **Gestion des événements** : Création, modification, suppression d’événements
- **Gestion des utilisateurs** : Comptes, rôles, profils
- **Gestion des espaces** : Réservation de lieux
- **Gestion de blog** : Articles, actualités
- **Gestion des matériels** : Suivi des équipements et des stocks
- **Gestion des produits dérivés** : Vente de produits liés à l’événement

## 📦 Prérequis

- Java 17+
- Maven
- XAMPP (MySQL)
- IntelliJ IDEA
- Scene Builder

## ⚙️ Installation

1. Cloner le dépôt :
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
=======

# Evencia Event Planner - Projet PI

Evencia Event Planner est une application de gestion d'événements qui permet aux utilisateurs de planifier, gérer et suivre des événements. L'application est composée de SIX modules interconnectés qui permettent une gestion complète et fluide des aspects liés à l'organisation d'événements.

## Table des matières
- [Description du projet](#description-du-projet)
- [Modules du projet](#modules-du-projet)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Structure du projet](#structure-du-projet)
- [Technologies utilisées](#technologies-utilisées)


## Description du projet
Evencia Event Planner permet de gérer tous les aspects d'un événement, y compris la planification, la gestion des utilisateurs, des espaces, du matériel et des produits dérivés. L'application est basée sur une architecture modulaire, ce qui permet à chaque fonctionnalité de fonctionner de manière autonome tout en communiquant avec les autres modules pour garantir une gestion complète.

## Modules du projet
Le projet est constitué de 6 modules principaux :
1. **Gestion des événements** : Permet de créer, modifier, afficher et supprimer des événements. Il inclut des fonctionnalités pour gérer les détails des événements, comme la date, l'heure, le lieu, les participants, etc.
2. **Gestion des utilisateurs** : Permet de gérer les utilisateurs de l'application (administrateurs, organisateurs, participants). Il permet de créer des comptes, gérer les profils, et attribuer des rôles aux utilisateurs.
3. **Gestion des espaces** : Permet de gérer les différents espaces où les événements peuvent avoir lieu, comme les salles de conférence, les auditoriums, ou d'autres lieux. Il permet également de réserver et de gérer la disponibilité des espaces.
4. **Gestion de blog** : Permet aux utilisateurs de publier des articles, des actualités et des mises à jour concernant les événements ou l'entreprise. Cela permet d'informer les participants et d'encourager l'engagement.
5. **Gestion des matériels** : Permet de gérer tout le matériel nécessaire pour les événements, comme les équipements audiovisuels, les décorations, les meubles, etc. Il permet de suivre les stocks, les réservations et l'utilisation du matériel.
6. **Gestion des produits dérivés** : Permet de gérer les produits dérivés liés aux événements (t-shirts, mugs, etc.), de suivre les commandes et les stocks, et de permettre aux utilisateurs d'acheter des produits en ligne.

## Prérequis
Avant de pouvoir exécuter le projet sur votre machine, assurez-vous d'avoir les prérequis suivants :
- **Java** version 17 ou supérieure
- **Maven** pour la gestion des dépendances
- **MySQL** avec XAMPP (pour la gestion de la base de données)
- **IntelliJ IDEA** pour le développement et l'exécution du code
- **Scene Builder** pour l'interface graphique JavaFX

## Installation
1. **Cloner le dépôt** :
    git clone https://github.com/Amal-Melki/PROJET_PI.git
    
2. **Configurer la base de données MySQL** :
    - Ouvrez XAMPP et démarrez le serveur MySQL.
    - Créez une base de données dans MySQL.
    - Importez les tables nécessaires depuis le fichier SQL fourni.
3. **Configurer les fichiers de connexion** :
    - Modifiez les fichiers de configuration ( `application.properties` ou `database.properties`) pour y ajouter vos informations de connexion MySQL (adresse, utilisateur, mot de passe).

4. **Lancer l'application** :
    - Ouvrez le projet dans IntelliJ IDEA et exécutez la classe principale.

## Structure du projet
Le projet est organisé de la manière suivante :

- **controllers** : Contient les classes qui gèrent les interactions entre l'interface utilisateur et la logique de l'application.
- **models** : Contient les classes représentant les entités de l'application (utilisateur, événement, matériel, espace, produit derviée, blog).
- **services** : Contient la logique métier de l'application (gestion des utilisateurs, des événements, des matériels, espaces, des blogs et medias, des réservations).
- **utils** : Contient les classes utilitaires (pour la gestion des erreurs, la connexion à la base de données, etc.).

## Technologies utilisées
- **Java 17** pour le développement de l'application.
- **JavaFX** pour l'interface utilisateur.
- **Maven** pour la gestion des dépendances et la construction du projet.
- **MySQL** pour la gestion de la base de données.
- **XAMPP** pour exécuter MySQL en local.
- **Scene Builder** pour la création de l'interface graphique.

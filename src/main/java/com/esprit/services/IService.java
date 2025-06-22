package com.esprit.services;

import java.util.List;

public interface IService<T> {
    // Gardez votre méthode existante
    List<T> rechercher();

    // Ajoutez la nouvelle méthode avec implémentation par défaut
    default List<T> recuperer() {
        return this.rechercher(); // Réutilise l'ancienne méthode
    }

    // Gardez vos autres méthodes CRUD
    void ajouter(T t);
    void modifier(T t);
    void supprimer(T t);
}
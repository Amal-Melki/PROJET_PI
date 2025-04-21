package com.esprit.services.produits;

import java.util.List;

public interface IService<T> {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(T t);
    List<T> recuperer();
}

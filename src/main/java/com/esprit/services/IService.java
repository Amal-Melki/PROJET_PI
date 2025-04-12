package com.esprit.services;

import com.esprit.modules.Materiels;

import java.util.List;

public interface IService<T> {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(T t);
    List<T> recuperer();
}


package com.esprit.services;

import com.esprit.modules.utilisateurs.User;

public interface UserDAO {
    void create(User user);
    User read(int id);
    void update(User user);
    void delete(int id);
}
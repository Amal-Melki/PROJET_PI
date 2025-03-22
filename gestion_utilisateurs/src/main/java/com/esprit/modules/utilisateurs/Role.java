package com.esprit.modules.utilisateurs;

public class Role {
    private int id;
    private String name;

    // Constructeur
    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Role{id=" + id + ", name='" + name + "'}";
    }
}
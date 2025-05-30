package com.esprit.modules;

public enum CategorieEnum {
    CONCERT("concert"),
    FESTIVAL("festival"),
    CONFERENCE("conference"),
    EXPOSITION("exposition"),
    ATELIER("atelier"),
    SEMINAIRE("séminaire"),
    EVENEMENT_SPORTIF("événement sportif"),
    GALA("gala");

    private final String displayName;

    CategorieEnum(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static CategorieEnum fromString(String text) {
        for (CategorieEnum categorie : CategorieEnum.values()) {
            if (categorie.toString().equalsIgnoreCase(text)) {
                return categorie;
            }
        }
        return null;
    }
}

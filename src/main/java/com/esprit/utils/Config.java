package com.esprit.utils;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Fichier config.properties introuvable !");
            }
            props.load(input);
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement config.properties : " + e.getMessage());
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}

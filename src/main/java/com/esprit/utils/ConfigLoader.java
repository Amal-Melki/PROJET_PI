package com.esprit.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Fichier config.properties non trouvé dans resources");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour accéder aux valeurs par clé
    public static String get(String key) {
        return properties.getProperty(key);
    }


}

package com.esprit.controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MesReservationsControllerTest {

    @Test
    void testNormalizeStatus() {
        MesReservationsController controller = new MesReservationsController();
        
        // Test des variantes pour "Confirmée"
        assertEquals("Confirmée", controller.normalizeStatus("confirmed"));
        assertEquals("Confirmée", controller.normalizeStatus("validée"));
        assertEquals("Confirmée", controller.normalizeStatus("Confirmée"));
        
        // Test des variantes pour "En cours"
        assertEquals("En cours", controller.normalizeStatus("pending"));
        assertEquals("En cours", controller.normalizeStatus("en_cours"));
        assertEquals("En cours", controller.normalizeStatus("En cours"));
        
        // Test des variantes pour "Terminée"
        assertEquals("Terminée", controller.normalizeStatus("completed"));
        assertEquals("Terminée", controller.normalizeStatus("done"));
        assertEquals("Terminée", controller.normalizeStatus("Terminée"));
        
        // Test des valeurs inconnues
        assertEquals("Autre", controller.normalizeStatus("Autre"));
    }
}

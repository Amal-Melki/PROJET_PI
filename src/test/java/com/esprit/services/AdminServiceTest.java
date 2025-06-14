package com.esprit.services;

import com.esprit.exceptions.ServiceException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminServiceTest {
    private static final String TEST_EMAIL = "test-admin@esprit.com";
    private static final AdminService service = new AdminService();
    private static Admin testAdmin;

    @Test
    @Order(1)
    void testAjouterAdmin() {
        testAdmin = new Admin(TEST_EMAIL, "Test", "User", "password", "ADMIN");
        assertDoesNotThrow(() -> service.ajouter(testAdmin));
    }

    @Test
    @Order(2)
    void testAjouterAdminDuplicate() {
        Admin duplicate = new Admin(TEST_EMAIL, "Test", "Duplicate", "pass", "ADMIN");
        assertThrows(ServiceException.class, () -> service.ajouter(duplicate));
    }

    @Test
    @Order(3)
    void testFindByEmail() {
        assertNotNull(service.findByEmail(TEST_EMAIL));
    }

    @Test
    @Order(4)
    void testModifierAdmin() {
        testAdmin.setRole("SUPER_ADMIN");
        assertDoesNotThrow(() -> service.modifier(testAdmin));
        assertEquals("SUPER_ADMIN", service.findByEmail(TEST_EMAIL).getRole());
    }

    @Test
    @Order(5)
    void testSupprimerAdmin() {
        assertDoesNotThrow(() -> service.supprimer(testAdmin));
        assertNull(service.findByEmail(TEST_EMAIL));
    }
}

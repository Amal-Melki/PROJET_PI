package com.esprit.tests;

import com.esprit.services.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @Test
    public void testUserCreation() {
        UserService service = new UserService();
        assertDoesNotThrow(() -> service.ajouter(new User("Test", "User", "test@user.com", "password")));
    }
}

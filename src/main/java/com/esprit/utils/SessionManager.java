package com.esprit.utils;

import com.esprit.models.Espace;
import com.esprit.models.User;

/**
 * Utility class to manage the current user session
 */
public class SessionManager {
    private static User currentUser;
    private static Espace selectedEspace;
    
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    public static User getCurrentUser() {
        if (currentUser == null) {
            // Temporary test user with required fields
            currentUser = new User(1, "Test", "User", "test@example.com", "password", "user");
        }
        return currentUser;
    }
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static void clearSession() {
        currentUser = null;
        selectedEspace = null;
    }
    
    public static Espace getSelectedEspace() {
        return selectedEspace;
    }
    
    public static void setSelectedEspace(Espace espace) {
        selectedEspace = espace;
    }
}

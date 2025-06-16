package com.esprit.controllers;

import com.esprit.models.Admin;

/**
 * Interface for controllers that need access to the current admin user.
 * Controllers implementing this interface will receive the current admin user
 * when their view is loaded in the main layout.
 */
public interface AdminAware {
    /**
     * Sets the current admin user for the controller.
     * @param admin The current admin user, or null if no admin is logged in
     */
    void setAdmin(Admin admin);
} 
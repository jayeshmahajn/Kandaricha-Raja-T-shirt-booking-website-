package com.mandal.tshirt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Very lightweight admin check. The frontend admin page collects a
 * password and sends it as the "X-Admin-Key" header on every request
 * that uploads/deletes images or views the bookings list. No sessions,
 * no database - just a shared secret, which is enough for a small
 * mandal committee site with one or two admins.
 */
@Component
public class AdminAuth {

    @Value("${app.admin.key}")
    private String adminKey;

    public boolean isValid(String providedKey) {
        return adminKey != null && adminKey.equals(providedKey);
    }
}

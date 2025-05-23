package com.finance.extracted_logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AuthenticationHelper.
 */
public class AuthenticationHelperTest {

    @Test
    void testHashPassword_NonNullInput() {
        String password = "password123";
        String hashedPassword = AuthenticationHelper.hashPassword(password);
        assertNotNull(hashedPassword, "Hashed password should not be null for non-null input.");
        // SHA-256 hash of "password123" is ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
        assertEquals("ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f", hashedPassword, "Hashed password does not match expected SHA-256 hash.");
    }

    @Test
    void testHashPassword_EmptyInput() {
        String password = "";
        String hashedPassword = AuthenticationHelper.hashPassword(password);
        assertNotNull(hashedPassword, "Hashed password should not be null for empty input.");
        // SHA-256 hash of "" is e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", hashedPassword, "Hashed password for empty string does not match expected SHA-256 hash.");
    }

    @Test
    void testHashPassword_NullInput() {
        String hashedPassword = AuthenticationHelper.hashPassword(null);
        assertNull(hashedPassword, "Hashed password should be null for null input.");
    }

    @Test
    void testHashPassword_Consistency() {
        String password = "testPassword";
        String hashedPassword1 = AuthenticationHelper.hashPassword(password);
        String hashedPassword2 = AuthenticationHelper.hashPassword(password);
        assertEquals(hashedPassword1, hashedPassword2, "Hashing the same password multiple times should produce the same result.");
    }

    @Test
    void testHashPassword_DifferentInputs() {
        String passwordA = "PasswordA";
        String passwordB = "PasswordB";
        String hashedPasswordA = AuthenticationHelper.hashPassword(passwordA);
        String hashedPasswordB = AuthenticationHelper.hashPassword(passwordB);
        assertNotNull(hashedPasswordA);
        assertNotNull(hashedPasswordB);
        assertNotEquals(hashedPasswordA, hashedPasswordB, "Hashing different passwords should produce different results.");
    }
}
package com.finance.extracted_logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityProcessorTest {
    private SecurityProcessor securityProcessor;
    
    @BeforeEach
    void setUp() {
        securityProcessor = new SecurityProcessor();
    }
    
    @Test
    void testHashPassword_ValidPassword() {
        String password = "Test@1234";
        String hashed = securityProcessor.hashPassword(password);
        
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
    }
    
    @Test
    void testHashPassword_EmptyPassword() {
        String hashed = securityProcessor.hashPassword("");
        assertNotNull(hashed);
    }
    
    @Test
    void testHashPassword_NullPassword() {
        assertThrows(RuntimeException.class, () -> {
            securityProcessor.hashPassword(null);
        });
    }
    
    @Test
    void testVerifyPassword_CorrectPassword() {
        String password = "Test@1234";
        String hashed = securityProcessor.hashPassword(password);
        assertTrue(securityProcessor.verifyPassword(password, hashed));
    }
    
    @Test
    void testVerifyPassword_WrongPassword() {
        String hashed = securityProcessor.hashPassword("Test@1234");
        assertFalse(securityProcessor.verifyPassword("WrongPassword", hashed));
    }
    
    @Test
    void testVerifyPassword_EmptyInput() {
        String hashed = securityProcessor.hashPassword("Test@1234");
        assertFalse(securityProcessor.verifyPassword("", hashed));
    }
    
    @Test
    void testVerifyPassword_NullInput() {
        String hashed = securityProcessor.hashPassword("Test@1234");
        assertFalse(securityProcessor.verifyPassword(null, hashed));
    }
}
package com.finance.extracted_logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class UserInfoValidatorTest {
    
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_DIR = "UserInfo";
    
    @BeforeEach
    void setUp() throws IOException {
        // 确保测试目录存在
        Files.createDirectories(Path.of(TEST_DIR));
        // 创建测试用户文件
        Files.writeString(Path.of(TEST_DIR, TEST_USERNAME + ".txt"), "Username: " + TEST_USERNAME);
    }
    
    @Test
    void validateUsername_EmptyUsername_ReturnsFalse() {
        assertFalse(UserInfoValidator.validateUsername(""));
    }
    
    @Test
    void validateUsername_TooLongUsername_ReturnsFalse() {
        String longUsername = "a".repeat(21);
        assertFalse(UserInfoValidator.validateUsername(longUsername));
    }
    
    @Test
    void validateUsername_InvalidCharacters_ReturnsFalse() {
        assertFalse(UserInfoValidator.validateUsername("test@user"));
    }
    
    @Test
    void validateUsername_ExistingUsername_ReturnsFalse() {
        assertFalse(UserInfoValidator.validateUsername(TEST_USERNAME));
    }
    
    @Test
    void validateUsername_ValidUsername_ReturnsTrue() {
        assertTrue(UserInfoValidator.validateUsername("newuser"));
    }
    
    @Test
    void validatePassword_EmptyPassword_ReturnsFalse() {
        assertFalse(UserInfoValidator.validatePassword("", ""));
    }
    
    @Test
    void validatePassword_MismatchPasswords_ReturnsFalse() {
        assertFalse(UserInfoValidator.validatePassword("password1", "password2"));
    }
    
    @Test
    void validatePassword_SingleCharacterType_ReturnsFalse() {
        assertFalse(UserInfoValidator.validatePassword("password", "password"));
    }
    
    @Test
    void validatePassword_TwoCharacterTypes_ReturnsTrue() {
        assertTrue(UserInfoValidator.validatePassword("Password1", "Password1"));
    }
    
    @Test
    void validateUserInfo_EmptyFields_ReturnsFalse() {
        assertFalse(UserInfoValidator.validateUserInfo("", "", ""));
    }
}
package com.finance.extracted_logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class CredentialProcessorTest {
    private static final String TEST_DIR = "UserInfo";
    private static final String TEST_FILE = "saved_credentials_test.json";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "Test@1234";
    
    private CredentialProcessor processor;
    
    @BeforeEach
    void setUp() throws IOException {
        // 确保测试目录存在
        Files.createDirectories(Path.of(TEST_DIR));
        // 初始化处理器
        processor = new CredentialProcessor(TEST_DIR + "/" + TEST_FILE, "TestSecretKey");
        // 清理测试文件
        if (Files.exists(Path.of(TEST_DIR, TEST_FILE))) {
            Files.delete(Path.of(TEST_DIR, TEST_FILE));
        }
    }
    
    @Test
    void saveCredentials_NewUser_SavesSuccessfully() {
        processor.saveCredentials(TEST_USERNAME, TEST_PASSWORD);
        List<String> usernames = processor.getSavedUsernames();
        assertTrue(usernames.contains(TEST_USERNAME));
    }
    
    @Test
    void getSavedPassword_ExistingUser_ReturnsCorrectPassword() {
        processor.saveCredentials(TEST_USERNAME, TEST_PASSWORD);
        String retrieved = processor.getSavedPassword(TEST_USERNAME);
        assertEquals(TEST_PASSWORD, retrieved);
    }
    
    @Test
    void removeCredentials_ExistingUser_RemovesSuccessfully() {
        processor.saveCredentials(TEST_USERNAME, TEST_PASSWORD);
        processor.removeCredentials(TEST_USERNAME);
        assertNull(processor.getSavedPassword(TEST_USERNAME));
    }
    
    @Test
    void encryptDecryptPassword_ReturnsOriginalValue() {
        String encrypted = processor.encryptPassword(TEST_PASSWORD);
        String decrypted = processor.decryptPassword(encrypted);
        assertEquals(TEST_PASSWORD, decrypted);
    }
}
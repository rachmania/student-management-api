package com.javamastery.module10.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    // Never hard-code this — load from an environment variable or a secrets manager.
    private static final String KEY_ENV_VAR = "STUDENT_API_FIELD_KEY";

    @Override
    public String convertToDatabaseColumn(String plainText) {
        if (plainText == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keyFromEnv());
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String cipherText) {
        if (cipherText == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keyFromEnv());
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    private SecretKeySpec keyFromEnv() {
        String key = System.getenv(KEY_ENV_VAR);
        return new SecretKeySpec(key.getBytes(), "AES");
    }
}
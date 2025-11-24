package com.rideshare;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PasswordValidatorTest {
    PasswordValidator passwordValidator=new PasswordValidator();

    @Test
    void testValidPassword() throws InfyAcademyException{
        String password="temp1234";
        boolean result=passwordValidator.validatePassword(password);
        Assertions.assertTrue(result);
    }
}

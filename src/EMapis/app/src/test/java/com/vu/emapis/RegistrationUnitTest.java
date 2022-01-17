package com.vu.emapis;
import static org.junit.Assert.assertEquals;


import org.junit.Test;


public class RegistrationUnitTest{

    @Test
    public void RegistrationTest() {
        String username = "@Deivis";

        boolean result = RegisterActivity.checkUsername(username);

        assertEquals(false, result);

    }
}
package com.example.hw04_gymlog_v300;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.example.hw04_gymlog_v300.database.entities.GymLog;

public class GymLogUnitTest {

    private LoginValidator loginValidator;

    @Before
    public void setUp() {
        loginValidator = new LoginValidator();
    }


    @Test
    public void emptyUsername_returnsFalse() {
        assertFalse(loginValidator.isValidUsername(""));
    }

    @Test
    public void validUsername_returnsTrue() {
        assertTrue(loginValidator.isValidUsername("admin1"));
    }

    @Test
    public void correctPassword_returnsTrue() {
        assertTrue(loginValidator.isValidPassword("admin1", "admin1"));
    }

    @Test
    public void incorrectPassword_returnsFalse() {
        assertFalse(loginValidator.isValidPassword("wrong", "admin1"));
    }

    @Test
    public void canLogin_success() {
        assertTrue(loginValidator.canLogin("admin1", "admin1", "admin1"));
    }

    @Test
    public void canLogin_fail() {
        assertFalse(loginValidator.canLogin("admin1", "wrong", "admin1"));
    }

    @Test
    public void gymLog_constructor_setsValuesCorrectly() {
        int userId = 1;

        GymLog log = new GymLog("Deadlift", 405.0, 3, userId);

        assertEquals("Deadlift", log.getExercise());
        assertEquals(405.0, log.getWeight(), 0.0);
        assertEquals(3, log.getReps());
        assertEquals(userId, log.getUserID());
    }
}
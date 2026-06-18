package br.unibh.gestar.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrgencyLevelTest {

    @Test
    void shouldHaveCorrectRedProperties() {
        UrgencyLevel red = UrgencyLevel.RED;

        assertEquals("Emergency", red.getDescription());
        assertEquals(1, red.getPriority());
        assertEquals(0, red.getTargetTimeMinutes());
    }

    @Test
    void shouldHaveCorrectOrangeProperties() {
        UrgencyLevel orange = UrgencyLevel.ORANGE;

        assertEquals("Very urgent", orange.getDescription());
        assertEquals(2, orange.getPriority());
        assertEquals(10, orange.getTargetTimeMinutes());
    }

    @Test
    void shouldHaveCorrectYellowProperties() {
        UrgencyLevel yellow = UrgencyLevel.YELLOW;

        assertEquals("Urgent", yellow.getDescription());
        assertEquals(3, yellow.getPriority());
        assertEquals(60, yellow.getTargetTimeMinutes());
    }

    @Test
    void shouldHaveCorrectGreenProperties() {
        UrgencyLevel green = UrgencyLevel.GREEN;

        assertEquals("Low urgency", green.getDescription());
        assertEquals(4, green.getPriority());
        assertEquals(120, green.getTargetTimeMinutes());
    }

    @Test
    void shouldComparePrioritiesByNumber() {
        assertTrue(UrgencyLevel.RED.getPriority() < UrgencyLevel.ORANGE.getPriority());
        assertTrue(UrgencyLevel.ORANGE.getPriority() < UrgencyLevel.YELLOW.getPriority());
        assertTrue(UrgencyLevel.YELLOW.getPriority() < UrgencyLevel.GREEN.getPriority());
    }

    @Test
    void shouldHaveMoreUrgentTargetTime() {
        assertTrue(UrgencyLevel.RED.getTargetTimeMinutes() < UrgencyLevel.ORANGE.getTargetTimeMinutes());
        assertTrue(UrgencyLevel.ORANGE.getTargetTimeMinutes() < UrgencyLevel.YELLOW.getTargetTimeMinutes());
        assertTrue(UrgencyLevel.YELLOW.getTargetTimeMinutes() < UrgencyLevel.GREEN.getTargetTimeMinutes());
    }
}

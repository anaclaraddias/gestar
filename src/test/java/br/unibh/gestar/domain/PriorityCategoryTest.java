package br.unibh.gestar.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PriorityCategoryTest {
    @Test
    void shouldHaveCorrectWeights() {
        assertEquals(3, PriorityCategory.HIGHEST_PRIORITY.getWeight());
        assertEquals(2, PriorityCategory.PREFERRED.getWeight());
        assertEquals(1, PriorityCategory.NORMAL.getWeight());
    }

    @Test
    void shouldCategorizeByAgeAbove80() {
        PriorityCategory category = PriorityCategory.forAge(85);
        assertEquals(PriorityCategory.HIGHEST_PRIORITY, category);
    }

    @Test
    void shouldCategorizeByAgeAbove60AndBelow80() {
        PriorityCategory category = PriorityCategory.forAge(65);
        assertEquals(PriorityCategory.PREFERRED, category);
    }

    @Test
    void shouldCategorizeByAgeBelow60() {
        PriorityCategory category = PriorityCategory.forAge(35);
        assertEquals(PriorityCategory.NORMAL, category);
    }

    @Test
    void shouldCategorizeAgeExactly80() {
        PriorityCategory category = PriorityCategory.forAge(80);
        assertEquals(PriorityCategory.HIGHEST_PRIORITY, category);
    }

    @Test
    void shouldCategorizeAgeExactly60() {
        PriorityCategory category = PriorityCategory.forAge(60);
        assertEquals(PriorityCategory.PREFERRED, category);
    }

    @Test
    void shouldHaveHigherWeightForHigherPriority() {
        assertTrue(PriorityCategory.HIGHEST_PRIORITY.getWeight() > PriorityCategory.PREFERRED.getWeight());
        assertTrue(PriorityCategory.PREFERRED.getWeight() > PriorityCategory.NORMAL.getWeight());
    }

    @Test
    void shouldHandleVeryOldAge() {
        PriorityCategory category = PriorityCategory.forAge(150);
        assertEquals(PriorityCategory.HIGHEST_PRIORITY, category);
    }

    @Test
    void shouldHandleNewborn() {
        PriorityCategory category = PriorityCategory.forAge(0);
        assertEquals(PriorityCategory.NORMAL, category);
    }
}

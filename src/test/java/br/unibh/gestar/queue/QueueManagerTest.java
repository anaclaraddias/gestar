package br.unibh.gestar.queue;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.UrgencyLevel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class QueueManagerTest {
    private QueueManager queue;
    private Patient patient;

    @BeforeEach
    void setUp() {
        queue = new QueueManager();
        patient = new Patient("Test Patient", LocalDate.of(1990, 5, 15));
    }

    @Test
    void shouldThrowExceptionWhenAddingUnclassifiedMedicalCare() {
        MedicalCare care = new MedicalCare(patient, "Test", PriorityCategory.NORMAL);

        assertThrows(IllegalStateException.class, () -> queue.add(care));
    }

    @Test
    void shouldAddClassifiedMedicalCareToQueue() {
        MedicalCare care = new MedicalCare(patient, "Test", PriorityCategory.NORMAL);
        care.setClassification(UrgencyLevel.GREEN);

        queue.add(care);
        assertEquals(1, queue.size());
    }

    @Test
    void shouldReturnCorrectQueueSize() {
        MedicalCare care1 = new MedicalCare(patient, "Test 1", PriorityCategory.NORMAL);
        care1.setClassification(UrgencyLevel.GREEN);
        MedicalCare care2 = new MedicalCare(patient, "Test 2", PriorityCategory.NORMAL);
        care2.setClassification(UrgencyLevel.YELLOW);

        queue.add(care1);
        queue.add(care2);

        assertEquals(2, queue.size());
    }

    @Test
    void shouldIdentifyEmptyQueue() {
        assertTrue(queue.isEmpty());
    }

    @Test
    void shouldIdentifyNonEmptyQueue() {
        MedicalCare care = new MedicalCare(patient, "Test", PriorityCategory.NORMAL);
        care.setClassification(UrgencyLevel.GREEN);
        queue.add(care);

        assertFalse(queue.isEmpty());
    }

    @Test
    void shouldReturnRedPatientsFirst() {
        MedicalCare red = new MedicalCare(patient, "Red case", PriorityCategory.NORMAL);
        red.setClassification(UrgencyLevel.RED);

        MedicalCare green = new MedicalCare(patient, "Green case", PriorityCategory.NORMAL);
        green.setClassification(UrgencyLevel.GREEN);

        queue.add(green);
        queue.add(red);

        MedicalCare next = queue.next();
        assertEquals(UrgencyLevel.RED, next.getUrgencyLevel());
    }

    @Test
    void shouldPeekWithoutRemoving() {
        MedicalCare care = new MedicalCare(patient, "Test", PriorityCategory.NORMAL);
        care.setClassification(UrgencyLevel.GREEN);
        queue.add(care);

        MedicalCare peeked = queue.peek();
        assertEquals(1, queue.size());
        assertNotNull(peeked);
    }

    @Test
    void shouldRemoveOnNext() {
        MedicalCare care = new MedicalCare(patient, "Test", PriorityCategory.NORMAL);
        care.setClassification(UrgencyLevel.GREEN);
        queue.add(care);

        queue.next();
        assertEquals(0, queue.size());
    }

    @Test
    void shouldReturnNullForEmptyQueueNext() {
        assertNull(queue.next());
    }

    @Test
    void shouldReturnNullForEmptyQueuePeek() {
        assertNull(queue.peek());
    }

    @Test
    void shouldCountSizeByUrgencyLevel() {
        MedicalCare red = new MedicalCare(patient, "Red", PriorityCategory.NORMAL);
        red.setClassification(UrgencyLevel.RED);

        MedicalCare green = new MedicalCare(patient, "Green", PriorityCategory.NORMAL);
        green.setClassification(UrgencyLevel.GREEN);

        queue.add(red);
        queue.add(green);

        assertEquals(1, queue.size(UrgencyLevel.RED));
        assertEquals(1, queue.size(UrgencyLevel.GREEN));
        assertEquals(0, queue.size(UrgencyLevel.ORANGE));
    }

    @Test
    void shouldRespectPriorityOrderWithinUrgencyLevel() {
        MedicalCare highPriority = new MedicalCare(patient, "High priority", PriorityCategory.HIGH);
        highPriority.setClassification(UrgencyLevel.GREEN);

        MedicalCare lowPriority = new MedicalCare(patient, "Low priority", PriorityCategory.LOW);
        lowPriority.setClassification(UrgencyLevel.GREEN);

        queue.add(lowPriority);
        queue.add(highPriority);

        MedicalCare next = queue.next();
        assertEquals(PriorityCategory.HIGH, next.getPriorityCategory());
    }

    @Test
    void shouldHandleMultipleCases() {
        for (int i = 0; i < 5; i++) {
            MedicalCare care = new MedicalCare(patient, "Case " + i, PriorityCategory.NORMAL);
            care.setClassification(UrgencyLevel.GREEN);
            queue.add(care);
        }

        assertEquals(5, queue.size());

        for (int i = 0; i < 5; i++) {
            assertNotNull(queue.next());
        }

        assertEquals(0, queue.size());
    }

    @Test
    void shouldMaintainOrderAfterMultipleAdds() {
        MedicalCare yellow = new MedicalCare(patient, "Yellow", PriorityCategory.NORMAL);
        yellow.setClassification(UrgencyLevel.YELLOW);

        MedicalCare red = new MedicalCare(patient, "Red", PriorityCategory.NORMAL);
        red.setClassification(UrgencyLevel.RED);

        MedicalCare orange = new MedicalCare(patient, "Orange", PriorityCategory.NORMAL);
        orange.setClassification(UrgencyLevel.ORANGE);

        queue.add(yellow);
        queue.add(red);
        queue.add(orange);

        assertEquals(UrgencyLevel.RED, queue.next().getUrgencyLevel());
        assertEquals(UrgencyLevel.ORANGE, queue.next().getUrgencyLevel());
        assertEquals(UrgencyLevel.YELLOW, queue.next().getUrgencyLevel());
    }
}

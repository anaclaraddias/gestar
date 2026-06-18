package br.unibh.gestar.alert;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.PriorityCategory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClinicalNotifierTest {
    private ClinicalNotifier notifier;
    private MockAlertObserver observer1;
    private MockAlertObserver observer2;

    @BeforeEach
    void setUp() {
        notifier = new ClinicalNotifier();
        observer1 = new MockAlertObserver();
        observer2 = new MockAlertObserver();
    }

    @Test
    void shouldRegisterObserver() {
        notifier.register(observer1);

        notifier.triggerAlert(createMedicalCare());

        assertTrue(observer1.wasNotified());
    }

    @Test
    void shouldNotifyMultipleObservers() {
        notifier.register(observer1);
        notifier.register(observer2);

        MedicalCare care = createMedicalCare();
        notifier.triggerAlert(care);

        assertTrue(observer1.wasNotified());
        assertTrue(observer2.wasNotified());
    }

    @Test
    void shouldNotifyWithCorrectMedicalCare() {
        notifier.register(observer1);

        MedicalCare care = createMedicalCare();
        notifier.triggerAlert(care);

        assertNotNull(observer1.getNotifiedCare());
        assertEquals(care.getId(), observer1.getNotifiedCare().getId());
    }

    @Test
    void shouldNotifyEachObserverSeparately() {
        notifier.register(observer1);
        notifier.register(observer2);

        MedicalCare care = createMedicalCare();
        notifier.triggerAlert(care);

        assertEquals(1, observer1.getNotificationCount());
        assertEquals(1, observer2.getNotificationCount());
    }

    @Test
    void shouldHandleMultipleAlerts() {
        notifier.register(observer1);

        MedicalCare care1 = createMedicalCare();
        MedicalCare care2 = createMedicalCare();

        notifier.triggerAlert(care1);
        notifier.triggerAlert(care2);

        assertEquals(2, observer1.getNotificationCount());
    }

    private MedicalCare createMedicalCare() {
        Patient patient = new Patient("Test Patient", LocalDate.of(1990, 5, 15));
        return new MedicalCare(patient, "Critical condition", PriorityCategory.HIGH);
    }

    private static class MockAlertObserver implements AlertObserver {
        private int notificationCount = 0;
        private MedicalCare notifiedCare;

        @Override
        public void notify(MedicalCare medicalCare) {
            notificationCount++;
            notifiedCare = medicalCare;
        }

        boolean wasNotified() {
            return notificationCount > 0;
        }

        int getNotificationCount() {
            return notificationCount;
        }

        MedicalCare getNotifiedCare() {
            return notifiedCare;
        }
    }
}

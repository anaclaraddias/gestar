package br.unibh.gestar.queue;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.PriorityQueue;

public final class QueueUtils {
    public static PriorityQueue<MedicalCare> selectQueue(
        UrgencyLevel level,
        PriorityQueue<MedicalCare> redQueue,
        PriorityQueue<MedicalCare> orangeQueue,
        PriorityQueue<MedicalCare> yellowQueue,
        PriorityQueue<MedicalCare> greenQueue
    ) {
        return switch (level) {
            case RED -> redQueue;
            case ORANGE -> orangeQueue;
            case YELLOW -> yellowQueue;
            case GREEN -> greenQueue;
        };
    }

    public static UrgencyLevel levelToServe(
        LocalDateTime now,
        PriorityQueue<MedicalCare> redQueue,
        PriorityQueue<MedicalCare> orangeQueue,
        PriorityQueue<MedicalCare> yellowQueue,
        PriorityQueue<MedicalCare> greenQueue
    ) {
        if (!redQueue.isEmpty()) {
            return UrgencyLevel.RED;
        }

        UrgencyLevel[] others = {UrgencyLevel.ORANGE, UrgencyLevel.YELLOW, UrgencyLevel.GREEN};
        
        for (UrgencyLevel level : others) {
            MedicalCare head = selectQueue(level, redQueue, orangeQueue, yellowQueue, greenQueue).peek();
            
            if (head != null && Duration.between(head.getArrivalDateTime(), now).compareTo(Duration.ofMinutes(level.getTargetTimeMinutes())) > 0) {
                return level;
            }
        }

        for (UrgencyLevel level : others) {
            if (!selectQueue(level, redQueue, orangeQueue, yellowQueue, greenQueue).isEmpty()) {
                return level;
            }
        }

        return null;
    }

    public static long waitingMinutes(MedicalCare medicalCare, LocalDateTime now) {
        return Duration.between(medicalCare.getArrivalDateTime(), now).toMinutes();
    }
}

package br.unibh.gestar.queue;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.PriorityQueue;

/**
 * Utility that centralizes the choice between the four severity queues of
 * QueueManager. Two decisions live here:
 *  1) selectQueue: given the urgency level, returns the queue of that level
 *     (switch). Used to enqueue and to always operate on the right queue.
 *  2) levelToServe: decides which queue the next call should come from, combining
 *     the severity level with how long the head of each queue has been waiting,
 *     compared to the protocol target time (UrgencyLevel.targetTimeMinutes).
 */
public final class QueueUtils {

    private QueueUtils() {
    }

    /**
     * Returns the queue corresponding to the given urgency level.
     */
    public static PriorityQueue<MedicalCare> selectQueue(UrgencyLevel level,
                                                         PriorityQueue<MedicalCare> redQueue,
                                                         PriorityQueue<MedicalCare> orangeQueue,
                                                         PriorityQueue<MedicalCare> yellowQueue,
                                                         PriorityQueue<MedicalCare> greenQueue) {
        return switch (level) {
            case RED -> redQueue;
            case ORANGE -> orangeQueue;
            case YELLOW -> yellowQueue;
            case GREEN -> greenQueue;
        };
    }

    /**
     * Decides the level of the next call, in this order:
     *  1) Red always has absolute precedence (RN01).
     *  2) Among the other queues, whoever has already exceeded the target time
     *     of its own level comes first; if more than one is overdue, the most
     *     severe level breaks the tie. Prevents yellow and green from waiting
     *     indefinitely.
     *  3) With no overdue, severity order applies (orange, yellow, green).
     * Returns null if all queues are empty.
     */
    public static UrgencyLevel levelToServe(LocalDateTime now,
                                            PriorityQueue<MedicalCare> redQueue,
                                            PriorityQueue<MedicalCare> orangeQueue,
                                            PriorityQueue<MedicalCare> yellowQueue,
                                            PriorityQueue<MedicalCare> greenQueue) {
        if (!redQueue.isEmpty()) {
            return UrgencyLevel.RED;
        }
        UrgencyLevel[] others = {UrgencyLevel.ORANGE, UrgencyLevel.YELLOW, UrgencyLevel.GREEN};
        for (UrgencyLevel level : others) {
            MedicalCare head = selectQueue(level, redQueue, orangeQueue, yellowQueue, greenQueue).peek();
            if (head != null && Duration.between(head.getArrivalDateTime(), now)
                    .compareTo(Duration.ofMinutes(level.getTargetTimeMinutes())) > 0) {
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

    /**
     * Whole minutes the medical care has already waited in the queue.
     */
    public static long waitingMinutes(MedicalCare medicalCare, LocalDateTime now) {
        return Duration.between(medicalCare.getArrivalDateTime(), now).toMinutes();
    }
}

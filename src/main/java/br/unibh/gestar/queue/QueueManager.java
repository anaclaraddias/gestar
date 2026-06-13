package br.unibh.gestar.queue;

import java.util.Comparator;
import java.util.PriorityQueue;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Prioritized queue of medical care. The order follows three criteria, in this sequence:
 *  1) urgency color, from most urgent to least (RN01: Red first);
 *  2) priority category of the token (RN03: 80+ elderly before disabled/60+ elderly,
 *     which comes before normal);
 *  3) order of arrival, oldest first.
 * It is the heart of the system and the main target of tests.
 */
public class QueueManager {

    private static final Comparator<MedicalCare> PRIORITY_ORDER =
            Comparator
                    .comparingInt((MedicalCare a) -> a.getUrgencyLevel().getPriority())
                    .thenComparing(Comparator.comparingInt(
                            (MedicalCare a) -> a.getPriorityCategory().getWeight()).reversed())
                    .thenComparing(MedicalCare::getArrivalDateTime);

    private final PriorityQueue<MedicalCare> queue = new PriorityQueue<>(PRIORITY_ORDER);

    /**
     * Adds a classified medical care to the queue.
     */
    public void add(MedicalCare medicalCare) {
        if (medicalCare.getUrgencyLevel() == null) {
            throw new IllegalStateException("Medical care without classification cannot enter the queue.");
        }
        queue.add(medicalCare);
    }

    /**
     * Removes and returns the next medical care (the most prioritary), or null if empty.
     */
    public MedicalCare next() {
        return queue.poll();
    }

    /**
     * Returns the next without removing, or null if the queue is empty.
     */
    public MedicalCare peek() {
        return queue.peek();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

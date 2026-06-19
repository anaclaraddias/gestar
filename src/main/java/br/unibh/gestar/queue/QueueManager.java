package br.unibh.gestar.queue;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;

public class QueueManager {
    private static final Comparator<MedicalCare> ORDER_WITHIN_QUEUE =
        Comparator
        .comparingInt((MedicalCare a) -> a.getPriorityCategory().getWeight())
        .reversed()
        .thenComparing(MedicalCare::getArrivalDateTime);

    private final PriorityQueue<MedicalCare> redQueue = new PriorityQueue<>(ORDER_WITHIN_QUEUE);
    private final PriorityQueue<MedicalCare> orangeQueue = new PriorityQueue<>(ORDER_WITHIN_QUEUE);
    private final PriorityQueue<MedicalCare> yellowQueue = new PriorityQueue<>(ORDER_WITHIN_QUEUE);
    private final PriorityQueue<MedicalCare> greenQueue = new PriorityQueue<>(ORDER_WITHIN_QUEUE);

    public void add(MedicalCare medicalCare) {
        if (medicalCare.getUrgencyLevel() == null) {
            throw new IllegalStateException("Medical care without classification cannot enter the queue.");
        }

        queueOf(medicalCare.getUrgencyLevel()).add(medicalCare);
    }

    public MedicalCare next() {
        UrgencyLevel level = levelToServe();

        return level == null ? null : queueOf(level).poll();
    }

    public MedicalCare peek() {
        UrgencyLevel level = levelToServe();

        return level == null ? null : queueOf(level).peek();
    }

    public int size() {
        return redQueue.size() + orangeQueue.size() + yellowQueue.size() + greenQueue.size();
    }

    public int size(UrgencyLevel level) {
        return queueOf(level).size();
    }

    public boolean isEmpty() {
        return redQueue.isEmpty() && orangeQueue.isEmpty()
        && yellowQueue.isEmpty() && greenQueue.isEmpty();
    }

    private UrgencyLevel levelToServe() {
        return QueueUtils.levelToServe(
            LocalDateTime.now(),
            redQueue, 
            orangeQueue, 
            yellowQueue, 
            greenQueue
        );
    }

    private PriorityQueue<MedicalCare> queueOf(UrgencyLevel level) {
        return QueueUtils.selectQueue(
            level,
            redQueue, 
            orangeQueue, 
            yellowQueue, 
            greenQueue
        );
    }
}

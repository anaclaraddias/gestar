package br.unibh.gestar.domain;

public enum UrgencyLevel {
    RED("Emergency", 1, 0),
    ORANGE("Very urgent", 2, 10),
    YELLOW("Urgent", 3, 60),
    GREEN("Low urgency", 4, 120);

    private final String description;
    private final int priority;
    private final int targetTimeMinutes;

    UrgencyLevel(String description, int priority, int targetTimeMinutes) {
        this.description = description;
        this.priority = priority;
        this.targetTimeMinutes = targetTimeMinutes;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public int getTargetTimeMinutes() {
        return targetTimeMinutes;
    }
}

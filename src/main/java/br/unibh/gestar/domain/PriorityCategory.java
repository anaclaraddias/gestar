package br.unibh.gestar.domain;

public enum PriorityCategory {
    HIGHEST_PRIORITY(3),
    PREFERRED(2),
    NORMAL(1);

    private final int weight;

    PriorityCategory(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public static PriorityCategory forAge(int age) {
        if (age >= 80) {
            return HIGHEST_PRIORITY;
        }
        if (age >= 60) {
            return PREFERRED;
        }
        return NORMAL;
    }
}

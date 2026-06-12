package br.unibh.gestar.domain;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

public class Patient {
    private final String id;
    private final String name;
    private final LocalDate birthDate;

    public Patient(String name, LocalDate birthDate) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.birthDate = birthDate;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
}

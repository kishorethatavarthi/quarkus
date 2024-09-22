package org.acme;

import jakarta.persistence.Entity;
import java.time.Instant;
import jakarta.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
@Entity
@Table(name="TASKS")
public class Tasks extends PanacheEntity {
    public Instant createdAt;

    public Tasks() {
        createdAt = Instant.now();
    }

    public Tasks(Instant time) {
        this.createdAt = time;
    }
}

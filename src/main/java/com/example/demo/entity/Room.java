package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String type; // e.g., "VOICE", "TEXT"

    @Column(nullable = true) // Nullable for backward compatibility
    private String hostUsername;

    public Room(String name, String type, String hostUsername) {
        this.name = name;
        this.type = type;
        this.hostUsername = hostUsername;
    }
}

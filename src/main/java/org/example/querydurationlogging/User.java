package org.example.querydurationlogging;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "users")
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String username;


}

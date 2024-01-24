package com.example.demo.events;

import com.example.demo.accounts.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;

    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING) // ???
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    private Account manager;
    public void update() {
        this.free = (this.basePrice == 0 && this.maxPrice == 0) ? true : false;
        this.offline = (this.location == null || this.location.isBlank()) ? false : true;

    }
}

package com.example.UberComp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private LocalDateTime time;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> notifiedUsers;

    public Notification(String title, String content, LocalDateTime time){
        this.title = title;
        this.content = content;
        this.time = time;
    }
}

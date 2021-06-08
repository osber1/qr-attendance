package com.tracking.attendance.qr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "t_faculty")
@NoArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "faculty")
    private List<User> users;

    @OneToMany(mappedBy = "faculty")
    private List<Lecture> lectures;

    @PreRemove
    private void preRemove() {
        users.forEach(user -> user.setFaculty(null));
        lectures.forEach(lecture -> lecture.setFaculty(null));
    }
}
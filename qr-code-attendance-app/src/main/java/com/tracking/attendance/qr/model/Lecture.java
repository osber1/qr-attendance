package com.tracking.attendance.qr.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_lecture")
@NoArgsConstructor
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToMany(mappedBy = "attendableLectures")
    private Set<User> assignedStudents;

    @ManyToMany(mappedBy = "ledLectures")
    private Set<User> assignedLectors;

    @OneToMany(mappedBy = "lecture")
    private List<Event> events;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @PreRemove
    private void preRemove() {
        assignedStudents.forEach(user -> user.getLedLectures().remove(this));
        assignedStudents.forEach(user -> user.getAttendableLectures().remove(this));
        assignedLectors.forEach(user -> user.getLedLectures().remove(this));
        assignedLectors.forEach(user -> user.getAttendableLectures().remove(this));
        events.forEach(s -> s.setLecture(null));
    }
}
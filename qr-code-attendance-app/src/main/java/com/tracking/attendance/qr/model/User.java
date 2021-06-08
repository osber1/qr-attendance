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
@Table(name = "t_user")
@NoArgsConstructor
public class User {
    @Id
    private String id;

    private String name;

    private String surname;

    private String email;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToMany
    @JoinTable(
            name = "student_attendable_lectures",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "lecture_id"))
    private Set<Lecture> attendableLectures;

    @ManyToMany
    @JoinTable(
            name = "lecturer_led_lectures",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "lecture_id"))
    private Set<Lecture> ledLectures;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    private String role;

    @ManyToMany
    @JoinTable(
            name = "student_event",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> studentEvents;

    @OneToMany(mappedBy = "lector")
    private List<Event> lectorEvents;

    @PreRemove
    private void preRemove() {
        studentEvents.forEach(event -> event.getAttendableStudents().remove(this));
        studentEvents.forEach(event -> event.setLector(null));
        attendableLectures.forEach(lecture -> lecture.getAssignedStudents().remove(this));
        attendableLectures.forEach(lecture -> lecture.getAssignedLectors().remove(this));
        ledLectures.forEach(lecture -> lecture.getAssignedStudents().remove(this));
        ledLectures.forEach(lecture -> lecture.getAssignedLectors().remove(this));
        lectorEvents.forEach(user -> user.setLector(null));
    }
}
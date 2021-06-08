package com.tracking.attendance.qr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "t_group")
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "group")
    private List<User> assignedStudents;

    @PreRemove
    private void preRemove() {
        assignedStudents.forEach(user -> user.setGroup(null));
    }
}
create table t_event (
    id integer not null auto_increment,
    end_time datetime(6),
    start_time datetime(6),
    lector_id varchar(255),
    lecture_id integer,
    primary key (id)
) engine=InnoDB;

create table t_faculty (
id integer not null auto_increment,
name varchar(255),
primary key (id)
) engine=InnoDB;

create table t_group (
    id integer not null auto_increment,
    name varchar(255),
    primary key (id)
) engine=InnoDB;

create table t_lecture (
    id integer not null auto_increment,
    name varchar(255),
    faculty_id integer,
    primary key (id)
) engine=InnoDB;

create table t_user (
    id varchar(255) not null,
    email varchar(255),
    name varchar(255),
    role varchar(255),
    surname varchar(255),
    faculty_id integer,
    group_id integer,
    primary key (id)
) engine=InnoDB;

create table lecturer_led_lectures (
    student_id varchar(255) not null,
    lecture_id integer not null,
    primary key (student_id, lecture_id)
) engine=InnoDB;

create table student_attendable_lectures (
    student_id varchar(255) not null,
    lecture_id integer not null,
    primary key (student_id, lecture_id)
) engine=InnoDB;

create table student_event (
    student_id varchar(255) not null,
    event_id integer not null,
    primary key (student_id, event_id)
) engine=InnoDB;

alter table lecturer_led_lectures
    add constraint FK85bgdm4il2d1ta9l5c436hiio
    foreign key (lecture_id)
    references t_lecture (id);

alter table lecturer_led_lectures
    add constraint FK4578uqk6e0i4hchi8vslelare
    foreign key (student_id)
    references t_user (id);

alter table student_attendable_lectures
    add constraint FKsll8y06t08wse7ro3jf3u6yoh
    foreign key (lecture_id)
    references t_lecture (id);

alter table student_attendable_lectures
    add constraint FKo3t9h8wb0xytxsl2ppwik5ljk
    foreign key (student_id)
    references t_user (id);

alter table student_event
    add constraint FKpy350oy7ev2sankfwey4hwme
    foreign key (event_id)
    references t_event (id);

alter table student_event
    add constraint FKi4mfxns32ub3tvydclbqkqm86
    foreign key (student_id)
    references t_user (id);

alter table t_event
    add constraint FKev2wwnp7s0uicmt5yw1kxwl4r
    foreign key (lector_id)
    references t_user (id);

alter table t_event
    add constraint FK6gq0p5r99pgupfx65krvyi9u7
    foreign key (lecture_id)
    references t_lecture (id);

alter table t_lecture
    add constraint FKrgnbfch6dh6c946uw6k46vdee
    foreign key (faculty_id)
    references t_faculty (id);

alter table t_user
    add constraint FK3wehaoerkuwgruxesccjkv2d4
    foreign key (faculty_id)
    references t_faculty (id);

alter table t_user
    add constraint FKi6ctvlw9uxi9xeaqjkksjvfo7
    foreign key (group_id)
    references t_group (id);
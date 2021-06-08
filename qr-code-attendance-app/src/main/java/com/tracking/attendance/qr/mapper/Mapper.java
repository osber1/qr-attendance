package com.tracking.attendance.qr.mapper;

import com.tracking.attendance.qr.*;
import com.tracking.attendance.qr.model.*;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Collection;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {

    EventResponseDTO eventToDTO(Event event);

    Collection<EventResponseDTO> eventToDTOs(Collection<Event> all);

    @Mappings({
            @Mapping(target = "startDate", ignore = true),
            @Mapping(target = "endDate", ignore = true)
    })
    Event eventToEntity(EventRequestDTO eventDTO);

    FacultyDTO facultyToDTO(Faculty faculty);

    Faculty facultyToEntity(FacultyDTO facultyDTO);

    Collection<FacultyDTO> facultyToDTOs(Collection<Faculty> all);

    GroupDTO groupToDTO(Group group);

    Group groupToEntity(GroupDTO groupDTO);

    Collection<GroupDTO> groupToDTOs(Collection<Group> all);

    LectureDTO lectureToDTO(Lecture lecture);

    Lecture lectureToEntity(LectureDTO lectureDTO);

    Collection<LectureDTO> lectureToDTOs(Collection<Lecture> all);

    UserDTO userToDTO(User user);

    User userToEntity(UserDTO userDTO);

    Collection<UserDTO> userToDTOs(Collection<User> all);
}

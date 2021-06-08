package com.tracking.attendance.qr.controller;

import com.tracking.attendance.qr.GroupDTO;
import com.tracking.attendance.qr.mapper.Mapper;
import com.tracking.attendance.qr.model.Group;
import com.tracking.attendance.qr.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService service;
    private final Mapper mapper;

    @PostMapping
    public GroupDTO create(@RequestBody @Valid GroupDTO groupDTO) {
        Group group = mapper.groupToEntity(groupDTO);
        return mapper.groupToDTO(service.create(group));
    }

    @GetMapping
    public Collection<GroupDTO> getAll() {
        return mapper.groupToDTOs(service.getAll());
    }

    @GetMapping("{id}")
    public GroupDTO getOne(@PathVariable Integer id) {
        return mapper.groupToDTO(service.getOne(id));
    }

    @PutMapping
    public GroupDTO update(@RequestBody @Valid GroupDTO groupDTO) {
        Group group = mapper.groupToEntity(groupDTO);
        return mapper.groupToDTO(service.update(group));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
package com.vti.controller;


import com.vti.dto.DepartmentDTO;
import com.vti.entity.Department;
import com.vti.form.DepartmentCreateForm;
import com.vti.form.DepartmentFilterForm;
import com.vti.form.DepartmentUpdateForm;
import com.vti.service.IDepartmentService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private IDepartmentService service;

    @GetMapping
    public Page<DepartmentDTO> findAll(Pageable pageable, DepartmentFilterForm form) {
        Page<Department> page = service.findAll(pageable, form);
        List<Department> departments = page.getContent();
        List<DepartmentDTO> dtos = mapper.map(departments, new TypeToken<List<DepartmentDTO>>(){}.getType());
        for (DepartmentDTO dto : dtos){
            dto.add(linkTo(methodOn(DepartmentController.class).findById(dto.getId())).withSelfRel());
            List<DepartmentDTO.AccountDTO> accountDTOS = dto.getAccounts();
            for (DepartmentDTO.AccountDTO accountDTO : accountDTOS){
                dto.add(linkTo(methodOn(AccountController.class).findById(accountDTO.getId())).withSelfRel());
            }
        }
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @GetMapping("/{id}")
    public DepartmentDTO findById(@PathVariable("id") int id) {
        Department department = service.findById(id);
        DepartmentDTO dto = mapper.map(department, DepartmentDTO.class);
        dto.add(linkTo(methodOn(DepartmentController.class).findById(id)).withSelfRel());
        return dto;
    }

    @GetMapping("/name/{name}")
    public Department findByName(@PathVariable("name") String name) {
        return service.findByName(name);
    }

    @PostMapping
    public void create(@RequestBody DepartmentCreateForm form) {

        service.create(form);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") int id, @RequestBody DepartmentUpdateForm form) {
        form.setId(id);
        service.update(form);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") int id) {
        service.deleteById(id);
    }

    @DeleteMapping("/name/{name}")
    public void deleteByName(@PathVariable("name") String name) {
        service.deleteByName(name);
    }
}

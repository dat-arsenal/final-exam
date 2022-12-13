package com.vti.controller;


import com.vti.dto.DepartmentDTO;
import com.vti.entity.Department;
import com.vti.form.DepartmentCreateForm;
import com.vti.form.DepartmentFilterForm;
import com.vti.form.DepartmentUpdateForm;
import com.vti.service.IDepartmentService;
import com.vti.validation.DepartmentIdExists;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Validated
// dùng để thông báo xác thực dữ liệu
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private IDepartmentService service;

    @GetMapping
    public Page<DepartmentDTO> findAll(@SortDefault(value = "totalMembers", direction = Sort.Direction.DESC) Pageable pageable, DepartmentFilterForm form) {
        Page<Department> page = service.findAll(pageable, form);
        List<Department> departments = page.getContent();
        List<DepartmentDTO> dtos = mapper.map(departments, new TypeToken<List<DepartmentDTO>>(){}.getType());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @GetMapping("/{id}")
    public DepartmentDTO findById(@PathVariable("id") @DepartmentIdExists int id) {
        Department department = service.findById(id);
        DepartmentDTO dto = mapper.map(department, DepartmentDTO.class);
        dto.add(linkTo(methodOn(DepartmentController.class).findById(id)).withSelfRel());
        return dto;
    }

    @PostMapping
    public void create(@RequestBody @Valid DepartmentCreateForm form) {

        service.create(form);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") @DepartmentIdExists int id, @RequestBody DepartmentUpdateForm form) {
        form.setId(id);
        service.update(form);
    }

    @DeleteMapping
    public void deleteAllById(@RequestBody List<Integer> ids) {
        service.deleteAllById(ids);
    }

}

package com.vti.service;

import com.vti.entity.Department;
import com.vti.form.DepartmentCreateForm;
import com.vti.form.DepartmentFilterForm;
import com.vti.form.DepartmentUpdateForm;
import com.vti.repository.IDepartmentRepository;
import com.vti.specification.DepartmentSpecification;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DepartmentService implements IDepartmentService {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private IDepartmentRepository repository;


    @Override
    public Page<Department> findAll(Pageable pageable, DepartmentFilterForm form) {
        Specification<Department> spec = DepartmentSpecification.buildSpec(form);
        return repository.findAll(spec, pageable);
    }

    @Override
    public Department findById(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Department findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void create(DepartmentCreateForm form) {
        Department department = mapper.map(form, Department.class);
        repository.save(department);

    }

    @Override
    public void update(DepartmentUpdateForm form) {
        Department department = mapper.map(form, Department.class);
        repository.save(department);
    }

    @Override
    public void deleteById(int id) {

        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByName(String name) {
        repository.deleteByName(name);
    }
}

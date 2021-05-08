package de.legendlime.demo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import de.legendlime.demo.model.Department;

public interface DepartmentRepository extends ReactiveCrudRepository<Department, Long> {
	
}

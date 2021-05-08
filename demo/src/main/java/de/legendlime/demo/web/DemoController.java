package de.legendlime.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.legendlime.demo.model.Department;
import de.legendlime.demo.model.DepartmentDTO;
import de.legendlime.demo.repository.DepartmentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class DemoController {

	private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

	@Autowired
	DepartmentRepository repo;

	@GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
	@NewSpan("hello")
	public Mono<String> hello(Authentication authentication) {

		// For JWT based authentication, one has to extract the access token from the
		// Authentication object
		// For opaque tokens one can inject @AuthenticationPrincipal
		// OAuth2AuthenticatedPrincipal principal
		// but opaque tokens only has the SCOPE authorities as authorities.

		Jwt jwt = (Jwt) authentication.getPrincipal();

		LOG.debug("JWT token: {} ", jwt.getTokenValue());
		LOG.debug("Username : {} ", jwt.getClaim("preferred_username").toString());
		String name = jwt.getClaimAsString("name");
		LOG.debug("Full name: {} ", name != null ? name : "");
		LOG.debug("Roles    : {} ", authentication.getAuthorities().toString());

		return Mono.just("hello world");
	}

	@GetMapping(value = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
	@NewSpan("getDepartments")
	public Flux<Department> getDepartments() {
		return repo.findAll();
	}

	@GetMapping(value = "/departments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@NewSpan("getDepartment")
	public Mono<ResponseEntity<Department>> getDepartment(@PathVariable Long id) {
		Mono<Department> dept = repo.findById(id);
		return dept.map(d -> ResponseEntity
				.ok(d))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping(value = "/departments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@NewSpan("createDepartment")
	public Mono<ResponseEntity<Department>> createDepartment(@RequestBody DepartmentDTO deptDTO) {

		Department dept = new Department();
		dept.setDeptId(deptDTO.getDeptId());
		dept.setName(deptDTO.getName());
		dept.setDescription(deptDTO.getDescription());
		// tell the persistence layer that this is a new entry
		dept.setAsNew();
		
		Mono<Department> department = repo.save(dept);
		return department.map(d -> ResponseEntity
				.ok(d))
				.defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PutMapping(value = "/departments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@NewSpan("updateDepartment")
	public Mono<ResponseEntity<Department>> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO deptDTO) {

		Department dept = new Department();
		dept.setDeptId(deptDTO.getDeptId());
		dept.setName(deptDTO.getName());
		dept.setDescription(deptDTO.getDescription());
		
		Mono<Department> department = repo.save(dept);
		return department.map(d -> ResponseEntity
				.ok(d))
				.defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@DeleteMapping(value = "/departments/{id}")
	@NewSpan("deleteDepartment")
	public Mono<ResponseEntity<Void>> deleteDepartment(@PathVariable Long id) {
		return repo.deleteById(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
	}
}

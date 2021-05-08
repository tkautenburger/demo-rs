package de.legendlime.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import de.legendlime.demo.model.Department;
import de.legendlime.demo.repository.DepartmentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This code is not used yet, we are working with the annotated controller
 * @author thomas kautenburger
 *
 */
public class DemoHandler {
	
	boolean exist;

	@Autowired
	DepartmentRepository repo;

	public Mono<ServerResponse> getDepartmentsHandler(ServerRequest request) {
		Flux<Department> depts = repo.findAll();
		return ServerResponse.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(depts, Department.class);
	}

	public Mono<ServerResponse> getDepartment(ServerRequest request) {
		long id = Long.valueOf(request.pathVariable("id"));
		return repo.findById(id)
			.flatMap(dept -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(dept))
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> createDepartment(ServerRequest request) {
        Mono<Department> dept = request.bodyToMono(Department.class);
        Department d = dept.block();
        d.setAsNew();
        return ServerResponse.ok()
        	.contentType(MediaType.APPLICATION_JSON)
        	.bodyValue(repo.save(d));
    }

	public Mono<ServerResponse> updateDepartment(ServerRequest request) {

        Mono<Department> dept = request.bodyToMono(Department.class);
		long id = Long.valueOf(request.pathVariable("id"));
		repo.existsById(id).subscribe(exist -> this.exist = exist);
		if (exist)
			return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(repo.save(dept.block()));
		else
			return ServerResponse.notFound().build();
	}

	public Mono<ServerResponse> deleteDepartment(ServerRequest request) {
		long id = Long.valueOf(request.pathVariable("id"));

		return ServerResponse.ok()
			.build(repo.deleteById(id));
	}
}

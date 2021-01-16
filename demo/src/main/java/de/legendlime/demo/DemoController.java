package de.legendlime.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	@GetMapping(value = "/hello")
	public String hello() {
		return "hello world";
	}

}

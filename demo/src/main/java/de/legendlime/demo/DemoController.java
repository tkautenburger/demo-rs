package de.legendlime.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class DemoController {

	private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

	@GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
	public Mono<String> hello(Authentication authentication) {
		
		// For JWT based authentication, one has to extract the access token from the Authentication object
		// For opaque tokens one can inject @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal
		// but opaque tokens only has the SCOPE authorities as authorities.
		
		Jwt jwt = (Jwt) authentication.getPrincipal();
		
		LOG.debug("JWT token: {} ", jwt.getTokenValue());
		LOG.debug("Username : {} ", jwt.getClaim("preferred_username").toString());
		String name = jwt.getClaimAsString("name");
		LOG.debug("Full name: {} ", name != null ? name : "");
		LOG.debug("Roles    : {} ", authentication.getAuthorities().toString());
		
		return Mono.just("hello world");
	}
}

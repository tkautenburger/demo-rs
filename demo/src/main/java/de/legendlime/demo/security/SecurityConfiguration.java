package de.legendlime.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * OAuth2 Security Configuration for Resource Server
 * @author Thomas Kautenburger
 *
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;

    private SecurityProperties props;
	
	public SecurityConfiguration(SecurityProperties props) {
		this.props = props;
	}

	/**
	 * Set the custom authentication converter for Keycloak in the HTTP security configurer
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {// @formatter:off
		http.cors()
        .and()
          .authorizeRequests()
            .antMatchers(HttpMethod.GET).hasAuthority("ROLE_CLIENT_hello")
            .antMatchers(HttpMethod.POST, "/departments/**").hasAuthority("ROLE_CLIENT_hello")
            .antMatchers(HttpMethod.DELETE, "/departments/**").hasAuthority("ROLE_CLIENT_hello")
            .antMatchers(HttpMethod.PUT, "/departments/**").hasAuthority("ROLE_CLIENT_hello")
            .anyRequest()
              .authenticated()
        .and()
          .oauth2ResourceServer()
            .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter(props.getClientId(), 
            		props.isAddRealm())));
	}// @formatter:on

	/**
	 * Validate the audience claim against the client ID of the application
	 * @author Thomas Kautenburger
	 *
	 */
	public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
		OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

		public OAuth2TokenValidatorResult validate(Jwt jwt) {
			// Check if demo-resource service is contained in the audience claim
			// This is achieved by assigning the user a client-specific role at the auth
			// service
			if (jwt.getAudience() != null && jwt.getAudience().contains(props.getClientId())) {
				return OAuth2TokenValidatorResult.success();
			} else {
				return OAuth2TokenValidatorResult.failure(error);
			}
		}
	}
	/**
	 * Validate the issuer claim against the issuer URI from the application configuration
	 * @author Thomas Kautenburger
	 *
	 */
	public class IssuerValidator implements OAuth2TokenValidator<Jwt> {
		OAuth2Error error = new OAuth2Error("invalid_token", "The issuer does not match", null);

		public OAuth2TokenValidatorResult validate(Jwt jwt) {
			if (props.isIssuerCheck() == false) {
				// issuer check disabled, skip it
				return OAuth2TokenValidatorResult.success();
			}
			// Check if the issuer URI matches the one in the JWT token
			if (jwt.getIssuer() != null && jwt.getIssuer().toString().contains(issuerUri)) {
				return OAuth2TokenValidatorResult.success();
			} else {
				return OAuth2TokenValidatorResult.failure(error);
			}
		}
	}

	/**
	 * Add custom validators to JWT decoder
	 * @return JWT Decoder with custom validators
	 */
	@Bean
	JwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

		OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator();
		OAuth2TokenValidator<Jwt> withIssuer = new IssuerValidator();
		OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

		jwtDecoder.setJwtValidator(validator);

		return jwtDecoder;
	}
	
}

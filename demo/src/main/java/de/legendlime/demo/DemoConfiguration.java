package de.legendlime.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;


@Configuration
@ConditionalOnProperty(prefix = "legendlime", value = "local-validation", havingValue = "true")
public class DemoConfiguration {
	
	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;


	public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
		OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

		public OAuth2TokenValidatorResult validate(Jwt jwt) {
			// Check if demo-resource service is contained in the audience claim
			// This is achieved by assigning the user a client-specific role at the auth service 
			if (jwt.getAudience().contains("demo-resource")) {
				return OAuth2TokenValidatorResult.success();
			} else {
				return OAuth2TokenValidatorResult.failure(error);
			}
		}
	}

	@Bean
	JwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

		OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator();
		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
		OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

		jwtDecoder.setJwtValidator(withAudience);

		return jwtDecoder;
	}
}



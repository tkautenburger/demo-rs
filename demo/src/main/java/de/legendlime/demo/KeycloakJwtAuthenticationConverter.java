package de.legendlime.demo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Custom converter to extract realm roles and client roles from Keycloak issued
 * JWT access token
 * 
 * @author Thomas Kautenburger
 * 
 */
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private static final Logger LOG = LoggerFactory.getLogger(KeycloakJwtAuthenticationConverter.class);
	private static final String REALM_PREFIX = "ROLE_REALM_";
	private static final String CLIENT_PREFIX = "ROLE_CLIENT_";
	private static final String REALM_NODE = "realm_access";
	private static final String RESOURCE_NODE = "resource_access";
	private static final String ROLES_NODE = "roles";

	private final String clientId;
	private final boolean addRealm;

	public KeycloakJwtAuthenticationConverter(String clientId, boolean addRealm) {
		this.clientId = clientId;
		this.addRealm = addRealm;
	}

	@SuppressWarnings("unchecked")
	private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt, final String clientId,
			boolean addRealm) {

		Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_NODE);
		Map<String, Object> realmAccess = jwt.getClaim(REALM_NODE);
		Map<String, Object> resource;
		Collection<String> resourceRoles;
		Collection<String> realmRoles;
		Set<GrantedAuthority> roles = new HashSet<>();

		// collect all client roles from resource access claim
		if (resourceAccess != null 
				&& (resource = (Map<String, Object>) resourceAccess.get(clientId)) != null
				&& (resourceRoles = (Collection<String>) resource.get(ROLES_NODE)) != null) {
			for ( String role : resourceRoles) {
				roles.add(new SimpleGrantedAuthority(CLIENT_PREFIX + role));
				LOG.debug("added resource role: {}", CLIENT_PREFIX + role);
			}
		}
		// collect all realm access roles if they shall be added
		if (addRealm && realmAccess != null 
				&& (realmRoles = (Collection<String>) realmAccess.get("roles")) != null) {
			for ( String role : realmRoles) {
				roles.add(new SimpleGrantedAuthority(REALM_PREFIX + role));
				LOG.debug("added realm role: {}", REALM_PREFIX + role);
			}
		}
		return roles;
	}

	private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

	@Override
	public AbstractAuthenticationToken convert(final Jwt source) {
		Collection<GrantedAuthority> authorities = Stream
				.concat(defaultGrantedAuthoritiesConverter.convert(source).stream(),
						extractResourceRoles(source, clientId, addRealm).stream())
				.collect(Collectors.toSet());
		return new JwtAuthenticationToken(source, authorities);
	}
}
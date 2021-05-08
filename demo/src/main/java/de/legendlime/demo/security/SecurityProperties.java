package de.legendlime.demo.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "legendlime.oauth2")
public class SecurityProperties {
	/**
	 * Check issuer claim, default: true
	 */
	private boolean issuerCheck = true;
	/**
	 * Add realm roles to authorities list, default: true
	 */
	private boolean addRealm = true;
	/**
	 * Client ID with authentication server
	 */
	private String clientId;

	public boolean isIssuerCheck() {
		return issuerCheck;
	}
	public void setIssuerCheck(boolean issuerCheck) {
		this.issuerCheck = issuerCheck;
	}
	public boolean isAddRealm() {
		return addRealm;
	}
	public void setAddRealm(boolean addRealm) {
		this.addRealm = addRealm;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
}

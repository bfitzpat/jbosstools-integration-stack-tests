package org.jboss.tools.runtime.reddeer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Remote {

	@XmlElement(namespace = Namespaces.SOA_REQ)
	protected String host;

	@XmlElement(namespace = Namespaces.SOA_REQ)
	private String remoteHome;

	@XmlElement(namespace = Namespaces.SOA_REQ)
	private String username;

	@XmlElement(namespace = Namespaces.SOA_REQ)
	private String password;

	@XmlElement(namespace = Namespaces.SOA_REQ)
	private boolean isExternallyManaged;

	@XmlElement(namespace = Namespaces.SOA_REQ)
	private boolean useManagementOperations;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getRemoteHome() {
		return remoteHome;
	}

	public void setRemoteHome(String remoteHome) {
		this.remoteHome = remoteHome;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isExternallyManaged() {
		return isExternallyManaged;
	}

	public void setExternallyManaged(boolean isExternallyManaged) {
		this.isExternallyManaged = isExternallyManaged;
	}

	public boolean isUseManagementOperations() {
		return useManagementOperations;
	}

	public void setUseManagementOperations(boolean useManagementOperations) {
		this.useManagementOperations = useManagementOperations;
	}

}

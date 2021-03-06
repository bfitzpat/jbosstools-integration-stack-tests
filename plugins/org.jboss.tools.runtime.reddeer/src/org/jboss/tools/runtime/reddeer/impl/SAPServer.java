package org.jboss.tools.runtime.reddeer.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.runtime.reddeer.Namespaces;

@XmlRootElement(name = "server", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class SAPServer {

	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "destination", namespace = Namespaces.SOA_REQ)
	private String destination;

	@XmlElement(name = "gwhost", namespace = Namespaces.SOA_REQ)
	private String gwhost;

	@XmlElement(name = "gwport", namespace = Namespaces.SOA_REQ)
	private String gwport;

	@XmlElement(name = "progid", namespace = Namespaces.SOA_REQ)
	private String progid;

	@XmlElement(name = "connectionCount", namespace = Namespaces.SOA_REQ)
	private String connectionCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getGwhost() {
		return gwhost;
	}

	public void setGwhost(String gwhost) {
		this.gwhost = gwhost;
	}

	public String getGwport() {
		return gwport;
	}

	public void setGwport(String gwport) {
		this.gwport = gwport;
	}

	public String getProgid() {
		return progid;
	}

	public void setProgid(String progid) {
		this.progid = progid;
	}

	public String getConnectionCount() {
		return connectionCount;
	}

	public void setConnectionCount(String connectionCount) {
		this.connectionCount = connectionCount;
	}

}

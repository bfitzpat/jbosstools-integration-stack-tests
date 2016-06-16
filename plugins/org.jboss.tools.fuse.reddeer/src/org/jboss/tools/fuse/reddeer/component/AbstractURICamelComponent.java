package org.jboss.tools.fuse.reddeer.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.tools.fuse.reddeer.utils.CamelComponentUtils;

/**
 * 
 * @author apodhrad
 *
 */
public abstract class AbstractURICamelComponent implements CamelComponent {

	private String baseUri;
	private List<String> keyList;
	private Properties properties;

	public AbstractURICamelComponent(String baseUri) {
		this.baseUri = baseUri;
		keyList = new ArrayList<String>();
		properties = new Properties();
	}

	@Override
	public String getLabel() {
		return CamelComponentUtils.getLabel(getUri());
	}

	@Override
	public String getTooltip() {
		return getUri();
	}

	public String getUri() {
		StringBuffer uri = new StringBuffer(baseUri);
		for (String key : keyList) {
			uri.append(":").append(getProperty(key));
		}
		return uri.toString();
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	protected void addProperty(String key, String value) {
		properties.setProperty(key, value);
		keyList.add(key);
	}

}

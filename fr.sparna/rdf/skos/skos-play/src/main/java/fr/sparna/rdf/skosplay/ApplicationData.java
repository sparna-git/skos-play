package fr.sparna.rdf.skosplay;

import java.util.Map;

import javax.servlet.ServletContext;

import org.openrdf.repository.Repository;

/**
 * Everything that needs to be loaded at an application-wide level
 * 
 * @author Thomas Francart
 */
public class ApplicationData {

	public static final String KEY = "applicationData";
	
	protected String buildTimestamp;
	protected Map<String, Repository> exampleDatas;

	public void register(ServletContext ctx) {
		if(ctx.getAttribute(KEY) != null) {
			throw new IllegalStateException();
		}
		ctx.setAttribute(KEY, this);
	}
	
	public static ApplicationData get(ServletContext ctx) {
		return (ApplicationData)ctx.getAttribute(KEY);
	}
	
	public Map<String, Repository> getExampleDatas() {
		return exampleDatas;
	}

	public void setExampleDatas(Map<String, Repository> exampleDatas) {
		this.exampleDatas = exampleDatas;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public void setBuildTimestamp(String buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}
	
}

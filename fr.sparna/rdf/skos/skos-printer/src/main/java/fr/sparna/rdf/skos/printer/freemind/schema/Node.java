package fr.sparna.rdf.skos.printer.freemind.schema;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {

	@XmlAttribute(name = "CREATED")
	private String created;
	
	@XmlAttribute(name = "ID")
	private String id;
	
	@XmlAttribute(name = "MODIFIED")
	private String modified;
	
	@XmlAttribute(name = "TEXT")
	private String text;
	
	@XmlElement(name = "node")
	private List<Node> childrens;


	
	public Node(String id, String text) {
		super();
		this.id = id;
		this.text = text;
		this.created = Long.toString(System.currentTimeMillis());
		this.modified = Long.toString(System.currentTimeMillis());
		this.childrens = new ArrayList<Node>();
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Node> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<Node> childrens) {
		this.childrens = childrens;
	}
	
	
	
}

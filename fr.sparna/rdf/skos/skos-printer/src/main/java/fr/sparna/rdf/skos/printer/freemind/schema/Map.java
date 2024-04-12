package fr.sparna.rdf.skos.printer.freemind.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {

	@XmlAttribute(name = "version")
	private String version = "1.1.0";
	
	@XmlElement(name = "node")
	private Node node;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
}

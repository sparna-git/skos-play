package fr.sparna.rdf.skosplay.log;

public class LogEntry {
  public String name;
  public String output;
  public boolean zip;
  public boolean xls;
  public boolean graph;
  public String  actiontype;
  public String  actiondate;
  public int comptconvert;
  public int comptprint;
  
  public String getName() {
	  return name;
  }
  public void setName(String name) {
	  this.name = name;
  }
  public String getOutput() {
	  return output;
  }
  public void setOutput(String output) {
	  this.output = output;
  }
  public boolean isZip() {
	  return zip;
  }
  public void setZip(boolean ziP) {
	  zip = ziP;
  }
  public boolean isGraph() {
	  return graph;
  }
  public void setGraph(boolean graphe) {
	  graph = graphe;
  }
  public boolean isXls() {
	  return xls;
  }
  public void setXls(boolean xls) {
	  this.xls = xls;
  }
  public String getActiontype() {
	  return actiontype;
  }
  public void setActiontype(String actiontype) {
	  this.actiontype = actiontype;
  }
  public String getActiondate() {
	  return actiondate;
  }
  public void setActiondate(String actiondate) {
	  this.actiondate = actiondate;
  }
  @Override
  public String toString() {
	  return "LogEntry [name=" + name + ", output=" + output + ", zip=" + zip + ", xls=" + xls + ", graph=" + graph
			  + ", actiontype=" + actiontype + ", actiondate=" + actiondate + "]\n";
  }
  public int getComptconvert() {
	  return comptconvert;
  }
  public void setComptconvert(int comptconvert) {
	  this.comptconvert = comptconvert;
  }
  public int getComptprint() {
	  return comptprint;
  }
  public void setComptprint(int comptprint) {
	  this.comptprint = comptprint;
  }

}

package uk.bl.odin.orcid.client;

public enum SearchType {
	PREFIX("prefix"),
	SOLR("solr"),
	EXACT("exact");
	
	private final String stringValue;
	private SearchType(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	public static SearchType fromString(String text) {
	    if (text != null) {
	      for (SearchType b : SearchType.values()) {
	        if (text.equals(b.toString())) {
	          return b;
	        }
	      }
	    }
	    throw new IllegalArgumentException("Invalid identifier type");
	  }
	
}

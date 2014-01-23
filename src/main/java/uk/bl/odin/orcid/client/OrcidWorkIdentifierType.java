package uk.bl.odin.orcid.client;

/** Orcid work identifier.
 * Not defined in the Schema.
 * @see http://support.orcid.org/knowledgebase/articles/118807
 * TODO: add them
 */
public enum OrcidWorkIdentifierType {
	DISSERTATION("dissertation");
	
	private final String stringValue;
	private OrcidWorkIdentifierType(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	//note handles are not a top level type!!!
}

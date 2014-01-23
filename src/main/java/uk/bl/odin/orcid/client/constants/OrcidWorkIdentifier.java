package uk.bl.odin.orcid.client.constants;

/** Orcid work identifier.
 * Not defined in the Schema.
 * @see http://support.orcid.org/knowledgebase/articles/118807
 * TODO: add them
 */
public enum OrcidWorkIdentifier {
	DISSERTATION("dissertation");
	
	private final String stringValue;
	private OrcidWorkIdentifier(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	//note handles are not a top level type!!!
}

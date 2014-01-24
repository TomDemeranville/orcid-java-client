package uk.bl.odin.orcid.client.constants;

/** Orcid contributor sequence
 * These are not defined in the Schema.
 * @see http://support.orcid.org/knowledgebase/articles/118843
 */
public enum OrcidContributorSequence {
	FIRST("first"),
	ADDITIONAL("additional");
	private final String stringValue;
	private OrcidContributorSequence(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
}

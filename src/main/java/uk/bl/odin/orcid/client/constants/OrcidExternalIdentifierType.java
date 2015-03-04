package uk.bl.odin.orcid.client.constants;

/** External Identifier Types.
 * Not included in ORCiD schema.
 * @see http://support.orcid.org/knowledgebase/articles/118795
 * @author tom
 *
 */
public enum OrcidExternalIdentifierType {
	OTHER_ID("other-id"),
	ARXIV("arxiv"),
	ASIN("asin"),
	ASIN_TLD("sin-tld"),
	BIBCODE("bibcode"),
	DOI("doi"),
	EID("eid"),
	ISBN("isbn"),
	ISSN("issn"),
	JFM("jfm"),
	JSTOR("jstor"),
	LCCN("lccn"),
	MR("mr"),
	OCLC("oclc"),
	OL("ol"),
	OSTI("osti"),
	PMC("pmc"),
	PMID("pmid"),
	RFC("rfc"),
	SSRN("ssrn"),
	ZBL("zbl");
	
	private final String stringValue;
	private OrcidExternalIdentifierType(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	
	public static OrcidExternalIdentifierType fromString(String text) {
	    if (text != null) {
	      for (OrcidExternalIdentifierType b : OrcidExternalIdentifierType.values()) {
	        if (text.equals(b.toString())) {
	          return b;
	        }
	      }
	    }
	    throw new IllegalArgumentException("Invalid identifier type");
	  }
	
	public OrcidSearchField toOrcidSearchField(){
		if (this == DOI){
			return OrcidSearchField.DIGITAL_OBJECT_IDS;
		} else if (this == OTHER_ID){
			return OrcidSearchField.OTHER_IDENTIFIER_TYPE;
		}
		return OrcidSearchField.fromString(this.toString());
	}
}

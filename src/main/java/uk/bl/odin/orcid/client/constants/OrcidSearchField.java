package uk.bl.odin.orcid.client.constants;

/** Models orcid search vocabulary.
 * @see http://support.orcid.org/knowledgebase/articles/132354-searching-with-the-public-api
 * 
 * @author tom
 *
 */
public enum OrcidSearchField {
	ORCID("orcid"),
	GIVEN_NAMES("given-names"),
	FAMILY_NAME("family-name"),
	CREDIT_NAME("credit-name"),
	OTHER_NAMES("other-names"),
	EMAIL("email"),
	WORK_TITLES("work-titles"),
	KEYWORDS("keywords"),
	TEXT("text"),
	
	WORK_ID_OTHER_ID("other-id"),
	WORK_ID_ARXIV("arxiv"),
	WORK_ID_ASIN("asin"),
	WORK_ID_ASIN_TLD("sin-tld"),
	WORK_ID_BIBCODE("bibcode"),
	WORK_ID_DOI("digital-object-ids"),
	WORK_ID_EID("eid"),
	WORK_ID_ISBN("isbn"),
	WORK_ID_ISSN("issn"),
	WORK_ID_JFM("jfm"),
	WORK_ID_JSTOR("jstor"),
	WORK_ID_LCCN("lccn"),
	WORK_ID_MR("mr"),
	WORK_ID_OCLC("oclc"),
	WORK_ID_OL("ol"),
	WORK_ID_OSTI("osti"),
	WORK_ID_PMC("pmc"),
	WORK_ID_PMID("pmid"),
	WORK_ID_RFC("rfc"),
	WORK_ID_SSRN("ssrn"),
	WORK_ID_ZBL("zbl");
	
	private final String stringValue;
	private OrcidSearchField(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	
	/** Constructs a param suitable for use as a query that EXACTLY matches term
	 * 
	 * @param field
	 * @param term
	 * @return
	 */
	public final String buildExactQuery(String term) {
		return this.toString() + ": \"" + term + "\"";
	}
	
	/** Constructs a param suitable for use as a query that matches profiles STARTING WITH the term
	 * 
	 * @param field
	 * @param term
	 * @return
	 */
	public final String buildPrefixQuery(String term) {
		return this.toString() + ": " + term + "*";
	}
	
	public static OrcidSearchField fromString(String text) {
	    if (text != null) {
	      for (OrcidSearchField b : OrcidSearchField.values()) {
	        if (text.equals(b.toString())) {
	          return b;
	        }
	      }
	    }
	    throw new IllegalArgumentException("invalid search field");
	  }
}

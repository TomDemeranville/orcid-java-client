package uk.bl.odin.orcid.client.constants;

import uk.bl.odin.orcid.client.SearchType;

/** Models orcid search vocabulary.
 * @see http://support.orcid.org/knowledgebase/articles/132354-searching-with-the-public-api
 * Note, above inaccurate.
 * 
 * Taken from the SOLR vocab at 
 * https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/java/schema/constants/SolrConstants.java
 * @author tom
 *
 */
public enum OrcidSearchField {
    SCORE("score"),
    ORCID("orcid"),
    GIVEN_NAMES("given-names"),
    FAMILY_NAME("family-name"),
    GIVEN_AND_FAMILY_NAMES("given-and-family-names"),
    EMAIL_ADDRESS("email"),
    AFFILIATE_PAST_INSTITUTION_NAMES("past-institution-affiliation-name"),
    AFFILIATE_PRIMARY_INSTITUTION_NAMES("current-primary-institution-affiliation-name"),
    AFFILIATE_INSTITUTION_NAME("current-institution-affiliation-name"),
    CREDIT_NAME("credit-name"),
    OTHER_NAMES("other-names"),
    EXTERNAL_ID_ORCIDS("external-id-orcid"),
    EXTERNAL_ID_REFERENCES("external-id-reference"),
    EXTERNAL_ID_ORCIDS_AND_REFERENCES("external-id-orcid-and-reference"),
    DIGITAL_OBJECT_IDS("digital-object-ids"),
    WORK_TITLES("work-titles"),
    GRANT_NUMBERS("grant-numbers"),
    FUNDING_TITLES("funding-titles"),
    PATENT_NUMBERS("patent-numbers"),
    KEYWORDS("keyword"),
    TEXT("text"),
    PUBLIC_PROFILE("public-profile-message"),
    PRIMARY_RECORD("primary-record"),
    ARXIV("arxiv"),
    ASIN("asin"),
    ASIN_TLD("asin-tld"),
    BIBCODE("bibcode"),
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
    ZBL("zbl"),
    OTHER_IDENTIFIER_TYPE("other-identifier-type"),
    ORG_DISAMBIGUATED_ID("org-disambiguated-id"),
    ORG_DISAMBIGUATED_NAME("org-disambiguated-name"),
    ORG_DISAMBIGUATED_CITY("org-disambiguated-city"),
    ORG_DISAMBIGUATED_REGION("org-disambiguated-region"),
    ORG_DISAMBIGUATED_COUNTRY("org-disambiguated-country"),
    ORG_DISAMBIGUATED_TYPE("org-disambiguated-type"),
    ORG_DISAMBIGUATED_POPULARITY("org-disambiguated-popularity"),
    ORG_NAMES("org-names");
	
	private final String stringValue;
	private OrcidSearchField(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	
	public final String buildQuery(SearchType type, String term){
		if (type == SearchType.EXACT) {
			return buildExactQuery(term);
		}
		else if(type == SearchType.PREFIX) {
			return buildPrefixQuery(term);
		}
		else {
			return buildSolrQuery(term);
		}
	}
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
		return this.toString() + ": \"" + term + "*\"";
	}
	
	public final String buildSolrQuery(String term) {
		return this.toString() + ": " + term;
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

package uk.bl.odin.orcid.client;

/** External Identifier Types.
 * Not included in ORCiD schema.
 * @see http://support.orcid.org/knowledgebase/articles/118795
 * @author tom
 *
 */
public enum OrcidExternalIdentifier {
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
	private OrcidExternalIdentifier(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
}

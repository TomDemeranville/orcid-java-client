package uk.bl.odin.orcid.client.constants;

/** Orcid work identifier.
 * These are not defined in the Schema.
 * @see http://support.orcid.org/knowledgebase/articles/118807
 * 
 * TODO: test these.  The listing on the website has spaces, but API expects dashes.  Other problems may exist.
 */
public enum OrcidWorkType {
	BOOK("book"),
	BOOK_CHAPTER("book-chapter"),
	BOOK_REVIEW("book-review"),
	DICTIONARY_ENTRY("dictionary-entry"),
	DISSERTAION("dissertation"),
	ENCYCLOPEDIA_ARTICLE("encyclopedia-article"),
	EDITED_BOOK("edited-book"),
	JOURNAL_ARTICLE("journal-article"),
	JOURNAL_ISSUE("journal-issue"),
	MAGAZINE_ARTICLE("magazine-article"),
	MANUAL("manual"),
	ONLINE_RESOURCE("online-resource"),
	NEWSLETTER_ARTICLE("newsletter-article"),
	NEWSPAPER_ARTICLE("newspaper-article"),
	REPORT("report"),
	RESEARCH_TOOL("research-tool"),
	SUPERVISED_STUDENT_PUBLICATION("supervised-student-publication"),
	TEST("test"),
	TRANSLATION("translation"),
	WEBSITE("website"),
	CONFERENCE_ABSTRACT("conference-abstract"),
	CONFERENCE_PAPER("conference-paper"),
	CONFERENCE_POSTER("conference-poster"),
	DISCLOSURE("disclosure"),
	LICENCE("license"),
	PATENT("patent"),
	REGISTERED_COPYRIGHT("registered-copyright"),
	ARTISITIC_PERFORMANCE("artistic-performance"),
	DATASET("data-set"),
	INVENTION("invention"),
	LECTURE_SPEECH("lecture/speech"),
	RESEARCH_TECHNIQUE("research-technique"),
	SPIN_OFF_COMPANY("spin-off-company"),
	STANDARDS_AND_POLICY("standards-and-policy"),
	TECHNICAL_STANDARD("technical-standard"),
	OTHER("other");
	
	private final String stringValue;
	private OrcidWorkType(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	//note handles are not a top level type!!!
}

package uk.bl.odin.orcid.client.constants;

/** Orcid roles
 * These are not defined in the Schema.
 * @see http://support.orcid.org/knowledgebase/articles/118843
 */
public enum OrcidContributorRole {
	AUTHOR("author"),  
	ASSIGNEE("assignee"),  
	EDITOR("editor"),  
	CHAIR_OR_TRANSLATOR("chair-or-translator"),  
	CO_INVESTIGATOR("co-investigator"),  
	CO_INVENTOR("co-inventor"),  
	GRADUATE_STUDENT("graduate-student"),  
	OTHER_INVENTOR("other-inventor"),  
	PRINCIPAL_INVESTIGATOR("principal-investigator"),  
	POSTDOCTORAL_RESEARCHER("postdoctoral-researcher"),  
	SUPPORT_STAFF("support-staff");
	private final String stringValue;
	private OrcidContributorRole(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
}

package uk.bl.odin.orcid.client.constants;

/**
 * Models the various Auth scopes
 *
 * @See http://members.orcid.org/api/orcid-scopes
 *
 * @author tom
 *
 */
public enum OrcidAuthScope {

    AUTHENTICATE("/authenticate"),
    
    /**
     * Read Entire Record
     */
    READ_PROFILE("/orcid-profile/read-limited"),
    /**
     * Members are encouraged to use READ_PROFILE
     */
    READ_BIO("/orcid-bio/read-limited"),
    /**
     * Members are encouraged to use READ_PROFILE
     */
    READ_WORKS("/orcid-works/read-limited"),
    READ_PUBLIC("/read-public"),
    
    CREATE_WORKS("/orcid-works/create"),
    CREATE_EXTERNAL_ID("/orcid-bio/external-identifiers/create"),
    CREATE_AFFILIATIONS("/affiliations/create"),
    CREATE_FUNDING("/funding/create"),
    
    /**
     * Create and update works, funding, and affiliations
     */
    UPDATE_ACTIVITIES("/activities/update"),

    UPDATE_BIO("/orcid-bio/update"),
    UPDATE_WORKS("/orcid-works/update"),
    UPDATE_AFFILIATIONS("/affiliations/update"),
    UPDATE_FUNDING("/funding/update"),
    
    /**
     * Replaced with READ_PUBLIC
     *
     * @deprecated
     */
    @Deprecated
    READPUBLIC("/read-public"),
    
    /**
     * 
     * @deprecated
     */
    @Deprecated
    CREATE_PROFILE("/orcid-profile/create");
    
    //affiliation schema is 1.2! //see http://support.orcid.org/knowledgebase/articles/269377-tutorial-add-affiliations-with-curl
    //CREATE_AFFILIATION("/affiliations/create");

    private final String stringValue;

    private OrcidAuthScope(final String s) {
        stringValue = s;
    }

    public String toString() {
        return stringValue;
    }
}

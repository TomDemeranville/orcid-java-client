package uk.bl.odin.orcid.client.constants;

import org.restlet.data.MediaType;

/**
 * ORCID message constants not defined in schema
 */
public class OrcidConstants {
	
	public static final MediaType APPLICATION_VND_ORCID_XML = MediaType.register("application/vnd.orcid+xml",
			"application/vnd.orcid+xml");
	public static final MediaType APPLICATION_ORCID_JSON = MediaType.register("application/orcid+json",
			"application/orcid+json");
	public static final MediaType APPLICATION_ORCID_XML = MediaType.register("application/orcid+xml",
			"application/orcid+xml");
	
	//TODO: enumerate search param types
	public static final String SEARCH_PARAM_DOI = "digital-object-ids";
	public static final String MESSAGE_VERSION = "1.2";
	
	//TODO: emumerate sequences
	public static String SEQUENCE_FIRST = "first";
}

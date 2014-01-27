package uk.bl.odin.orcid.client;

import java.io.IOException;

import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import uk.bl.odin.orcid.client.constants.OrcidConstants;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidMessage;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidProfile;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidSearchResults;

/** Orcid client that fetches profiles and performs searches using the (LIVE) orcid public api.
 */
public class OrcidPublicClient {

	private static final String PUBLIC_URI_V11 = "http://pub.orcid.org/v1.1";
	private static final String SEARCH_ENDPOINT = "/search/orcid-bio/";
	
	private static final String TYPE_ORCID_BIO = "orcid-bio";
	private static final String TYPE_ORCID_PROFILE = "orcid-profile";

	/** Perform a search against the public ORCID API
	 * 
	 * @param query the 'q' GET param to send
	 * @param page the 'page' GET param to send, starts from 0 - -1 will ommit this paramter and use ORCiD default page (0)
	 * @param pagesize the 'page' GET param to send - -1 will ommit this parameter and use ORCiD default pagesize (10)
	 * @return an OrcidSearchResults object with 0 or more OrcidSearchResult children
	 * @throws IOException if result unparsable or network unreachable.
	 * @throws ResourceException if there's a http problem (e.g. 404, 400)
	 */
	public OrcidSearchResults search(String query, int page, int pagesize) throws IOException {
		if (query == null || query.isEmpty())
			throw new IllegalArgumentException();
		ClientResource res = new ClientResource(PUBLIC_URI_V11 + SEARCH_ENDPOINT);
		res.accept(OrcidConstants.APPLICATION_ORCID_XML);
		res.addQueryParameter("q", query);
		if (page >= 0)
			res.setQueryValue("rows", Integer.toString(pagesize));
		if (pagesize >= 0)
			res.setQueryValue("start", Integer.toString(page));
		JaxbRepresentation<OrcidMessage> jax = new JaxbRepresentation<OrcidMessage>(res.get(), OrcidMessage.class);
		OrcidMessage m = jax.getObject();
		return m.getOrcidSearchResults();
	}
	
	public OrcidSearchResults search(String query) throws IOException {
		return search(query, -1, -1);
	}

	private OrcidProfile getProfile(String orcid, String profileType) throws IOException, ResourceException {
		if (profileType == null) 
			throw new IllegalArgumentException();
		ClientResource res = new ClientResource(PUBLIC_URI_V11 + "/" + orcid + "/" + profileType);
		res.accept(OrcidConstants.APPLICATION_ORCID_XML);
		JaxbRepresentation<OrcidMessage> jax = new JaxbRepresentation<OrcidMessage>(res.get(), OrcidMessage.class);
		OrcidMessage m = jax.getObject();
		return m.getOrcidProfile();
	}
	
	/** Fetch an ORCID profile
	 * 
	 * @param orcid  the ORCID in plain, non URL format
	 * @return an OrcidProfile, with with OrcidWorks
	 * @throws IOException if result unparsable or network unreachable.
	 * @throws ResourceException if there's a http problem (e.g. 404, 400)
	 */
	public OrcidProfile getOrcidProfile(String orcid) throws ResourceException, IOException{
		return getProfile(orcid,TYPE_ORCID_PROFILE);
	}

	/** Fetch an ORCID Bio
	 * 
	 * @param orcid the ORCID in plain, non URL format
	 * @return an OrcidProfile, without OrcidWorks
	 * @throws IOException if result unparsable or network unreachable.
	 * @throws ResourceException if there's a http problem (e.g. 404, 400)
	 */
	public OrcidProfile getOrcidBio(String orcid) throws ResourceException, IOException{
		return getProfile(orcid,TYPE_ORCID_BIO);
	}

}

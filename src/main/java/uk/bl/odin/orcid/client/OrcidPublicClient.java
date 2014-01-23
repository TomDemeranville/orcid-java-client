package uk.bl.odin.orcid.client;

import java.io.IOException;

import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

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

	public OrcidSearchResults search(String query) throws IOException {
		return search(query, -1, -1);
	}

	public OrcidSearchResults search(String query, int page, int pagesize) throws IOException {
		if (query == null || query.isEmpty())
			throw new IllegalArgumentException();
		ClientResource res = new ClientResource(PUBLIC_URI_V11 + SEARCH_ENDPOINT);
		res.accept(OrcidConstants.APPLICATION_ORCID_XML);
		res.addQueryParameter("q", query);
		if (page >= 0)
			res.setQueryValue("rows", Integer.toString(pagesize));
		if (pagesize >= 0)
			res.setQueryValue("start", Integer.toString(page));// starts from 0
		Representation r = res.get();
		JaxbRepresentation<OrcidMessage> jax = new JaxbRepresentation<OrcidMessage>(r, OrcidMessage.class);
		try {
			OrcidMessage m = jax.getObject();
			return m.getOrcidSearchResults();
		} catch (IOException e) {
			// TODO: handle properly
			throw e;
		}
	}

	private OrcidProfile getProfile(String orcid, String profileType) throws IOException, ResourceException {
		if (profileType == null) {
			profileType = TYPE_ORCID_PROFILE;
		}
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
	 * @throws IOException if unparsable.
	 * @throws ResourceException if there's a http problem (e.g. 404, 400)
	 */
	public OrcidProfile getOrcidProfile(String orcid) throws ResourceException, IOException{
		return getProfile(orcid,TYPE_ORCID_PROFILE);
	}

	/** Fetch an ORCID Bio
	 * 
	 * @param orcid the ORCID in plain, non URL format
	 * @return an OrcidProfile, without OrcidWorks
	 * @throws IOException if unparsable.
	 * @throws ResourceException if there's a http problem (e.g. 404, 400)
	 */
	public OrcidProfile getOrcidBio(String orcid) throws ResourceException, IOException{
		return getProfile(orcid,TYPE_ORCID_BIO);
	}

	/**
	 * Constructs a param suitable for use as a query that matches profiles with
	 * this DOI
	 * 
	 * @param doi
	 *            the doi to match.
	 * @return the query string
	 */
	public static final String buildDOIQuery(String doi) {
		return OrcidConstants.SEARCH_PARAM_DOI + ": \"" + doi + "\"";
	}

}

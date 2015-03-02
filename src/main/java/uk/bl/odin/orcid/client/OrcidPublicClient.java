package uk.bl.odin.orcid.client;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import uk.bl.odin.orcid.client.constants.OrcidConstants;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidMessage;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidProfile;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidSearchResults;

/**
 * Orcid client that fetches profiles and performs searches using the (LIVE)
 * orcid public api.
 */
@SuppressWarnings("restriction")
public class OrcidPublicClient {

	private static final Logger log = Logger.getLogger(OrcidPublicClient.class.getName());

	private static final String PUBLIC_URI_V12 = "http://pub.orcid.org/v1.2";
	private static final String SEARCH_ENDPOINT = "/search/orcid-bio/";

	private static final String TYPE_ORCID_BIO = "orcid-bio";
	private static final String TYPE_ORCID_PROFILE = "orcid-profile";

	private final JAXBContext orcidMessageContext;

	@Inject
	public OrcidPublicClient() throws JAXBException {
		orcidMessageContext = JAXBContext.newInstance(OrcidMessage.class);
	}

	/**
	 * Perform a search against the public ORCID API
	 * 
	 * @param query
	 *            the 'q' GET param to send
	 * @param page
	 *            the 'page' GET param to send, starts from 0 - -1 will ommit
	 *            this paramter and use ORCiD default page (0)
	 * @param pagesize
	 *            the 'page' GET param to send - -1 will ommit this parameter
	 *            and use ORCiD default pagesize (10)
	 * @return an OrcidSearchResults object with 0 or more OrcidSearchResult
	 *         children
	 * @throws IOException
	 *             if result unparsable or network unreachable.
	 * @throws ResourceException
	 *             if there's a http problem (e.g. 404, 400, 500)
	 */
	public OrcidSearchResults search(String query, int page, int pagesize) throws IOException {
		if (query == null || query.isEmpty()) {
			throw new IllegalArgumentException();
		}
		ClientResource res = new ClientResource(PUBLIC_URI_V12 + SEARCH_ENDPOINT);
		res.accept(OrcidConstants.APPLICATION_ORCID_XML);
		res.addQueryParameter("q", query);
		if (pagesize >= 0) {
			res.addQueryParameter("rows", Integer.toString(pagesize));
		}
		if (page >= 0) {
			res.addQueryParameter("start", Integer.toString(page));
		}

		//this will throw any non-2XX http code as a ResourceException
		//note GAE thows internal errors in the 1XXX range!
		StringReader reader = new StringReader(res.get().getText());
		
		try {
			Unmarshaller um = orcidMessageContext.createUnmarshaller();
			OrcidMessage message = (OrcidMessage) um.unmarshal(reader);
			if (message.getOrcidSearchResults() == null) {
				// shouldn't happen but there's a bug ORCiD side.
				OrcidSearchResults r = new OrcidSearchResults();
				r.setNumFound(BigInteger.ZERO);
				return r;
			} else {
				return message.getOrcidSearchResults();
			}
		} catch (JAXBException e) {
			log.info("Problem unmarshalling return value " + e);
			throw new IOException(e);
		}
	}

	public OrcidSearchResults search(String query) throws IOException {
		return search(query, -1, -1);
	}
	
	public OrcidSearchResults search(SearchKey query) throws IOException {
		return search(query.getQuery(), query.getPage(), query.getPagesize());
	}

	private OrcidProfile getProfile(String orcid, String profileType) throws IOException, ResourceException {
		if (profileType == null) {
			throw new IllegalArgumentException();
		}
		ClientResource res = new ClientResource(PUBLIC_URI_V12 + "/" + orcid + "/" + profileType);
		res.accept(OrcidConstants.APPLICATION_ORCID_XML);
		try {
			Unmarshaller um = orcidMessageContext.createUnmarshaller();
			OrcidMessage message = (OrcidMessage) um.unmarshal(res.get().getStream());
			return message.getOrcidProfile();
		} catch (JAXBException e) {
			log.info("Problem unmarshalling return value " + e);
			log.info(res.toString());
			throw new IOException(e);
		}
	}

	/**
	 * Fetch an ORCID profile
	 * 
	 * @param orcid
	 *            the ORCID in plain, non URL format
	 * @return an OrcidProfile, with with OrcidWorks
	 * @throws IOException
	 *             if result unparsable or network unreachable.
	 * @throws ResourceException
	 *             if there's a http problem (e.g. 404, 400)
	 */
	public OrcidProfile getOrcidProfile(String orcid) throws ResourceException, IOException {
		return getProfile(orcid, TYPE_ORCID_PROFILE);
	}

	/**
	 * Fetch an ORCID Bio
	 * 
	 * @param orcid
	 *            the ORCID in plain, non URL format
	 * @return an OrcidProfile, without OrcidWorks
	 * @throws IOException
	 *             if result unparsable or network unreachable.
	 * @throws ResourceException
	 *             if there's a http problem (e.g. 404, 400)
	 */
	public OrcidProfile getOrcidBio(String orcid) throws ResourceException, IOException {
		return getProfile(orcid, TYPE_ORCID_BIO);
	}

}

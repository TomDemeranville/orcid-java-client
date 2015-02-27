package uk.bl.odin.orcid.client;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.engine.header.Header;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

import uk.bl.odin.orcid.client.constants.OrcidAuthScope;
import uk.bl.odin.orcid.client.constants.OrcidConstants;
import uk.bl.odin.orcid.client.constants.OrcidExternalIdentifierType;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidActivities;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidBio;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidMessage;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidProfile;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidWork;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidWorks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

/**
 * General purpose ORCID client that supports simple OAuth scenarios
 * 
 * @see http://support.orcid.org/knowledgebase/articles/116874-orcid-api-guide
 *      Uses message 1.1
 *      https://raw.github.com/ORCID/ORCID-Source/master/orcid-model
 *      /src/main/resources/orcid-message-1.1.xsd
 */
@SuppressWarnings("restriction")
public class OrcidOAuthClient {

	private static final Logger log = Logger.getLogger(OrcidOAuthClient.class.getName());

	private static final String AUTHZ_ENDPOINT = "/oauth/authorize";
	private static final String TOKEN_ENDPOINT = "/oauth/token";
	private static final String WORK_CREATE_ENDPOINT = "/orcid-works";

	private static final String SANDBOX_LOGIN_URI = "https://sandbox.orcid.org";
	private static final String SANDBOX_API_URI_TOKEN = "https://api.sandbox.orcid.org";
	private static final String SANDBOX_API_URI_V1_1 = "http://api.sandbox.orcid.org/v1.1";
	
	private static final String LIVE_LOGIN_URI = "https://orcid.org";
	private static final String LIVE_API_URI_TOKEN = "https://api.orcid.org";
	private static final String LIVE_API_URI_V1_1 = "http://api.orcid.org/v1.1";

	private final String clientID;
	private final String clientSecret;
	private final String redirectUri;

	private final String loginUri;
	private final String apiUriToken;
	private final String apiUriV11;

	private final JAXBContext orcidMessageContext;

	/**
	 * Suitable for injection or manual construction. Thread safe.
	 * 
	 * @param clientID
	 *            OAuth credential
	 * @param clientSecret
	 *            OAuth credential
	 * @param redirectUri
	 *            OAuth credential
	 * @param sandbox
	 *            if true use sandbox endpoints
	 * @throws JAXBException
	 *             if we can't create a JaxB context
	 */
	@Inject
	public OrcidOAuthClient(@Named("OrcidClientID") String clientID, @Named("OrcidClientSecret") String clientSecret,
			@Named("OrcidReturnURI") String redirectUri, @Named("OrcidSandbox") boolean sandbox) throws JAXBException {
		if (clientID == null || clientSecret == null || redirectUri == null)
			throw new IllegalArgumentException("cannot create OrcidOAuthClient - missing init parameter(s)");
		if (sandbox) {
			this.loginUri = SANDBOX_LOGIN_URI;
			this.apiUriToken = SANDBOX_API_URI_TOKEN;
			this.apiUriV11 = SANDBOX_API_URI_V1_1;
		} else {
			this.loginUri = LIVE_LOGIN_URI;
			this.apiUriToken = LIVE_API_URI_TOKEN;
			this.apiUriV11 = LIVE_API_URI_V1_1;
		}
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		orcidMessageContext = JAXBContext.newInstance(OrcidMessage.class);
	}

	/**
	 * Create a URL that can be used to request an accessCode
	 */
	public String getAuthzCodeRequest(String state, OrcidAuthScope scope) {
		return getAuthzCodeRequest(state) + "&scope=" + scope.toString();
	}

	/**
	 * Create a URL that can be used to request an accessCode
	 */
	public String getAuthzCodeRequest(String state, List<OrcidAuthScope> scopes) {
		return getAuthzCodeRequest(state) + "&scope=" + Joiner.on(" ").join(scopes);
	}

	private String getAuthzCodeRequest(String state) {
		String req = loginUri + AUTHZ_ENDPOINT;
		req += "?client_id=" + clientID;
		req += "&response_type=code";
		if (state != null)
			req += "&state=" + state;
		req += "&redirect_uri=" + redirectUri;
		return req;
	}

	/**
	 * Exchange and authorization code for an auth token from ORCID
	 * 
	 * @see http://support.orcid.org/knowledgebase/articles/120107
	 * @see http 
	 *      ://support.orcid.org/knowledgebase/articles/179969-methods-to-generate
	 *      -an-access-token-for-testing
	 * @param authorizationCode
	 * @return the parsed response
	 * @throws IOException
	 *             if result unparsable or network unreachable.
	 * @throws ResourceException
	 *             if there's a http problem (e.g. 404, 400)
	 */
	public OrcidAccessToken getAccessToken(String authorizationCode) throws IOException {
		Reference ref = new Reference(apiUriToken + TOKEN_ENDPOINT);
		if (Context.getCurrent() == null) {
			Context.setCurrent(new Context());
		}
		ClientResource client = new ClientResource(ref);
		Form f = new Form();
		f.add("client_id", clientID);
		f.add("client_secret", clientSecret);
		f.add("grant_type", "authorization_code");
		f.add("code", authorizationCode);
		f.add("redirect_uri", redirectUri);
		client.getContext().getParameters().add("followRedirects", "true");
		log.fine(f.toString());
		log.fine(client.toString());
		Representation rep = client.post(f, MediaType.APPLICATION_JSON);
		String json = rep.getText();
		OrcidAccessToken token = new ObjectMapper().reader(OrcidAccessToken.class).readValue(json);
		return token;
	}
	
	/** Fetch an access token that enables the creation of ORCiD profiles
	 * 
	 * @return
	 * @throws IOException 
	 */
	public OrcidAccessToken getCreateProfileAccessToken() throws IOException{
		Reference ref = new Reference(apiUriToken + TOKEN_ENDPOINT);
		ClientResource client = new ClientResource(ref);
		Form f = new Form();
		f.add("client_id", clientID);
		f.add("client_secret", clientSecret);
		f.add("scope", OrcidAuthScope.CREATE_PROFILE.toString());
		f.add("grant_type", "client_credentials");		
		Representation rep = client.post(f, MediaType.APPLICATION_JSON);
		String json = rep.getText();
		OrcidAccessToken token = new ObjectMapper().reader(OrcidAccessToken.class).readValue(json);
		return token;
	}

	/**
	 * Adds a research activity to the ORCID Record. requires
	 * OrcidAuthScope.CREATE_WORKS scope
	 * 
	 * @see http 
	 *      ://support.orcid.org/knowledgebase/articles/177528-add-works-technical
	 *      -developer
	 * @see http
	 *      ://support.orcid.org/knowledgebase/articles/171893-tutorial-add-
	 *      works -with-curl
	 * @param token
	 *            containing a valid auth token and orcid
	 * @throws IOException
	 *             if result unparsable or network unreachable.
	 * @throws ResourceException
	 *             if there's a http problem (e.g. 404, 400)
	 */
	public void appendWork(OrcidAccessToken token, OrcidWork work) throws ResourceException, IOException {
		Reference ref = new Reference(apiUriV11 + "/" + token.getOrcid() + WORK_CREATE_ENDPOINT);
		ClientResource client = new ClientResource(ref);
		// OAUTH bearer is a pain via restlet ChallengeScheme on GAE
		addRestletHeader(client, "Authorization", "Bearer " + token.getAccess_token());
		log.info(token.getAccess_token());
		try {
			StringWriter sw = new StringWriter();
			orcidMessageContext.createMarshaller().marshal(wrapWork(work), sw);
			log.fine(sw.toString());
			StringRepresentation rep = new StringRepresentation(sw.toString(), OrcidConstants.APPLICATION_ORCID_XML);
			client.post(rep);
		} catch (JAXBException e) {
			log.fine("problem marshalling response "+e.getMessage());
			throw new IOException(e);
		} 
		//TODO: catch the 500 and extract the following! {"message-version":"1.2_rc3","error-desc":{"value":"Invalid authorization code: Me82Dc"}}
	}
	
	
	public void addOrcidProfile(){
		
	}


	/**
	 * Completely replaces all fields of the bio marked as PUBLIC or LIMITED in
	 * the ORCID Record, EXCEPT FOR elements containing lists such as External
	 * identifiers and Affiliations. requires OrcidAuthScope.UPDATE_BIO scope.
	 * 
	 * @param token
	 *            containing a valid auth token and orcid
	 * @param bio
	 */
	public void replaceOrcidBio(OrcidAccessToken token, OrcidBio bio) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Completely replaces all "works" research activities marked as PUBLIC or
	 * LIMITED in the ORCID Record. requires OrcidAuthScope.UPDATE_WORKS scope
	 * 
	 * @param token
	 *            containing a valid auth token and orcid
	 * @param works
	 */
	public void replaceOrcidWorks(OrcidAccessToken token, List<OrcidWork> works) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds an external identifier to the ORCID Record. requires
	 * OrcidAuthScope.CREATE_EXTERNAL_ID scope
	 * 
	 * @param token
	 *            containing a valid auth token and orcid
	 * @param type
	 * @param value
	 */
	public void appendExternalIdentifier(OrcidAccessToken token, OrcidExternalIdentifierType type, String value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates new ORCID iDs and Records and notifies each scholar that the
	 * record has been created. The scholar has 10 days to decline the
	 * invitation before the iD is activated and information in the Record become
	 * accessible (according to the privacy model). The scholar may claim (start
	 * managing) or deactivate the ORCID Record at any time after it has been
	 * created. Handles the request for an OrcidAuthScope.CREATE_PROFILE scope
	 * internally.
	 * 
	 * @param profile
	 */
	public void createOrcidProfile(OrcidProfile profile) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Wrap an OrcidWork inside an otherwise empty OrcidMessage
	 */
	private static final OrcidMessage wrapWork(OrcidWork work) {
		OrcidWorks works = new OrcidWorks();
		works.getOrcidWork().add(work);
		OrcidActivities activities = new OrcidActivities();
		activities.setOrcidWorks(works);
		OrcidProfile profile = new OrcidProfile();
		profile.setOrcidActivities(activities);
		OrcidMessage message = new OrcidMessage();
		message.setOrcidProfile(profile);
		message.setMessageVersion(OrcidConstants.MESSAGE_VERSION);
		return message;
	}

	/**
	 * Adds a HTTP header to a Restlet ClientResource OAUTH bearer is a pain via
	 * restlet on GAE, so we set it ourselves.
	 */
	public static final void addRestletHeader(ClientResource client, String key, String value) {
		@SuppressWarnings("unchecked")
		Series<Header> headers = (Series<Header>) client.getRequestAttributes().get("org.restlet.http.headers");
		if (headers == null) {
			headers = new Series<Header>(Header.class);
			client.getRequestAttributes().put("org.restlet.http.headers", headers);
		}
		headers.add(key, value);
	}

}

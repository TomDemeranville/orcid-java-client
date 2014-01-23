package uk.bl.odin.orcid.client;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.ext.jaxb.JaxbRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

import uk.bl.odin.orcid.schema.messages.onepointone.OrcidActivities;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidMessage;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidProfile;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidWork;
import uk.bl.odin.orcid.schema.messages.onepointone.OrcidWorks;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * General purpose ORCID client that supports simple OAuth scenarios
 */
public class OrcidOAuthClient {

	private static final Logger log = Logger.getLogger(OrcidOAuthClient.class.getName());

	private static final String AUTHZ_ENDPOINT = "/oauth/authorize";
	private static final String TOKEN_ENDPOINT = "/oauth/token";
	private static final String WORK_CREATE_ENDPOINT = "/orcid-works";
	private static final String SCOPE_CREATE_WORKS = "/orcid-works/create";
	private static final String SANDBOX_LOGIN_URI = "https://sandbox-1.orcid.org";
	private static final String SANDBOX_API_URI_TOKEN = "https://api.sandbox-1.orcid.org";
	private static final String SANDBOX_API_URI_V1_1 = "http://api.sandbox-1.orcid.org/v1.1";
	private static final String LIVE_LOGIN_URI = "https://orcid.org";
	private static final String LIVE_API_URI_TOKEN = "https://api.orcid.org";
	private static final String LIVE_API_URI_V1_1 = "http://api.orcid.org/v1.1";

	private final String clientID;
	private final String clientSecret;
	private final String redirectUri;

	private final String loginUri;
	private final String apiUriToken;
	private final String apiUriV11;



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
	 */
	@Inject
	public OrcidOAuthClient(@Named("OrcidClientID") String clientID, @Named("OrcidClientSecret") String clientSecret,
			@Named("OrcidReturnURI") String redirectUri, @Named("OrcidSandbox") boolean sandbox) {
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
	}

	/**
	 * Create a URL that can be used to request an accessCode
	 */
	public String getAuthzCodeRequest(String originalRef) {
		String req = loginUri + AUTHZ_ENDPOINT;
		req += "?client_id=" + clientID;
		req += "&scope=" + SCOPE_CREATE_WORKS;
		req += "&response_type=code";
		if (originalRef != null)
			req += "&state=" + originalRef;
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
	 */
	public OrcidAccessTokenResponse getAccessToken(String authorizationCode) throws ResourceException, IOException {
		Reference ref = new Reference(apiUriToken + TOKEN_ENDPOINT);
		ClientResource client = new ClientResource(ref);
		Form f = new Form();
		f.add("client_id", clientID);
		f.add("client_secret", clientSecret);
		f.add("grant_type", "authorization_code");
		f.add("code", authorizationCode);
		f.add("redirect_uri", redirectUri);
		client.getContext().getParameters().add("followRedirects", "true");

		log.fine(ref.toString());
		log.fine(f.toString());

		Representation rep = client.post(f, MediaType.APPLICATION_JSON);
		String json = rep.getText();
		log.fine(json);
		OrcidAccessTokenResponse token = new ObjectMapper().reader(OrcidAccessTokenResponse.class).readValue(json);
		return token;
	}

	/**
	 * Add a work to a users profile.
	 * 
	 * @see http 
	 *      ://support.orcid.org/knowledgebase/articles/177528-add-works-technical
	 *      -developer
	 * @see http
	 *      ://support.orcid.org/knowledgebase/articles/171893-tutorial-add-
	 *      works -with-curl
	 * @param token
	 *            a valid auth token
	 * @param work
	 *            a valid orcid work
	 * @throws IOException
	 *             if we can't write for any reason.
	 */
	public void appendWork(String orcid, String token, OrcidWork work) throws IOException {
		Reference ref = new Reference(apiUriV11 + "/" + orcid + WORK_CREATE_ENDPOINT);
		ClientResource client = new ClientResource(ref);
		// OAUTH bearer is a pain via restlet ChallengeScheme on GAE, so we set
		// it manually here.
		addRestletHeader(client, "Authorization", "Bearer " + token);

		JaxbRepresentation<OrcidMessage> jax = new JaxbRepresentation<OrcidMessage>(wrapWork(work));
		jax.setFormattedOutput(true);
		// jax.setSchemaLocation("http://www.orcid.org/ns/orcid https://raw.github.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/orcid-message-1.1.xsd");
		jax.setValidatingDtd(true);
		jax.setMediaType(OrcidConstants.APPLICATION_ORCID_XML);

		try {
			client.post(jax);
		} catch (ResourceException e) {
			Response response = client.getResponse();
			Status status = response.getStatus();
			log.fine(client.toString());
			log.fine(status.toString());
			log.fine(response.getEntityAsText());
			throw new IOException("Problem updating ORCID profile " + status.getCode());
		}
	}

	/**
	 * Wrap an OrcidWork inside an otherwise empty OrcidMessage
	 */
	public static final OrcidMessage wrapWork(OrcidWork work) {
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

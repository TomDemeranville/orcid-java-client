import org.junit.Before;
import org.junit.Test;
import uk.bl.odin.orcid.client.OrcidAccessToken;
import uk.bl.odin.orcid.client.OrcidOAuthClient;
import uk.bl.odin.orcid.client.constants.OrcidApiType;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;



/** Most tests migrated to orcid-update-app as it requires server interaction.
 * 
 * Developers should ensure that they copy testoauth.properties.example to testoauth.properties
 * and modify it to contain their credentials.
 *  
 * @author tom
 *
 */

public class OrcidOAuthClientTest {
	private Properties properties = new Properties();

	@Before
	public void before() throws IOException {
		final String filename = "testoauth.properties";
		final InputStream inputStream = getClass().getResourceAsStream(filename);

		if (inputStream == null) {
			throw new IOException("Unable to find properties file src/test/resources/" + filename);
		}

		properties.load(inputStream);
	}
	
	@Test
	public final void testCreateProfile() throws IOException, JAXBException {
		OrcidOAuthClient client = new OrcidOAuthClient(properties.getProperty("orcidClientID"),
				properties.getProperty("orcidClientSecret"), properties.getProperty("orcidReturnUri"),
				(Boolean.valueOf(properties.getProperty("orcidSandbox")) ? OrcidApiType.SANDBOX : OrcidApiType.LIVE));
		OrcidAccessToken tok = client.getCreateProfileAccessToken();
		assertNotNull(tok.getAccess_token());
	}
	
}

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import uk.bl.odin.orcid.client.OrcidPublicClient;
import uk.bl.odin.orcid.client.constants.OrcidSearchField;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidId;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidProfile;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidSearchResults;


public class OrcidPublicClientTest {

	@Test
	public final void testFetchProfile() throws IOException, JAXBException {
		OrcidPublicClient client = new OrcidPublicClient();
		
		//No works.
		OrcidProfile bio = client.getOrcidBio("0000-0002-9151-6445");
		assertEquals("Unit",bio.getOrcidBio().getPersonalDetails().getGivenNames());
		assertEquals("Test",bio.getOrcidBio().getPersonalDetails().getFamilyName());
		assertNull(bio.getOrcidActivities());
		assertEquals(getPathFromOrcidId(bio.getOrcidIdentifier()),"0000-0002-9151-6445");
	
		//with works
		OrcidProfile pro = client.getOrcidProfile("0000-0002-9151-6445");
		assertEquals("Unit",pro.getOrcidBio().getPersonalDetails().getGivenNames());
		assertEquals("Test",pro.getOrcidBio().getPersonalDetails().getFamilyName());
		assertEquals(pro.getOrcidActivities().getOrcidWorks().getOrcidWork().size(),2);
		assertEquals(getPathFromOrcidId(pro.getOrcidIdentifier()),"0000-0002-9151-6445");
		
	}
	
	@Test
	public final void testSearchForDOI() throws IOException, JAXBException{	
		//THIS IS RETURNING INVALID MESSAGES - they're 1.0.23 (have an <orcid> element) despite being labeled as 1.1
		//I've hacked a fix into the JAXB classes.
		OrcidPublicClient client = new OrcidPublicClient();
		String query = OrcidSearchField.DIGITAL_OBJECT_IDS.buildExactQuery("10.9997/abc123");
		assertEquals("digital-object-ids: \"10.9997/abc123\"",query);
		OrcidSearchResults results = client.search(query);
		assertEquals(1,results.getNumFound().intValue());
		assertEquals(getPathFromOrcidId(results.getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier()),
				"0000-0002-9151-6445");
	}
	
	@Test
	public final void testSearchForDOIPrefix() throws IOException, JAXBException{	
		OrcidPublicClient client = new OrcidPublicClient();
		String query = OrcidSearchField.DIGITAL_OBJECT_IDS.buildPrefixQuery("10.9998");
		assertEquals("digital-object-ids: 10.9998*",query);
		OrcidSearchResults results = client.search(query);
		assertEquals(1,results.getNumFound().intValue());
		assertEquals(getPathFromOrcidId(results.getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier()),
				"0000-0002-9151-6445");
	}
	
	@Test
	public final void testEmptySearch() throws IOException, JAXBException{	
		OrcidPublicClient client = new OrcidPublicClient();
		String query = OrcidSearchField.DIGITAL_OBJECT_IDS.buildPrefixQuery("10.9998DGSJHAJHLDSAG");
		assertEquals("digital-object-ids: 10.9998DGSJHAJHLDSAG*",query);
		OrcidSearchResults results = client.search(query);
		assertEquals(0,results.getNumFound().intValue());
	}

	private String getPathFromOrcidId(OrcidId orcidId) {
		for (JAXBElement<String> element : orcidId.getContent()) {
			if (element.getName().getLocalPart().equals("path")) {
				return element.getValue();
			}
		}

		return "";
	}
	
}

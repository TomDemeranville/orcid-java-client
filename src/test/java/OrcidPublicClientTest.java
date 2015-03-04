import org.junit.Test;
import uk.bl.odin.orcid.client.OrcidPublicClient;
import uk.bl.odin.orcid.client.constants.OrcidSearchField;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidId;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidProfile;
import uk.bl.odin.orcid.schema.messages.onepointtwo.OrcidSearchResults;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.*;


public class OrcidPublicClientTest {

	@Test
	public final void testFetchProfile() throws IOException, JAXBException {
		OrcidPublicClient client = new OrcidPublicClient();
		
		//No works.
		OrcidProfile bio = client.getOrcidBio("0000-0002-0661-7998");
		assertEquals("Stephan",bio.getOrcidBio().getPersonalDetails().getGivenNames());
		assertEquals("Windmüller",bio.getOrcidBio().getPersonalDetails().getFamilyName());
		assertNull(bio.getOrcidActivities());
		assertEquals(getPathFromOrcidId(bio.getOrcidIdentifier()),"0000-0002-0661-7998");
	
		//with works
		OrcidProfile pro = client.getOrcidProfile("0000-0002-0661-7998");
		assertEquals("Stephan",pro.getOrcidBio().getPersonalDetails().getGivenNames());
		assertEquals("Windmüller",pro.getOrcidBio().getPersonalDetails().getFamilyName());
		assertTrue(pro.getOrcidActivities().getOrcidWorks().getOrcidWork().size() > 3);
		assertEquals(getPathFromOrcidId(pro.getOrcidIdentifier()),"0000-0002-0661-7998");
		
	}

	@Test
	public final void testCombinedSearch() throws IOException, JAXBException {
		OrcidPublicClient client = new OrcidPublicClient();
		String query = OrcidSearchField.GIVEN_NAMES.buildExactQuery("Stephan");
		query += " AND ";
		query += OrcidSearchField.FAMILY_NAME.buildExactQuery("Windmüller");

		assertEquals("given-names: \"Stephan\" AND family-name: \"Windmüller\"", query);
		OrcidSearchResults results = client.search(query);
		assertTrue(results.getNumFound().intValue() > 0);
	}

	@Test
	public final void testSearchForDOI() throws IOException, JAXBException{	
		OrcidPublicClient client = new OrcidPublicClient();
		String query = OrcidSearchField.DIGITAL_OBJECT_IDS.buildExactQuery("10.1007/s10009-014-0321-6");
		assertEquals("digital-object-ids: \"10.1007/s10009-014-0321-6\"",query);
		OrcidSearchResults results = client.search(query);
		assertEquals(1,results.getNumFound().intValue());
		assertEquals(getPathFromOrcidId(results.getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier()),
				"0000-0002-0661-7998");
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
		assertEquals(0, results.getNumFound().intValue());
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

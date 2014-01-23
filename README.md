#ORCiD Java Client

Simple, easy to use ORCiD client written in Java.  Supports the pbulic and Tier2 API.

#Public API Examples:

`OrcidPublicClient client = new OrcidPublicClient();
//Fetch a profile
OrcidProfile pro = client.getOrcidProfile("0000-0002-9151-6445");

//Search for profile with a DOI attached
String query = OrcidPublicClient.buildDOIQuery("10.6084/m9.figshare.909352");
OrcidSearchResults results = client.search(query);`

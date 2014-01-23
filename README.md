#ORCiD Java Client 

(for ORCiD message Schema v1.1)

Simple, easy to use ORCiD client written in Java.  Supports the public and Tier2 API.

Natural object mapping - The entire ORCiD message schema is represented as a hirearchical graph of POJOs.

#Public API Examples:

`OrcidPublicClient client = new OrcidPublicClient();
//Fetch a profile
OrcidProfile pro = client.getOrcidProfile("0000-0002-9151-6445");

//Search for profile with a DOI attached
String query = OrcidPublicClient.buildDOIQuery("10.6084/m9.figshare.909352");
OrcidSearchResults results = client.search(query);`

#ORCiD Java Client 

Simple, easy to use ORCiD client written in Java.  Supports the public and Tier2 API.  Maven support.

Natural object mapping - The entire ORCiD message schema is represented as a hirearchical graph of POJOs.

Early stage development - note API may break.  Pull requests welcome.

#Public API Examples:

	OrcidPublicClient client = new OrcidPublicClient();

	//Fetch a profile
	OrcidProfile pro = client.getOrcidProfile("0000-0002-9151-6445");

	//Search for profile with a DOI attached
	String query = OrcidPublicClient.buildDOIQuery("10.6084/m9.figshare.909352");
	OrcidSearchResults results = client.search(query);

#Maven
Add the repository to your pom.xml like so:

	<repositories>
		<repository>
	        <id>orcid-java-client-mvn-repo</id>
	        <url>http://raw.github.com/TomDemeranville/orcid-java-client/mvn-repo/</url>
	        <snapshots>
	            <enabled>true</enabled>
	            <updatePolicy>always</updatePolicy>
	        </snapshots>
	    </repository>
    </repositories>

Add the dependency like this:

	<dependency>
		<groupId>uk.bl</groupId>
		<artifactId>orcid-java-client-gae</artifactId>
		<version>0.11.0</version>
	</dependency>

You can see the list of versions here: (https://github.com/TomDemeranville/orcid-java-client/tree/mvn-repo/uk/bl/orcid-java-client-gae)

#Schema support

The version supports the ORCiD message schema 1.1
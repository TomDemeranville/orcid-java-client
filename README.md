#ORCiD Java Client

Simple, easy to use ORCiD client written in Java.  Supports the public and Tier2 API with OAuth. Natural object
mapping - The entire ORCiD message schema is represented as a hirearchical graph of POJOs with JAXB support for
serialisation.  Maven support, GAE support.  Annotated to support javax.inject depdendency injection.

Early stage development - note API may break.  Pull requests welcome.

See also: [Orcid Profile Updater](https://github.com/TomDemeranville/orcid-update-java)

##Public API Examples:

	//create a client
	OrcidPublicClient client = new OrcidPublicClient();

	//Fetch a profile
	OrcidProfile pro = client.getOrcidProfile("0000-0002-9151-6445");

	//Search for profile with a DOI attached
	OrcidSearchResults results = client.search(OrcidSearchField.DIGITAL_OBJECT_IDS.buildExactQuery("10.9997/abc123"));
	//Search for all profiles for an ISBN
	OrcidSearchResults results = client.search(OrcidSearchField.ISBN.buildExactQuery("10.9997/"));

	//Search for all profiles with a given DOI prefix attached
	OrcidSearchResults results = client.search(OrcidSearchField.DIGITAL_OBJECT_IDS.buildPrefixQuery("10.9997/"));


##Private API examples

	//get an auth token
	OrcidOAuthClient client = new OrcidOAuthClient("OrcidClientID","OrcidClientSecret",("OrcidReturnURI"),OrcidApiType.LIVE);
	OrcidAccessTokenResponse token = client.getAccessToken(authCode);

	//create a work
	OrcidWork work = new OrcidWork();
	WorkTitle title = new WorkTitle();
	title.setTitle("Test Title");
	work.setWorkTitle(title);

	//append it to the users profile
	client.appendWork(token, work);

#Maven
Currently there is no Maven repository available for downloading orcid-java-client,
but we are working on it. Until then, please build your own packages using Maven.

When using the client in your own projects, you need to add the RESTlet dependencies. These need to be defiend by your
own application as RESTlet comes in multiple versions and your application must decide which to use.
Normal containers like jetty or tomcat use the "jee" edition and that is the most common scenario. Google app engine
uses the "gae" edition. There are other editions are available for jse, android.  Mine is set up like so:

	<dependency>
		<groupId>org.restlet.${restlet.edition}</groupId>
		<artifactId>org.restlet</artifactId>
		<version>2.2-M6</version>
	</dependency>

	<dependency>
	   <groupId>org.restlet.${restlet.edition}</groupId>
	   <artifactId>org.restlet.ext.net</artifactId>
		<version>2.2-M6</version>
	</dependency>

#Schema support

The version supports the ORCiD message schema 1.2

#Contact

There are people actively using this library that can be found on the orcid-api-users@googlegroups.com mailing list

[@tomdemeranville on twitter](https://twitter.com/tomdemeranville)

[My blog](http://demeranville.com)
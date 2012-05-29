Saplo4java
=======

This is an official Java client for the Saplo-API.

Quickstart
----------

    // Connect to the Saplo-API
    SaploClient client = new SaploClient.Builder("API_KEY","SECRET_KEY").build();

    // Connect to the Saplo-API using a proxy
    ClientProxy proxy = new ClientProxy("host", 8080, "username", "password");
    SaploClient client = new SaploClient.Builder("API_KEY","SECRET_KEY").proxy(proxy).build();
    
    // Create a manager to work with Collections
    SaploCollectionManager collectionMgr = new SaploCollectionManager(client);

    // Create a new collection and store it in the API
    SaploCollection myCollection = new SaploCollection("My Collection Name", Language.en);
    collectionMgr.create(myCollection);
    
    // After a collection is successfully created, it is populated with an ID 
    int collectionId = myCollection.getId();
    
    // Create a manager to handle Text
    SaploTextManager textMgr = new SaploTextManager(client);
	
    // Create and save new Text
    SaploText myText = new SaploText(myCollection, "Body of My Text, but more meaningful");
    textMgr.create(myText);
	
    // After a text is successfully created, it is populated with an ID
    int textId = myText.getId();
	    
    // Extract Tags from your text (make sure you have already saved the text into the API)
    List<SaploTag> myTags = textMgr.tags(myText);
    // or alternatively
    List<SaploTag> myTags = textMgr.tags(collectionId, textId);
    
    // Print out the tags extracted
    for(SaploTag tag : myTags) {
    	System.out.println("Category: \"" + tag.getCategory() + "\"" + "\tTag: \"" + tag.getTagWord() + "\"");
    }
    
    // Shut down the client
    client.shutdown();
        
For the rest of the API methods and examples, refer to http://developer.saplo.com/
    
    
Building from source
------------------

Create jar file:

    mvn package


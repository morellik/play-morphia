package it.unifi.cerm.playmorphia;

import com.typesafe.config.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import javax.inject.Inject;

/**
 * Created by morelli on 21/02/19.
 */
public class MongoClientFactory {

    protected Config config;
    protected boolean isTest;

    public MongoClientFactory(Config config) {
        this.config = config;
    }

    @Inject
    protected MongoClientFactory(Config config, boolean isTest) {
        this.config = config;
        this.isTest = isTest;
    }

    /**
     * Creates and returns a new instance of a MongoClient.
     *
     * @return a new MongoClient
     * @throws Exception
     */
    public MongoClient createClient() throws Exception {
        MongoClientURI uri = getClientURI();
        MongoClient mongo = new MongoClient(uri);

        return mongo;
    }


    /**
     * Returns the database name associated with the current configuration.
     *
     * @return The database name
     */
    public String getDBName() {
        return getClientURI().getDatabase();
    }

    protected MongoClientURI getClientURI() {
        MongoClientURI uri = new MongoClientURI(
                isTest
                        ? config.getString("playmorphia.test-uri")
                        : config.getString("playmorphia.uri"));
        return uri;
    }

    /**
     * Returns the models folder name associated with the current configuration.
     *
     * @return The models folder name
     */
    public String getModels() {
        return config.getString("playmorphia.models");
    }

}
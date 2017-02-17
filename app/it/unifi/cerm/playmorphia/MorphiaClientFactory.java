package it.unifi.cerm.playmorphia;

import play.Configuration;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Created by morelli on 12/21/16.
 */
public class MorphiaClientFactory {

    protected Configuration config;
    protected boolean isTest;

    public MorphiaClientFactory(Configuration config) {
        this.config = config;
    }

    protected MorphiaClientFactory(Configuration config, boolean isTest) {
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
        DB db = new DB(mongo, uri.getDatabase());

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
                    ? config.getString("playmorphia.test-uri", "mongodb://127.0.0.1:27017/test")
                    : config.getString("playmorphia.uri", "mongodb://127.0.0.1:27017/play"));
        return uri;
    }

    /**
     * Returns the models folder name associated with the current configuration.
     *
     * @return The models folder name
     */
    public String getModels() {
        return config.getString("playmorphia.models", "models");
    }

}

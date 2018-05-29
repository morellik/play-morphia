package it.unifi.cerm.playmorphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.typesafe.config.Config;

import java.util.Optional;

/**
 * Created by morelli on 12/21/16.
 */
public class MongoClientFactory {

    private Config config;
    private boolean isTest;

    public MongoClientFactory(Config config) {
        this.config = config;
    }

    public MongoClientFactory(Config config, boolean isTest) {
        this.config = config;
        this.isTest = isTest;
    }

    /**
     * Creates and returns a new instance of a MongoClient.
     *
     * @return a new MongoClient
     */
    public MongoClient createClient() {
        MongoClientURI uri = getClientURI();
        return new MongoClient(uri);
    }

    /**
     * Returns the database name associated with the current configuration.
     *
     * @return The database name
     */
    public String getDBName() {
        return getClientURI().getDatabase();
    }

    private MongoClientURI getClientURI() {
        String uri;
        if (isTest) {
            uri = Optional
                    .ofNullable(config.getString("playmorphia.test-uri"))
                    .orElse("mongodb://127.0.0.1:27017/test");
        } else {
            uri = Optional
                    .ofNullable(config.getString("playmorphia.uri"))
                    .orElse("mongodb://127.0.0.1:27017/play");
        }
        return new MongoClientURI(uri);
    }

    /**
     * Returns the models folder name associated with the current configuration.
     *
     * @return The models folder name
     */
    String getModels() {
        return Optional
                .ofNullable(config.getString("playmorphia.models"))
                .orElse("models");
    }

}

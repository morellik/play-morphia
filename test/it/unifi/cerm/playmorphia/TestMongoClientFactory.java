package it.unifi.cerm.playmorphia;

import play.Configuration;

public class TestMongoClientFactory extends MongoClientFactory {

    public TestMongoClientFactory(Configuration config) {
        super(config);
    }

    public TestMongoClientFactory(Configuration config, boolean isTest) {
        super(config, isTest);
    }

    @Override
    public String getDBName() {
        return "testMongoClientFactory";
    }
}
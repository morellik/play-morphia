package it.unifi.cerm.playmorphia;

import play.Configuration;

public class TestMorphiaClientFactory extends MorphiaClientFactory {

    public TestMorphiaClientFactory(Configuration config) {
        super(config);
    }

    public TestMorphiaClientFactory(Configuration config, boolean isTest) {
        super(config, isTest);
    }

    @Override
    public String getDBName() {
        return "testMongoClientFactory";
    }
}
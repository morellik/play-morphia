package it.unifi.cerm.playmorphia;


import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import play.Configuration;
import play.Logger;
import play.Play;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;

/**
 * Created by morelli on 12/20/16.
 */
@Singleton
public class PlayMorphia {

    private static volatile PlayMorphia INSTANCE = null;

    MongoClient mongo = null;
    Datastore datastore = null;
    Morphia morphia = null;

    @Inject
    public PlayMorphia(ApplicationLifecycle lifecycle) {
        PlayMorphia.forceNewInstance();
        lifecycle.addStopHook(()->{
            if (!Play.isTest()) {
                PlayMorphia.mongo().close();
            }
            return F.Promise.pure(null);
        });
    }

    PlayMorphia(Configuration config, ClassLoader classLoader, boolean isTestMode) throws Exception {

        String clientFactoryName = config.getString("playmorphia.mongoClientFactory");
        MongoClientFactory factory = getMongoClientFactory(clientFactoryName, config, isTestMode);
        mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of "+ factory.getClass().getName());
        }

        morphia = new Morphia();

        // Tell Morphia where to find our models
        morphia.mapPackage(factory.getModels());

        /*
        MongoClient mongoClient = new MongoClient(
                ConfigFactory.load().getString("mongodb.host"),
                ConfigFactory.load().getInt("mongodb.port"));

        datastore = morphia.createDatastore(
                mongoClient, ConfigFactory.load().getString("mongodb.database"));
        */

        datastore = morphia.createDatastore(
                mongo, factory.getDBName());

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected MongoClientFactory getMongoClientFactory(String className, Configuration config, boolean isTestMode) throws Exception {

        if (className != null) {
            try {
                Class factoryClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                if (!MongoClientFactory.class.isAssignableFrom(factoryClass)) {
                    throw new IllegalStateException("mongoClientFactory '" + className +
                            "' is not of type " + MongoClientFactory.class.getName());
                }

                Constructor constructor = null;
                try {
                    constructor = factoryClass.getConstructor(Configuration.class);
                } catch (Exception e) {
                    // can't use that one
                }
                if (constructor == null) {
                    return (MongoClientFactory) factoryClass.newInstance();
                }
                return (MongoClientFactory) constructor.newInstance(config);
            } catch (ClassNotFoundException e) {
                throw e;
            }
        }
        return new MongoClientFactory(config, isTestMode);
    }

    public static PlayMorphia getInstance() {
        if (INSTANCE == null) {
            synchronized (PlayMorphia.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new PlayMorphia(Play.application().configuration(), Play.application().classloader(), Play.isTest());
                    } catch (Exception e) {
                        Logger.error(e.getClass().getSimpleName(), e);
                    }
                }
            }
        }
        return INSTANCE;
    }

    public static void forceNewInstance() {
        INSTANCE = null;
        getInstance();
    }

    public static Mongo mongo() {
        return getInstance().mongo;
    }
    public static Datastore datastore() { return getInstance().datastore; }
    public static Morphia morphia() { return getInstance().morphia; }
}

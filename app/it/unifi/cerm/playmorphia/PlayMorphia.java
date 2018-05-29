package it.unifi.cerm.playmorphia;


import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import play.*;
import play.inject.ApplicationLifecycle;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;

/**
 * Created by morelli on 12/20/16.
 */
@Singleton
public class PlayMorphia {

    MongoClient mongo = null;
    Datastore datastore = null;
    Morphia morphia = null;

    @Inject
    public PlayMorphia(ApplicationLifecycle lifecycle, Environment env, Configuration config) {
        try {
            configure(config, env.classLoader(), env.isTest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        lifecycle.addStopHook(()->{
            if (env.isTest()) {
                mongo().close();
            }
            return CompletableFuture.completedFuture(null);
        });
    }


    PlayMorphia(Configuration config, ClassLoader classLoader, boolean isTestMode) throws Exception {
        configure(config,classLoader,isTestMode);
    }


    private void configure(Configuration config, ClassLoader classLoader, boolean isTestMode) throws Exception {

        String clientFactoryName = config.getString("playmorphia.mongoClientFactory");
        MongoClientFactory factory = getMongoClientFactory(clientFactoryName, config, isTestMode);
        mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of "+ factory.getClass().getName());
        }

        morphia = new Morphia();

        // Tell Morphia where to find our models
        morphia.mapPackage(factory.getModels());

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


    public Mongo mongo() {
        return mongo;
    }
    public Datastore datastore() { return datastore; }
    public Morphia morphia() { return morphia; }
}

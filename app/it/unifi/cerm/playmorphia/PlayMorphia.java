package it.unifi.cerm.playmorphia;


import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import play.Environment;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by morelli on 12/20/16.
 */
@Singleton
public class PlayMorphia {

    private MongoClient mongo;
    private Datastore datastore;
    private Morphia morphia;

    @Inject
    public PlayMorphia(ApplicationLifecycle lifecycle, Environment env, Config config) {
        try {
            configure(config, env.isTest());
        } catch (ClassNotFoundException |
                NoSuchMethodException |
                InvocationTargetException |
                IllegalAccessException |
                InstantiationException e) {
            e.printStackTrace();
        }

        lifecycle.addStopHook(() -> {
            if (env.isTest()) {
                Optional.ofNullable(mongo()).ifPresent(Mongo::close);
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    private void configure(Config config, boolean isTestMode)
            throws
            IllegalStateException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {

        String clientFactoryName = config.getString("playmorphia.mongoClientFactory");
        MongoClientFactory factory = getMongoClientFactory(clientFactoryName, config, isTestMode);
        mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of " + factory.getClass().getName());
        }

        morphia = new Morphia().mapPackage(factory.getModels()); // Tell Morphia where to find our models

        datastore = morphia.createDatastore(mongo, factory.getDBName());
    }

    private MongoClientFactory getMongoClientFactory(String className, Config config, boolean isTestMode)
            throws
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InstantiationException,
            InvocationTargetException {

        if (className != null) {
            Class<?> factoryClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());

            if (!MongoClientFactory.class.isAssignableFrom(factoryClass)) {
                throw new IllegalStateException("mongoClientFactory '" + className +
                        "' is not of type " + MongoClientFactory.class.getName());
            }

            Constructor constructor = factoryClass.getConstructor(Config.class);

            if (constructor == null) {
                return (MongoClientFactory) factoryClass.newInstance();
            }

            return (MongoClientFactory) constructor.newInstance(config);
        }

        return new MongoClientFactory(config, isTestMode);
    }

    public Mongo mongo() {
        return mongo;
    }

    public Datastore datastore() {
        return datastore;
    }

    public Morphia morphia() {
        return morphia;
    }
}

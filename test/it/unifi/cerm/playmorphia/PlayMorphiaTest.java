package it.unifi.cerm.playmorphia;

import static it.unifi.cerm.playmorphia.PlayMorphiaTest.MapBuilder.mapBuilder;
import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.Configuration;

import com.mongodb.ServerAddress;
import com.typesafe.config.ConfigFactory;
/**
 * Created by morelli on 12/21/16.
 */
public class PlayMorphiaTest {

    @Test
    public void testMongoUriConfig() throws Exception {
        Map<String, String> config = mapBuilder("playmorphia.uri", "mongodb://localhost:27017/foo").get();
        final PlayMorphia cut = playJongo(config, false);

        assertMongoProperties(cut, "localhost", 27017, "foo");
    }

    @Test
    public void testMongoTestUriConfig() throws Exception {
        Map<String, String> config = mapBuilder("playmorphia.test-uri", "mongodb://localhost:27017/bar").get();
        final PlayMorphia cut = playJongo(config, true);

        assertMongoProperties(cut, "localhost", 27017, "bar");
    }

    @Test
    public void testMongoClientFactory() throws Exception {
        Map<String, String> config = mapBuilder("playmorphia.test-uri", "mongodb://example.com:27018/bar")
                .with("playmorphia.mongoClientFactory", TestMorphiaClientFactory.class.getName()).get();
        final PlayMorphia cut = playJongo(config, false);
        // TestMorphiaClientFactory overrides getDBName, so using this to test we constructed with our
        // specified factory class
        assertThat(cut.datastore.getDB().getName()).isEqualTo("testMongoClientFactory");
    }

    private void assertMongoProperties(final PlayMorphia cut, String host, int port, String dbName) {
        assertThat(cut.mongo.getServerAddressList().size()).isEqualTo(1);
        final ServerAddress server0 = cut.mongo.getServerAddressList().get(0);
        assertThat(server0.getHost()).isEqualTo(host);
        assertThat(server0.getPort()).isEqualTo(port);
        assertThat(cut.datastore.getDB().getName()).isEqualTo(dbName);
    }

    private static PlayMorphia playJongo(Map<String, ? extends Object> config, boolean isTest) throws Exception {
        return new PlayMorphia(new Configuration(ConfigFactory.parseMap(config)), classLoader(), isTest);
    }

    private static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    static class MapBuilder<K, V> {
        private final Map<K, V> m = new HashMap<K, V>();
        public MapBuilder(K key, V value) {
            m.put(key, value);
        }
        public static <K, V> MapBuilder<K, V> mapBuilder(K key, V value) {
            return new MapBuilder<K, V>(key, value);
        }
        public MapBuilder<K, V> with(K key, V value) {
            m.put(key, value);
            return this;
        }
        public Map<K, V> get() {
            return m;
        }
    }
}

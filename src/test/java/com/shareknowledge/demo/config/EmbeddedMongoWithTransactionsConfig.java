package com.shareknowledge.demo.config;

import com.mongodb.BasicDBList;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongoCmdOptions;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.Document;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/*@Reference :
* https://apisimulator.io/spring-boot-auto-configuration-embedded-mongodb-transactions/*/
/**
 * Class for auto-configuring and starting an embedded MongoDB with support for transactions.
 * As there's some overhead in using it and slower startup time, use it only if support for
 * transactions is needed.
 */
@Profile("test")
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({MongoAutoConfiguration.class})
@ConditionalOnClass({MongoClient.class, MongodStarter.class})
@Import({
        EmbeddedMongoAutoConfiguration.class,
        EmbeddedMongoWithTransactionsConfig.DependenciesConfiguration.class
})
public class EmbeddedMongoWithTransactionsConfig {

    // You may get a warning in the log upon shutdown like this:
    // "...Destroy method 'stop' on bean with name 'embeddedMongoServer' threw an
    // exception: java.lang.IllegalStateException: Couldn't kill mongod process!..."
    // That seems harmless as the MongoD process shuts down and frees up the port.
    // There are multiple related issues logged on GitHub:
    // https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo/issues?q=is%3Aissue+Couldn%27t+kill+mongod+process%21

    public static final int DFLT_PORT_NUMBER = 27014;
    public static final String DFLT_REPLICASET_NAME = "rs0";
    public static final int DFLT_STOP_TIMEOUT_MILLIS = 200;

    private Version.Main mFeatureAwareVersion = Version.Main.V4_0;
    private int mPortNumber = DFLT_PORT_NUMBER;
    private String mReplicaSetName = DFLT_REPLICASET_NAME;
    private long mStopTimeoutMillis = DFLT_STOP_TIMEOUT_MILLIS;

    @Bean
    public MongodConfig mongodConfig() throws UnknownHostException, IOException {

        Map<String, String> args = new HashMap<>();
        args.put("--replSet", mReplicaSetName);

        MongodConfig mongodConfig = MongodConfig.builder().version(mFeatureAwareVersion)
                .args(args)
                .stopTimeoutInMillis(mStopTimeoutMillis)
                .cmdOptions(MongoCmdOptions.builder().useNoJournal(false).build())
                .net(new Net(mPortNumber, Network.localhostIsIPv6())).build();
        return mongodConfig;
    }

    /**
     * Initializes a new replica set.
     * Based on code from https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo/issues/257
     */
    class EmbeddedMongoReplicaSetInitialization {

        EmbeddedMongoReplicaSetInitialization() throws Exception {
            MongoClient mongoClient = null;
            try {
                final BasicDBList members = new BasicDBList();
                members.add(new Document("_id", 0).append("host", "localhost:" + mPortNumber));

                final Document replSetConfig = new Document("_id", mReplicaSetName);
                replSetConfig.put("members", members);

                //String connectionUrl = "mongodb://localhost:" + mPortNumber + "/?replicaSet="+mReplicaSetName;
                String connectionUrl = "mongodb://localhost:" + mPortNumber;
                mongoClient = MongoClients.create(connectionUrl);
                final MongoDatabase adminDatabase = mongoClient.getDatabase("admin");
                adminDatabase.runCommand(new Document("replSetInitiate", replSetConfig));

            } finally {
                if (mongoClient != null) {
                    //mongoClient.close();
                }
            }
        }
    }

    @Bean
    EmbeddedMongoReplicaSetInitialization embeddedMongoReplicaSetInitialization() throws Exception {
        return new EmbeddedMongoReplicaSetInitialization();
    }

    /**
     * Additional configuration to ensure that the replica set initialization happens after the
     * {@link MongodExecutable} bean is created. That's it - after the database is started.
     */
    @ConditionalOnClass({MongoClient.class, MongodStarter.class})
    protected static class DependenciesConfiguration
            extends AbstractDependsOnBeanFactoryPostProcessor {

        DependenciesConfiguration() {
            super(EmbeddedMongoReplicaSetInitialization.class, null, MongodExecutable.class);
        }
    }

}
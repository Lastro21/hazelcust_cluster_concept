package ru.sbrf.hazelcastcluster;

import com.hazelcast.client.Client;
import com.hazelcast.client.ClientListener;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

@Component
public class HazelcastComponent {

    private static final Logger LOGGER = Logger.getLogger(HazelcastComponent.class.getName());

    @PostConstruct
    private void postConstruct() {

        final Config config = new Config();
        config.setClusterName("MessageService");
        final NetworkConfig network = config.getNetworkConfig();
        network.setPort(5701).setPortCount(3);
        network.setPortAutoIncrement(true);
        final JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().addMember("192.168.0.113").addMember("192.168.0.175").setEnabled(true);
        final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        hazelcastInstance.getClientService().addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
                LOGGER.info(client.getClientType() + ", " + client.getUuid() + " is connected");
            }

            @Override
            public void clientDisconnected(Client client) {
                LOGGER.info(client.getClientType() + ", " + client.getUuid() + " is disconnected");
            }
        });
    }
}

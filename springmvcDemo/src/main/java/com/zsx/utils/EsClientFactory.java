package com.zsx.utils;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

//import org.elasticsearch.common.settings.ImmutableSettings;

/**
 * Created by QDHL on 2017/6/6.
 */
public class EsClientFactory {
    private static String esHostMaster = null;
    private static Integer esPortMaster;
    private static String esHostSlave;
    private static Integer esPortSlave;
    private static String clusterName;


    private static void initParams() {
        if (StringUtils.isBlank(esHostMaster)) {
            esHostMaster = ConfigUtil.getAttribute("esHostMaster");
            esPortMaster = Integer.parseInt(ConfigUtil.getAttribute("esPortMaster"));
            esHostSlave = ConfigUtil.getAttribute("esHostSlave");
            esPortSlave = Integer.parseInt(ConfigUtil.getAttribute("esPortSlave"));
            clusterName = ConfigUtil.getAttribute("clusterName");
        }
    }

    public static Client getClient() throws Exception {
        initParams();
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true)
                .build();
//        Settings settings = Settings.builder()
//                .put("cluster.name", clusterName).build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(esHostMaster, esPortMaster))
                .addTransportAddress(new InetSocketTransportAddress(esHostSlave, esPortSlave));
//        Client client = new PreBuiltTransportClient(settings)
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHostMaster), esPortMaster))
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHostSlave), esPortSlave));
        return client;
    }

    public static void closeClient(Client esClient) throws Exception {
        if (esClient != null) {
            esClient.close();
        }
    }

}

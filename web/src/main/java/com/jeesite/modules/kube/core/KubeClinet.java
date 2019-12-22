package com.jeesite.modules.kube.core;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Value;

public class KubeClinet {

    @Value("${kube_master_url}")
    public  String KUBE_MASTER_URL;

    @Value("${kube_master_url}")
    public  String KUBE_MASTER_URL1;

    public  KubernetesClient getDockerClient(){
        KubernetesClient client = null;
        if (client == null){
            synchronized (KubeClinet.class){
                Config config = new ConfigBuilder().withMasterUrl(KUBE_MASTER_URL).build();
                 client = new DefaultKubernetesClient(config);
            }
        }
        return client;
    }
}

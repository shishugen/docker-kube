package com.jeesite.modules.kube.core;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @ClassName Kubeclinet
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/16 16:29
 */
public class Kubeclinet {
    private static KubernetesClient client = null;
    public static KubernetesClient getKubeclinet(){
        if (client == null){
            synchronized (Kubeclinet.class){
                if (client == null){
                    Config config = new ConfigBuilder()
                            .withMasterUrl("https://192.168.103.234:6443")
                            .build();
                     client = new DefaultKubernetesClient(config);
                }
            }
        }
        return client;
    }
}

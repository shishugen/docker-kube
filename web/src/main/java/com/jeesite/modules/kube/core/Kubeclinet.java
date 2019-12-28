package com.jeesite.modules.kube.core;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * @ClassName Kubeclinet
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/16 16:29
 */
public class Kubeclinet{
    private static KubernetesClient client = null;
    private final static  String KUBECONFIG_FILE = Kubeclinet.filePath(Kubeclinet.class.getResource("/config/kube-config"));

    public static String filePath(URL path) {
        try {
            return Paths.get(path.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
        public static KubernetesClient getKubeclinet(){
        Config config = null;
                if (config == null) {
                    synchronized (Kubeclinet.class) {
                        System.setProperty(Config.KUBERNETES_KUBECONFIG_FILE, KUBECONFIG_FILE);
                        config = new ConfigBuilder()
                               // .withMasterUrl("https://192.168.152.132:6443")
                                .build();
                    }
                }
                     client = new DefaultKubernetesClient(config);
        return client;
    }



}

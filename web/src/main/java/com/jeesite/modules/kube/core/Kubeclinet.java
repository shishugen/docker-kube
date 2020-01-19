package com.jeesite.modules.kube.core;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * @ClassName Kubeclinet
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/16 16:29
 */
public class KubeClinet{
    private static KubernetesClient client = null;

    private final static  String KUBECONFIG_FILE = filePath(KubeClinet.class.getResource("/kube-docker/kube/kube-config"));

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
                    synchronized (KubeClinet.class) {
                        System.setProperty(Config.KUBERNETES_KUBECONFIG_FILE, KUBECONFIG_FILE);
                        config = new ConfigBuilder()
                               // .withMasterUrl("https://192.168.152.132:6443")
                                .build();
                    }
                }
                     client = new DefaultKubernetesClient(config);
        return client;
    }

    public static boolean dalDeploment(String nameSpace, String DeploymentName){
        return getKubeclinet().apps().deployments()
                .inNamespace(nameSpace)
                .withName(DeploymentName).delete();
    }
    public static boolean dalDeploment(String DeploymentName){
        return getKubeclinet().apps().deployments()
                .inNamespace(KubeConfig.DEFAULT_NAMESPACE)
                .withName(DeploymentName).delete();
    }

    public static void main(String[] args) {
        PodList list = KubeClinet.getKubeclinet().pods().list();
        System.out.println(list);
        System.out.println(list.getItems().size());

    }

}

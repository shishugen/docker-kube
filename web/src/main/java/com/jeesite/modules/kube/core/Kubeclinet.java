package com.jeesite.modules.kube.core;

import com.google.common.collect.Lists;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.service.image.KubeImagesService;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.networking.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.CopyOrReadable;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.core.io.buffer.DataBufferUtils.readInputStream;

/**
 * @ClassName Kubeclinet
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/16 16:29
 */
public class KubeClinet{
    private static KubernetesClient client = null;

    private final static  String KUBECONFIG_FILE = filePath(KubeClinet.class.getResource("/kube-docker/kube/kube-config"));

    /***
     * 端口
     */
    private  static  Integer CONTAINER_PORT = 22;

    /***
     * labels
     */
    private  static  String LABELS_KEY = "app";

    /***
     * WEBSSH2_KEY 代码 label
     */
    private  static  String WEBSSH2_KEY = "webssh2";



    /***
     * labels
     */
    private  static  String[] COMMAND_ARRY = {"/usr/sbin/sshd","-D"};







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

    /**
     * 创建 命名空间
     * */
    public static boolean createNamespace(String name){
        try{
            Map<String,String> LabelsMap = new HashMap<>();
            LabelsMap.put("app",name);
            Namespace namespace = new NamespaceBuilder().withNewMetadata().withName(name).withLabels(LabelsMap).endMetadata().build();
            getKubeclinet().namespaces().create(namespace);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /***
     *
     * @param networkName
     * @param accessName
     * @return
     */

    public  static boolean createNetworkPolicy(String namespaceName , String appName,String labelsName){
        try{
                  NetworkPolicy build = new NetworkPolicyBuilder()
                    .withNewMetadata()
                    .withName(namespaceName)
                    .withNamespace(namespaceName)
                    .addToLabels(LABELS_KEY, namespaceName)
                    .endMetadata()
                    .withNewSpec()
                    .withNewPodSelector()
                    .addToMatchLabels(LABELS_KEY, labelsName)
                    .endPodSelector()
                    .addToIngress(
                            new NetworkPolicyIngressRuleBuilder()
                                            .addToFrom(0, new NetworkPolicyPeerBuilder().withNewPodSelector()
                                            .addToMatchLabels(LABELS_KEY, labelsName).endPodSelector()
                                            .build()
                              ).addToFrom(1, new NetworkPolicyPeerBuilder().withNewNamespaceSelector()
                                    .addToMatchLabels(LABELS_KEY, WEBSSH2_KEY).endNamespaceSelector()
                                    .build()
                            ).build()).addToEgress(0,new NetworkPolicyEgressRuleBuilder().addToTo(
                            new NetworkPolicyPeerBuilder().withNewPodSelector().addToMatchLabels(LABELS_KEY, labelsName).endPodSelector().build()
                    ).addToTo(1,
                            new NetworkPolicyPeerBuilder().withNewNamespaceSelector().addToMatchLabels(LABELS_KEY, labelsName).endNamespaceSelector().build()).build()).endSpec().build();
            getKubeclinet().network().networkPolicies().create(build);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 创建 deployment
     * */
    public static boolean createDeployment(String deploymentName, String namespaceName,String appName,String image,String cpu, String memory){
        try{
            Deployment newDeployment = new DeploymentBuilder().withNewMetadata().withName(deploymentName).withNamespace(namespaceName).endMetadata()
                    .withNewSpec().withNewSelector().addToMatchLabels(LABELS_KEY, appName).endSelector()
                    .withNewTemplate().withNewMetadata().addToLabels(LABELS_KEY, appName).endMetadata()
                    .withNewSpec().withContainers(
                            new ContainerBuilder().withName(appName).withImage(image)
                                    .withNewResources().addToRequests("cpu",new QuantityBuilder().withAmount(cpu).withFormat("G").build())
                                    .addToRequests("memory",new QuantityBuilder().withAmount(memory).withFormat("G").build()).endResources()
                                    .withCommand(COMMAND_ARRY)
                                    .withPorts(new  ContainerPortBuilder().withContainerPort(CONTAINER_PORT).build())
                                    .build()
                        ).endSpec().endTemplate().endSpec().build();
            Deployment deployment = getKubeclinet().apps().deployments().create(newDeployment);
            System.out.println(deployment);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /***
     * 创建 pod
     * @param namespace
     * @param podName
     * @param image
     * @return
     */
    public static boolean createPod(String namespace, String podName,String labelsName ,String image , String cpu, String memory){
        try{
            Pod pod = new PodBuilder().withNewMetadata().withName(podName).withNamespace(namespace).addToLabels(LABELS_KEY, labelsName).endMetadata()
                    .withNewSpec().withContainers(new ContainerBuilder().withName(labelsName).withImage(image)
                            .withNewResources().addToRequests("cpu",new QuantityBuilder().withAmount(cpu).withFormat("G").build())
                            .addToRequests("memory",new QuantityBuilder().withAmount(memory).withFormat("G").build()).endResources()
                            .withCommand(COMMAND_ARRY).
                                    addToPorts(new ContainerPortBuilder().withContainerPort(CONTAINER_PORT).build()).build()).endSpec().build();
            Pod newPod = getKubeclinet().pods().create(pod);
            System.out.println(newPod);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



    /**
     * 创建 命名空间
     * */
    public static boolean checkNamespace(String name){
          return getKubeclinet().namespaces().list().getItems().
                stream().anyMatch(a -> name.equals(a.getMetadata().getName()));
    }

    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
    public static void main(String[] args) throws IOException {

        InputStream read = KubeClinet.getKubeclinet().pods().inNamespace("class-test-user1").withName("class-test-user1-0")
                .file("/root/").read();

        byte[] bytes = readInputStream(read);

        read.read(bytes);    //读取文件中的内容到b[]数组
        read.close();
        System.out.println(new String(bytes));




  /*      // String namespaceName ="test";
        String namespaceName ="test";
      //  createNamespace(namespaceName);

        String deploymentName ="test";

        String appName ="test";
        String image ="registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh:v1.0";
       // createDeployment(deploymentName,namespaceName,appName,image);

        createPod(namespaceName,"test-1",image);

      //  createNetworkPolicy(namespaceName,appName,"webssh2");

*/


    }

}

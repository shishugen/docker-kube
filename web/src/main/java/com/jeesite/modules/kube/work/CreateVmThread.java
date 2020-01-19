package com.jeesite.modules.kube.work;

import com.jeesite.modules.kube.core.KubeClinet;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @ClassName SyncCreateVmThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/23 15:01
 */
public class CreateVmThread extends  Thread{

    private String cpu;
    private String memory;
    private String imagesName;
    private Integer number;
    private Integer type;
    private String applyId;
    private String userId;

    public CreateVmThread(){ }

    public CreateVmThread(String cpu, String memory, String imagesName, Integer number,String applyId) {
        this.cpu = cpu;
        this.memory = memory;
        this.imagesName = imagesName;
        this.number = number;
        this.applyId = applyId;
    }
    public CreateVmThread(String cpu, String memory, String imagesName, Integer number,String applyId,Integer type) {
        this.cpu = cpu;
        this.memory = memory;
        this.imagesName = imagesName;
        this.number = number;
        this.applyId = applyId;
        this.type = type;
    }
    public CreateVmThread(String cpu, String memory, String imagesName, Integer number,String applyId,Integer type,String userId) {
        this.cpu = cpu;
        this.memory = memory;
        this.imagesName = imagesName;
        this.number = number;
        this.applyId = applyId;
        this.type = type;
        this.userId = userId;
    }
    public CreateVmThread(String cpu, String memory, String imagesName, Integer number) {
        this.cpu = cpu;
        this.memory = memory;
        this.imagesName = imagesName;
        this.number = number;
    }
    public CreateVmThread(String cpu, String memory, Integer number) {
        this.cpu = cpu;
        this.memory = memory;
        this.imagesName = DEFAULT_IMAGES_NAME;
        this.number = number;
    }

    private static final String DEFAULT_NAMESPACE = "default";
    private static final String DEFAULT_IMAGES_NAME = "registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh:v1.0";

    //kay:deployment name   value : images_id
    public static Map<String,String> syncVmMap = new ConcurrentHashMap<>();
    @Override
    public void run() {
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
            synchronized (this){
                try {
                    String deploymentName = UUID.randomUUID().toString().replaceAll("-", "");
                    Deployment deployment = new Deployment();
                    DeploymentSpec deploymentSpec = new DeploymentSpec();
                    ObjectMeta metadata0 = new ObjectMeta();
                    metadata0.setName(deploymentName); //设置名字
                    deployment.setMetadata(metadata0);
                    LabelSelector selector = new LabelSelector();
                    Map<String, String> matchLabels = new HashMap<>();
                    matchLabels.put("app","centos-ssh");
                    selector.setMatchLabels(matchLabels);
                    deploymentSpec.setSelector(selector);
                    deploymentSpec.setReplicas(number);
                    PodTemplateSpec template = new PodTemplateSpec();
                    ObjectMeta metadata = new ObjectMeta();
                    Map<String, String> labels = new HashMap<>();
                    labels.put("app","centos-ssh");
                    metadata.setLabels(labels);
                    template.setMetadata(metadata);
                    PodSpec spec = new PodSpec();
                    List<Container> containerList = new ArrayList<>();
                    Container container = new Container();
                    container.setName("centos-01");
                    container.setImage(imagesName);
                    ResourceRequirements resources = new ResourceRequirements();
                    Map<String, Quantity> requestsMap = new HashMap<>();
                    requestsMap.put("cpu",new Quantity(cpu,"G"));
                    requestsMap.put("memory",new Quantity(memory,"Mi"));
                    resources.setRequests(requestsMap);

                    container.setResources(resources);

                    List<String> commandList = new ArrayList<>();
                    commandList.add("/usr/sbin/sshd");
                    commandList.add("-D");
                    container.setCommand(commandList);

                    List<ContainerPort> ports = new ArrayList<>();
                    ContainerPort containerPort = new ContainerPort();
                    containerPort.setProtocol("TCP");
                    //containerPort.setHostPort(22);
                    containerPort.setContainerPort(22);
                    ports.add(containerPort);
                    container.setPorts(ports);

                    containerList.add(container);
                    spec.setContainers(containerList);
                    template.setSpec(spec);
                    deploymentSpec.setTemplate(template);
                    deployment.setSpec(deploymentSpec);
                    Deployment backData = kubeclinet.apps().deployments().inNamespace(DEFAULT_NAMESPACE).create(deployment);
                    System.out.println("backData=="+backData);
                    System.out.println("backData111=="+backData);
                    ThreadPool.executorService.submit(new SyncCreateVmThread(applyId,type,backData.getMetadata().getName(),backData.getMetadata().getNamespace(),imagesName,userId));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
    }
}

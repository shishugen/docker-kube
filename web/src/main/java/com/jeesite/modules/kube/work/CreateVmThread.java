package com.jeesite.modules.kube.work;

import com.jeesite.modules.kube.core.Kubeclinet;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.kube.utlis.SpringUtil;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @ClassName SyncCreateVmThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/23 15:01
 */
@Component
public class CreateVmThread extends  Thread{

    private String cpu;
    private String memory;
    private String imagesName;
    private Integer number;

    public CreateVmThread(){ }

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

        KubernetesClient kubeclinet = Kubeclinet.getKubeclinet();
         AtomicBoolean flag = new AtomicBoolean(true);
        while (flag.get()){
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
                    SyncCreateVmThread.syncVmMap.put(deploymentName+"__"+DEFAULT_NAMESPACE,imagesName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

}

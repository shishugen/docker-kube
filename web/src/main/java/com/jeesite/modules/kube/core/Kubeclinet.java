package com.jeesite.modules.kube.core;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.jeesite.common.idgen.IdGen;
import com.jeesite.common.io.FileUtils;
import com.jeesite.modules.kube.dao.vm.KubeVmDao;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.image.KubeImagesService;
import com.jeesite.modules.kube.utlis.SpringUtil;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.networking.*;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.dsl.*;
import javassist.compiler.ast.Variable;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.core.io.buffer.DataBufferUtils.readInputStream;

/**
 * @ClassName Kubeclinet
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/16 16:29
 */
public class KubeClinet{

    private static Logger logger = LoggerFactory.getLogger(KubeClinet.class);


    private static KubernetesClient client = null;

     private final static  String KUBECONFIG_FILE = filePath(KubeClinet.class.getResource("/kube-docker/kube/kube-config"));

    //  private final static  String KUBECONFIG_FILE = filePath(KubeClinet.class.getResource("/kube-docker/kube/config"));

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

    private static String DEFAULT_NAMESPACE = "default";

    private static  final String WIN_NFS_SERVER ="192.168.103.234";

    private static  final String CENTOS_NFS_SERVER ="10.150.1.15";

    /***
     * labels
     */
    private  static  String[] COMMAND_ARRY = {"/usr/sbin/sshd","-D"};







    public static String filePath(URL path) {
        try {
            boolean isWindows =  System.getProperty("os.name").toLowerCase().contains("win");
            if(isWindows){
                return Paths.get(path.toURI()).toString();
            }
            return  Paths.get(File.separator+"home"+File.separator+"kube-config").toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static KubernetesClient getKubeclinet(){
        logger.info("获取连接 KUBECONFIG_FILE: {}",KUBECONFIG_FILE);
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

    public static void client(){
        System.out.println(client);
        if(client != null){
            client.close();
            System.out.println("闭关");
        }
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
     *  命名空间
     * @param DeploymentName
     * @return
     */
    public static boolean dalNamespace(String name){
        return getKubeclinet().namespaces().withName(name).delete();

    }

    /**
     * 创建 命名空间
     * */
    public static boolean createNamespace(String name){
        System.out.println("createNamespace");
        logger.info("创建Namespaces : {}",name);
        try{
            if (checkNamespace(name)){
                return true;
            }
            Map<String,String> LabelsMap = new HashMap<>(1);
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
        logger.info("创建 createNetworkPolicy namespaceName : {},appName : {},labelsName : {}",namespaceName,appName,labelsName);
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
                                    .addToRequests("memory",new QuantityBuilder().withAmount(memory).withFormat("Mi").build()).endResources()
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
    public static boolean createPod(String namespace, String podName,String labelsName ,String image , String cpu, String memory,boolean readOnly){
        logger.info("创建 createPod namespace :{} , podName : {} , labelsName : {}, image : {} , cpu : {} , memory :{}",
                namespace,podName ,labelsName,image,cpu,memory);
            String pvName = podName+"-pv";
            String pvcName = podName+"-pvc";

            if(!checkPV(pvName)){
                testPV(pvName,readOnly);
            }
            if(!checkPVC(pvcName,namespace)){
                testPVC(pvcName,namespace);
            }
        try{

           // String nfsPath = isWindows == false ? File.separator+"nfs"+File.separator+"data"+File.separator+"nginx3"+File.separator
                //    : File.separator + "home" + File.separator;
            String nfsPath =   File.separator + "home" + File.separator;
            Pod pod = new PodBuilder().withNewMetadata().withName(podName).withNamespace(namespace).addToLabels(LABELS_KEY, labelsName).endMetadata()
                    .withNewSpec().withContainers(new ContainerBuilder().withName(labelsName).withImage(image)
                          //  .withNewResources().addToRequests("cpu",new QuantityBuilder().withAmount(cpu).withFormat("G").build())
                         //  .addToRequests("memory",new QuantityBuilder().withAmount(memory).withFormat("G").build()).endResources()
                            .withCommand(COMMAND_ARRY).
                                    addToPorts(new ContainerPortBuilder().withContainerPort(CONTAINER_PORT).build())
                            .addNewVolumeMount().withName(podName).withMountPath("/home/").endVolumeMount()
                            .build())
                            .addNewVolume().withName(podName)
                           .withNewPersistentVolumeClaim(pvcName,readOnly).endVolume()
                    .endSpec().build();
            Pod newPod = getKubeclinet().pods().create(pod);
            System.out.println(newPod);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void createService(){
        int port = 0;
        String targetPort = "test";
        Integer nodePort = 2234;
        String selectorKey = "app";
        String selectorValue = "centos7";
        String type = "NodePort";
        Service build = new ServiceBuilder().withNewMetadata().withName("testService").withNamespace(DEFAULT_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .addNewPort()
                //内网端口
                .withPort(port).withProtocol("TCP") //默认
                .withNewProtocol(targetPort)
                .withNodePort(nodePort)
                .endPort()
                .withType(type)
                .addToSelector(selectorKey, selectorValue).endSpec()
                .build();
        Service service = getKubeclinet().services().create(build);


    }



    /**
     * 检查 命名空间
     * */
    public static boolean checkNamespace(String name){
        logger.info("检查 命名空间 {}",name);
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

    public static Pod watch(String namespace, String podName,String labelsName ,String image , String cpu, String memory){
         Pod retPod = null;
        KubeClinet.createNamespace(namespace);
        final CountDownLatch closeLatch = new CountDownLatch(1);
        KubeClinet.createPod(namespace, podName, labelsName, image,cpu,memory,true);

        NawWatcher watche = new NawWatcher(namespace,podName,closeLatch);
        Watch watch = getKubeclinet().pods().inNamespace(namespace).withName(podName).watch(watche);
        try {
            closeLatch.await(200, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        watch.close();
        Pod pod = watche.getPod();
        return pod;
    }

    static class NawWatcher implements Watcher<Pod>{
       private String namespace ;
       private String podname ;
       private CountDownLatch closeLatch ;

        public  NawWatcher(String namespace, String podname, CountDownLatch closeLatch){
            this.namespace=namespace;
            this.podname=podname;
            this.closeLatch=closeLatch;
        }
        Pod retPod =null;
        @Override
        public void eventReceived(Action action, Pod pod) {
            System.out.println(pod);
            System.out.println("pod状态===》》" + pod.getStatus().getPhase());
            System.out.println(action);
            if (pod.getStatus().getPhase().equals("Running")) {
                System.out.println("close-----");
                retPod = getKubeclinet().pods().inNamespace(namespace).withName(podname).get();
                closeLatch.countDown();
            }
        }

        @Override
        public void onClose(KubernetesClientException cause) {
            closeLatch.countDown();
        }

        public Pod getPod(){
            return retPod;
        }

    }

    public static void  testPV(String pvName , boolean isReadOnly ){
        boolean isWindows =  System.getProperty("os.name").toLowerCase().contains("win");
        Map<String,Quantity> map = new HashMap(1);
        map.put("storage",new Quantity("5","Gi"));
        String nfsPath = File.separator+"nfs"+File.separator+"data"+File.separator+"nginx3"+File.separator;
        PersistentVolume build = new PersistentVolumeBuilder()
                .withNewMetadata()
                .addToLabels("app",pvName)
                .withName(pvName)
                .endMetadata()
                .withNewSpec()
                .withCapacity(map)
               // .withNewVolumeMode("Filesystem")
                //ReadOnlyMany 可以被多个node读取。缩写为ROX。
               //  ReadWriteMany：可以摆多个node读写。缩写为RWX。
                //ReadWriteOnce：可以被一个node读写。缩写为RWO。
                .withAccessModes("ReadOnlyMany")
                /**
                 * - 保留策略：允许人工处理保留的数据。
                 * - 删除策略：将删除pv和外部关联的存储资源，需要插件支持。
                 * - 回收策略：将执行清除操作，之后可以被新的pvc使用，需要插件支持。
                 *   目前只有NFS和HostPath类型卷支持回收策略，AWS EBS,GCE PD,Azure Disk和Cinder支持删除(Delete)策略。
                 */
                .withNewPersistentVolumeReclaimPolicy("Recycle") //Retain  Delete  Recycle（已废弃）
              //  .withStorageClassName("slow")
               // .withMountOptions(new String[]{"- hard, - nfsvers=4.1"})
                .withNewNfs("/nfs/data/nginx3/",isReadOnly,isWindows ? WIN_NFS_SERVER : CENTOS_NFS_SERVER)
                .endSpec()
                .build();
         KubernetesClient kubeclinet = getKubeclinet();
        PersistentVolume persistentVolume = kubeclinet.persistentVolumes().create(build);
        System.out.println(persistentVolume);

    }

    public static void  testPVC(String pvcName ,String namespace){
        Map<String,Quantity> map = new HashMap(1);
        map.put("storage",new Quantity("5","Gi"));
        PersistentVolumeClaim build = new PersistentVolumeClaimBuilder()
                .withNewMetadata()
                .withName(pvcName)
                .withNamespace(namespace)
                .endMetadata()
                .withNewSpec()
                .withAccessModes("ReadOnlyMany")
             //   .withNewVolumeMode("Filesystem")
                .withNewResources()
                .withRequests(map)
                .endResources()
             //   .withStorageClassName("slow")
             //   .withNewSelector()
              //  .withMatchLabels(Maps.newHashMap("release", "stable"))
              //  .addToMatchExpressions(new LabelSelectorRequirement("environment", "In", Lists.newArrayList("dev")))
           //     .endSelector()
                .endSpec()
                .build();
        KubernetesClient kubeclinet = getKubeclinet();
        PersistentVolumeClaim persistentVolume = kubeclinet.persistentVolumeClaims().create(build);
        System.out.println(persistentVolume);
    }
    public static boolean checkPV(String pvName){
        List<PersistentVolume> persistentVolumeList = getKubeclinet().persistentVolumes().list().getItems();
        return persistentVolumeList.stream().anyMatch(a -> pvName.equals(a.getMetadata().getName()));
    }

    public static boolean checkPVC(String pvcName , String namespace){
        List<PersistentVolumeClaim> list = getKubeclinet().persistentVolumeClaims().inNamespace(namespace).list().getItems();
        return list.stream().anyMatch(a -> pvcName.equals(a.getMetadata().getName()));
    }

    public static boolean deletePV(String pvName){
      return getKubeclinet().persistentVolumes().withName(pvName).delete();
    }

    public static boolean deletePVC(String pvcName,String namespace){
        return getKubeclinet().persistentVolumeClaims().inNamespace(namespace).withName(pvcName).delete();
    }

    public static void main(String[] args) throws IOException, InterruptedException {



/*
        String namespace = "class";
        createNamespace(namespace);
        String podName = "test";
        String pvName = podName + "-pv";
        String pvcName = podName + "-pvc";

       if(!checkPV(pvName)){
            testPV(pvName,false);
        }
        if(!checkPVC(pvcName,namespace)){
            testPVC(pvcName,namespace);
        }
       String image ="registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh:v1.0";
        createPod(namespace, podName,podName , image , "1", "1",false);*/

//        MixedOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> pods = getKubeclinet().pods();
//        System.out.println(pods.list().getItems());
/*
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final CountDownLatch execLatch = new CountDownLatch(1);
        ExecWatch execWatch = getKubeclinet().pods().inNamespace(namespace).withName(pod)
                .writingOutput(out).usingListener(new ExecListener() {
                    @Override
                    public void onOpen(Response response)
                    {
                        System.out.println(response);
                        System.out.println("response");
                    }

                    @Override
                    public void onFailure(Throwable throwable, Response response) {
                        System.out.println(response);
                        System.out.println("response");
                        execLatch.countDown();
                    }

                    @Override
                    public void onClose(int i, String s) {
                        System.out.println("onClose");
                        System.out.println(i);
                        System.out.println(s);
                      //  execLatch.countDown();
                    }
                }).exec();

        System.out.println(execWatch);
        System.out.println("www"+out.toString()); */

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

    /***
     * 创建 pod
     * @param namespace
     * @param podName
     * @param image
     * @return
     */
    public static Pod createPod1(String namespace, String podName,String labelsName ,String image ){
        Pod newPod = null;
        try{
            Pod pod = new PodBuilder().withNewMetadata().withName(podName).withNamespace(namespace).addToLabels(LABELS_KEY, labelsName).endMetadata()
                    .withNewSpec().withContainers(new ContainerBuilder().withName(labelsName).withImage(image)
                            .withCommand(COMMAND_ARRY).
                                    addToPorts(new ContainerPortBuilder().withContainerPort(CONTAINER_PORT).build()).build()).endSpec().build();
             newPod = getKubeclinet().pods().create(pod);
            System.out.println(newPod);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return newPod;
    }


}

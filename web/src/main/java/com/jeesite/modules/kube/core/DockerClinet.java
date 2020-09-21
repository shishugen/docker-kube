package com.jeesite.modules.kube.core;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DisconnectFromNetworkCmd;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.work.SyncCreateVmThread2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class DockerClinet {
    private static Logger logger = LoggerFactory.getLogger(DockerClinet.class);

    //private final static  String DOCKER_FILE = "";//DockerClinet.class.getResource(File.separator +"kube-docker"+File.separator +"docker"+File.separator);
   // String fileName = this.getClass().getClassLoader().getResource("文件名").getPath();
  // private final static String DOCKER_FILE = DockerClinet.class.getClassLoader().getResource("kube-docker"+File.separator +"docker"+File.separator).getPath();

    private static final String DEFAULT_HOSTIP = "10.150.1.13";
    private static final String DEFAULT_HOST_PORT = "2375";
    private static  String DEFAULT_FILE_DIRECTORY;



    public static DockerClient getDockerClient()  {
        DockerClient dockerClient = null;
        logger.info("获取连接:{}",dockerClient);
        if (dockerClient == null){

            synchronized (DockerClinet.class){
                isWindows();
                logger.info("配置文件路径==="+DEFAULT_FILE_DIRECTORY+File.separator+DEFAULT_HOSTIP);
                DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerTlsVerify(true)
                        .withDockerCertPath(DEFAULT_FILE_DIRECTORY+File.separator+DEFAULT_HOSTIP).withDockerHost("tcp://"+DEFAULT_HOSTIP+":"+DEFAULT_HOST_PORT)
                        .withDockerConfig(DEFAULT_FILE_DIRECTORY+File.separator+DEFAULT_HOSTIP)
                        .build();
                      //  .withApiVersion("1.38")
                       // .withRegistryUrl("https://192.168.103.235/harbor/")
                       // .withRegistryUsername("admin").withRegistryPassword("Harbor12345")
                       // .withRegistryEmail("dockeruser@github.com").build();
                DockerCmdExecFactory dockerCmdExecFactory =  new JerseyDockerCmdExecFactory()
                        .withReadTimeout(1000)
                        .withConnectTimeout(1000)
                        .withMaxTotalConnections(100)
                        .withMaxPerRouteConnections(10);
                 dockerClient = DockerClientBuilder.getInstance(config).withDockerCmdExecFactory(dockerCmdExecFactory).build();
            }
        }
        return dockerClient;
    }

    public static DockerClient getDockerClient(String hostIp)  {
        DockerClient dockerClient = null;
        logger.info("获取连接:{}",dockerClient);
        if (dockerClient == null){
            synchronized (DockerClinet.class){
                isWindows();
                logger.info("配置文件路径==="+DEFAULT_FILE_DIRECTORY+File.separator+hostIp);

                DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerTlsVerify(true)
                        .withDockerCertPath(DEFAULT_FILE_DIRECTORY+File.separator+hostIp).withDockerHost("tcp://"+hostIp+":"+DEFAULT_HOST_PORT)
                        .withDockerConfig(DEFAULT_FILE_DIRECTORY+File.separator+hostIp)
                        .build();
                      //  .withApiVersion("1.38")
                       // .withRegistryUrl("https://192.168.103.235/harbor/")
                       // .withRegistryUsername("admin").withRegistryPassword("Harbor12345")
                       // .withRegistryEmail("dockeruser@github.com").build();
                DockerCmdExecFactory dockerCmdExecFactory =  new JerseyDockerCmdExecFactory()
                        .withReadTimeout(1000)
                        .withConnectTimeout(1000)
                        .withMaxTotalConnections(100)
                        .withMaxPerRouteConnections(10);
                 dockerClient = DockerClientBuilder.getInstance(config).withDockerCmdExecFactory(dockerCmdExecFactory).build();
            }
        }
        return dockerClient;
    }


    public static void  isWindows(){
        if(StringUtils.isBlank(DEFAULT_FILE_DIRECTORY)){
        boolean isWindows =  System.getProperty("os.name").toLowerCase().contains("win");
         if(isWindows){
             DEFAULT_FILE_DIRECTORY = "E:\\dockerfile";
         }else{
             DEFAULT_FILE_DIRECTORY = File.separator+"home";
         }
        }
    }




    public static String filePath(URL path) {
        try {
            return Paths.get(path.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delImage(String imagesId){
        DockerClient dockerClient = getDockerClient();
        dockerClient.removeImageCmd(imagesId).exec();

    }

    public static void main(String[] args) {
        DockerClient dockerClient = getDockerClient("192.168.103.235");
        System.out.println(dockerClient.listImagesCmd().exec());
    }
    public void test(){

        String fileName = this.getClass().getClassLoader().getResource("kube-docker"+File.separator +"docker"+File.separator).getPath();
        System.out.println(fileName);

    }



}

package com.jeesite.modules.kube.core;

import com.github.dockerjava.api.DockerClient;

import java.util.UUID;

/**
 * @ClassName KubeConfig
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/30 10:36
 */
public class KubeConfig {
    public final static  String DEFAULT_NAMESPACE ="default";
    public static final String DEFAULT_IMAGES_NAME = "registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh:v1.0";
   // public static final String REGISTRY_ADDRESS = "http://192.168.103.236:5000/v2/";
    public static final String REGISTRY_USER_NAME = "admin";
   // public static final String REGISTRY_PASSWORD = "Harbor12345";
    public static final String REGISTRY_PASSWORD = "xyharbor123";
    public static final String REGISTRY_REPOSITORY_IP = "10.150.1.14";
    public static final String REGISTRY_REPOSITORY_PROJECT = "centos-test";

    //Harbor
    public static final String HARBOR_REGISTRY_URL = "10.150.1.14";
    public static final String HARBOR_REGISTRY_USERNAME = "admin";
    public static final String HARBOR_REGISTRY_PASSWORD = "Harbor12345";

    /**
     * deployment 名字
     */
    public static String DEPLOYMENT_NAME = UUID.randomUUID().toString().replaceAll("-", "");


    public static void main(String[] args) {
        DockerClient dockerClient = DockerClinet.getDockerClient("192.168.103.236");
        System.out.println(dockerClient.listImagesCmd().exec());

    }

}

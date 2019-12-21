package com.jeesite.modules.kube.core;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerClinet {

    public static DockerClient getDockerClient(){
        DockerClient dockerClient = null;
        if (dockerClient == null){
            synchronized (DockerClinet.class){
                 dockerClient = DockerClientBuilder.getInstance("tcp://192.168.152.131:2375").build();
            }
        }
        return dockerClient;
    }
}

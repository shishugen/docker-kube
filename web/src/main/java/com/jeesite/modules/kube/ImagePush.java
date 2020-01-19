package com.jeesite.modules.kube;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.jeesite.modules.kube.core.DockerClinet;
import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.core.KubeConfig;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName ImagePush
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/27 16:32
 */
public class ImagePush {

    static  RestTemplate template = new RestTemplate();

    public static void main(String[] args) {

        BigDecimal bigDecimal = new BigDecimal("48294789041");


        System.out.println(bigDecimal);
    }


}

package test.com.jeesite.modules.kube.work; 

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.jeesite.modules.kube.core.DockerClinet;
import com.jeesite.modules.kube.core.KubeClinet;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/** 
* BindVmThread Tester. 
* 
* @author <Authors name> 
* @since <pre>$todate</pre> 
* @version 1.0 
*/
public class BindVmThreadTest {

    static int x =1;
    int y ;
    public static void main(String[] args) {
//psharkey/webssh2@sha256:6fbcd24cc12d31df90fff202c7631f04118073e7da6b8aa273b2bbd6dbe9b099
        //registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh@sha256:08a79aadcd8b7c1fa178d9b4ec1c03090f011e910addecd167bc64333cbedc93
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
        NodeList list = kubeclinet.nodes().list();
        List<Node> items1 = list.getItems();
        DockerClient dockerClient = DockerClinet.getDockerClient();
        List<Image> exec = dockerClient.listImagesCmd().exec();
        System.out.println(exec);

    }

} 

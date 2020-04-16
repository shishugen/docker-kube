package com.jeesite.modules.kube.core;

import com.madailicai.devops.harbor.HarborClient;
import com.madailicai.devops.harbor.model.HarborResponse;
import com.madailicai.devops.harbor.model.Project;
import com.madailicai.devops.harbor.model.ProjectAndRepoNum;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName HarborClient
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2020/1/3 14:12
 */
public class KubeHarborClient {

    public static HarborClient getHarbrClient(){
        HarborClient harborClient = null;
        try {
        //1) Creat harbor client
             harborClient = new HarborClient(KubeConfig.HARBOR_REGISTRY_URL);

          //2) login
            harborClient.login(KubeConfig.REGISTRY_USER_NAME, KubeConfig.HARBOR_REGISTRY_PASSWORD);
            System.out.println(harborClient);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return harborClient;
    }

    public static void main(String[] args) throws  Exception{
        RestTemplate template = new RestTemplate();

        List forObject = template.getForObject("http://192.168.103.227/api/projects", List.class);
        System.out.println("h所有项目:"+forObject.get(0));


      //  List forObjec = template.getForObject("http://192.168.103.236/api/repositories?project_id=2", List.class);
      //  System.out.println("harbor状态:"+forObjec);
        //fetch("http://192.168.103.236/api/projects/2/summary", {"credentials":"include","headers":{"accept":"application/json","accept-language":"zh-CN,zh;q=0.9","cache-control":"no-cache","content-type":"application/json","pragma":"no-cache"},"referrer":"http://192.168.103.236/harbor/projects/2/summary","referrerPolicy":"no-referrer-when-downgrade","body":null,"method":"GET","mode":"cors"}); ;
       // fetch("http://192.168.103.236/api/repositories?page=1&page_size=15&project_id=2", {"credentials":"include","headers":{"accept":"application/json","accept-language":"zh-CN,zh;q=0.9","cache-control":"no-cache","content-type":"application/json","pragma":"no-cache"},"referrer":"http://192.168.103.236/harbor/projects/2/repositories","referrerPolicy":"no-referrer-when-downgrade","body":null,"method":"GET","mode":"cors"}); ;
       // fetch("http://192.168.103.236/api/systeminfo", {"credentials":"include","headers":{"accept":"application/json","accept-language":"zh-CN,zh;q=0.9","cache-control":"no-cache","content-type":"application/json","pragma":"no-cache"},"referrer":"http://192.168.103.236/harbor/projects/2/repositories","referrerPolicy":"no-referrer-when-downgrade","body":null,"method":"GET","mode":"cors"}); ;
       // fetch("http://192.168.103.236/api/repositories?page=1&page_size=15&project_id=2", {"credentials":"include","headers":{"accept":"application/json","accept-language":"zh-CN,zh;q=0.9","cache-control":"no-cache","content-type":"application/json","pragma":"no-cache"},"referrer":"http://192.168.103.236/harbor/projects/2/repositories","referrerPolicy":"no-referrer-when-downgrade","body":null,"method":"GET","mode":"cors"});




 /*       HarborResponse harborResponse = harbrClient.checkProject("centos-test");

        System.out.println(harborResponse);

        Project project = new Project();
        project.setName("proname");
        project.setPublic(false);
        HarborResponse project1 = harbrClient.createProject(project);
        System.out.println(project1);
        //  ProjectAndRepoNum statistics = harbrClient.getStatistics();
       // System.out.println(statistics);
*/
    }
}

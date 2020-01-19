package com.jeesite.modules.kube.core;

import com.madailicai.devops.harbor.HarborClient;
import com.madailicai.devops.harbor.model.HarborResponse;
import com.madailicai.devops.harbor.model.Project;
import com.madailicai.devops.harbor.model.ProjectAndRepoNum;

import java.io.IOException;

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
        HarborClient harbrClient = getHarbrClient();
        HarborResponse harborResponse = harbrClient.checkProject("centos-test");

        System.out.println(harborResponse);

        Project project = new Project();
        project.setName("proname");
        project.setPublic(false);
        HarborResponse project1 = harbrClient.createProject(project);
        System.out.println(project1);
        //  ProjectAndRepoNum statistics = harbrClient.getStatistics();
       // System.out.println(statistics);

    }
}

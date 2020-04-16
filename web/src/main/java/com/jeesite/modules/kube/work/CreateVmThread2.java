package com.jeesite.modules.kube.work;

import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.core.KubeConfig;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.service.course.KubeCourseService;
import com.jeesite.modules.kube.service.image.KubeImagesService;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SyncCreateVmThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/23 15:01
 */
public class CreateVmThread2 extends  Thread{


    @Autowired
    private KubeVmService kubeVmService;
    private String DEFAULT_NAMESPACE = "default";
    public final static String CLASS_APPLY_PREFIX = "class-";

    private static final String DEFAULT_IMAGES_NAME = "registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh:v1.0";

    private List<KubeCourseImages> courseImages;  //所有courseimages
    private KubeApply apply;
    private List<KubeClassStudents> classStudents;
    private String courseCode;

    public CreateVmThread2(List<KubeClassStudents> classStudents ,List<KubeCourseImages> courseImages, KubeApply apply,String courseCode){
        this.courseImages = courseImages;
        this.apply = apply;
        this.classStudents = classStudents;
        this.courseCode = courseCode;

    }


    //kay:deployment name   value : images_id
    public static Map<String,String> syncVmMap = new ConcurrentHashMap<>();
    @Override
    public void run() {
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
            synchronized (this){
                try {
                    DEFAULT_NAMESPACE = CLASS_APPLY_PREFIX +courseCode;
                    //班级
                    if(apply.getType() == KubeApply.CLASS_APPLY){
                        classStudents.stream().forEach(students -> {
                            //创建 命名空间 class + 班级 + 用户id
                            String namespace =DEFAULT_NAMESPACE + "-"+ students.getUserId().getLoginCode().toString().replace("_","-");
                            System.out.println("创建 namespace-----"+namespace);
                            if(!KubeClinet.createNamespace(namespace)){
                                System.err.println("创建 命名空间异常-----");
                            }
                            String appname = namespace;
                            String labelsName = namespace;
                            for (int i =0; i < courseImages.size(); i++ ){
                                KubeImages images = courseImages.get(i).getImages();
                                //创建 pod
                                KubeClinet.createPod(namespace,appname+"-"+i,labelsName,images.getRepositoryName(),images.getCpu(),images.getMemory());
                            }
                            //NetworkPolicy网络
                            KubeClinet.createNetworkPolicy(namespace,appname,labelsName);
                            ThreadPool.executorService.submit(new SyncCreateVmThread2(students,namespace,apply));
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
    }

}

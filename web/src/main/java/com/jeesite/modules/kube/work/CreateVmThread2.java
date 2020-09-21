package com.jeesite.modules.kube.work;

import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.core.KubeConfig;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.service.apply.KubeApplyService;
import com.jeesite.modules.kube.service.course.KubeCourseService;
import com.jeesite.modules.kube.service.image.KubeImagesService;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.entity.UserRole;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName SyncCreateVmThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/23 15:01
 */
public class CreateVmThread2 extends  Thread{

    private static Logger logger = LoggerFactory.getLogger(CreateVmThread2.class);

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
        logger.info("创建容器");
            synchronized (this){
                try {
                  //  KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
                    DEFAULT_NAMESPACE = CLASS_APPLY_PREFIX +courseCode;
                    //班级
                    logger.info("synchronized");
                    if(apply.getType() == KubeApply.CLASS_APPLY){
                        classStudents.stream().forEach(students -> {
                            //创建 命名空间 class + 班级 + 用户id
                            String namespace =DEFAULT_NAMESPACE + "-"+ students.getUserId().getLoginCode().toString().replace("_","-");
                            logger.info("创建 namespaces-----{}",namespace);
                            if(!KubeClinet.createNamespace(namespace)){
                                logger.error("创建 命名空间异常 {}",namespace);
                            }
                            String appname = namespace;
                            String labelsName = namespace;
                            User user = UserUtils.get(students.getUserId().getUserCode());
                            int teacher = user.getRoleList().stream().filter(s -> s.getRoleCode().equals("TEACHER")).collect(Collectors.toList()).size();

                            boolean readOnly = teacher == 1 ? false : true;
                            logger.info("是否 readOnly {},{}",teacher,readOnly);
                            for (int i =0; i < courseImages.size(); i++ ){
                                KubeImages images = courseImages.get(i).getImages();
                                //创建 pod
                                logger.info("创建 pod");
                                KubeClinet.createPod(namespace,appname+"-"+i,labelsName,images.getRepositoryName(),
                                        images.getCpu(),images.getMemory(),readOnly);
                            }
                            //NetworkPolicy网络
                            logger.info("创建 NetworkPolicy网络");
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

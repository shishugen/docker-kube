package com.jeesite.modules.kube.work;

import com.jcraft.jsch.Session;
import com.jeesite.common.idgen.IdGen;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.core.SSHRemoteCall;
import com.jeesite.modules.kube.dao.vm.KubeVmDao;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.apply.KubeApplyService;
import com.jeesite.modules.kube.utlis.SpringUtil;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @ClassName SyncCreateVmThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/23 15:01
 */
public class SyncCreateVmThread2 extends  Thread{
    private static Logger logger = LoggerFactory.getLogger(SyncCreateVmThread2.class);

    private String namespace;

    private Integer type = 0;
    private  KubeClassStudents classStudents ;
    private  KubeApply apply ;


    public SyncCreateVmThread2(KubeClassStudents classStudents, String namespace, KubeApply apply) {
        this.apply = apply;
        this.classStudents = classStudents;
        this.namespace = namespace;
    }

    private KubeVmDao kubeVmDao;
    //kay:deployment name   value : images_id
    @Override
    public void run() {

        logger.info("同步");
        kubeVmDao = SpringUtil.getBean(KubeVmDao.class);
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
        AtomicBoolean flag = new AtomicBoolean(true);
        while (flag.get()){
            synchronized (this){
                try {
                    List<Pod> items = kubeclinet.pods().inNamespace(namespace).list().getItems();

                    System.out.println("---namespace-----"+namespace+"----items="+items.size());

                    List<Pod> collect = items.stream().filter((a -> a.getMetadata().getName().indexOf(namespace) != -1 &&
                            KubeVm.VM_STATUS_RUNNING.equals(a.getStatus().getPhase()))).collect(Collectors.toList());


                   // List<Pod> collect = items.stream().filter((a -> a.getMetadata().getName().indexOf(namespace) != -1)).collect(Collectors.toList());

                    logger.info("collect创建数量== {}",collect.size());
                    List<KubeVm> kubeVmList = new ArrayList<>();
                    collect.parallelStream().forEach((vm) -> {

                        ObjectMeta metadata = vm.getMetadata();
                        PodStatus status = vm.getStatus();
                        String hostIp = status.getHostIP();
                        KubeVm kubeVm = new KubeVm();
                        kubeVm.setVmName(metadata.getName());
                        kubeVm.setVmIp(status.getPodIP());
                        kubeVm.setHostIp(hostIp);
                        Integer integer = KubeVm.VM_STATUS_MAP.get(vm.getStatus().getPhase());
                        kubeVm.setVmStatus(integer == null ? 404 : integer);
                       // kubeVm.setDeploymentName(deploymentName);
                        kubeVm.setNamespace(namespace);
                        kubeVm.setApplyId(apply.getId());
                        kubeVm.setType(type == null ? 0 : type);
                        kubeVm.setUserId(classStudents.getUserId());
                        String id = IdGen.nextId();
                        kubeVm.setId(id);
                        //同步状态
                     /*   if(vm.getStatus().getPhase() != KubeVm.VM_STATUS_RUNNING){
                            ThreadPool.executorService.submit(new SyncVmStatus(namespace,metadata.getName(),id));
                        }*/
                        status.getContainerStatuses().forEach(st->{
                            String containerID = st.getContainerID();
                            if(StringUtils.isNotEmpty(containerID)){
                                //去掉  docker://
                                 containerID = containerID.substring(9, containerID.length());
                                kubeVm.setContainerId(containerID);
                                SSHRemoteCall sshRemoteCall =  new SSHRemoteCall();
                                try {
                                    Session session = sshRemoteCall.getInstance2().sshRemoteCallLogin(hostIp);
                                    SSHRemoteCall.sshMap.put(containerID,session);
                                } catch (Exception e) {
                                    logger.error("SSH登录失败!");
                                    e.printStackTrace();
                                }
                            }
                        });
                        // kubeVm.setVmStartDate(new Date(status.getStartTime()));
                       // kubeVm.setImagesId(imagesId);
                       // kubeVmDao.save(kubeVm);
                        kubeVmList.add(kubeVm);
                    });
                    if(kubeVmList.size() > 0 && kubeVmList != null){
                        kubeVmDao.insertBatch(kubeVmList);
                        logger.info("保存虚拟机==={}",kubeVmList.size());
                        kubeVmList.clear();
                    }

                    if(collect.size() == 0){
                        try {
                            Thread.sleep(20000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            flag.set(false);
                            logger.error("同步-----虚拟机异常退出===");
                            return;
                        }
                    }else{
                        long count = collect.stream().filter((b -> !KubeVm.VM_STATUS_RUNNING.equals(b.getStatus().getPhase()))).count();
                        System.out.println("同步-----虚拟机==="+count);
                        logger.info("同步-----虚拟机==={}",count);
                        if (count != 0){
                            try {
                                Thread.sleep(60000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                flag.set(false);
                                logger.error("同步-----虚拟机异常退出==={}",count);
                                return;
                            }
                        }else {
                            flag.set(false);
                            logger.info("同步-----虚拟机完成退出==={}",count);
                            return;
                        }
                    }
                } catch (Exception e) {
                   e.printStackTrace();
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();

        List<Pod> items = kubeclinet.pods().inNamespace("class-test-user1").list().getItems();
        System.out.println(items);
        


    }

}

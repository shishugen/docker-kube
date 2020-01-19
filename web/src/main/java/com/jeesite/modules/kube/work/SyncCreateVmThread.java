package com.jeesite.modules.kube.work;

import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.dao.vm.KubeVmDao;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.kube.utlis.SpringUtil;
import com.jeesite.modules.sys.entity.User;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @ClassName SyncCreateVmThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/23 15:01
 */
public class SyncCreateVmThread  extends  Thread{

    private String applyId;
    private String namespace;
    private String deploymentName;
    private String imagesId;
    private String userId;
    private Integer type = 0;

    public SyncCreateVmThread(String applyId) {
        this.applyId = applyId;
    }
    public SyncCreateVmThread(String applyId,Integer type) {
        this.applyId = applyId;
        this.type = type;
    }
    public SyncCreateVmThread(String applyId,Integer type,String deploymentName ,String namespace ,String imagesId) {
        this.applyId = applyId;
        this.type = type;
        this.namespace = namespace;
        this.deploymentName = deploymentName;
        this.imagesId = imagesId;
    }
    public SyncCreateVmThread(String applyId,Integer type,String deploymentName ,String namespace ,String imagesId,String userId) {
        this.applyId = applyId;
        this.type = type;
        this.namespace = namespace;
        this.deploymentName = deploymentName;
        this.imagesId = imagesId;
        this.userId = userId;
    }

    private KubeVmDao kubeVmDao;

    //kay:deployment name   value : images_id
    public  Map<String,String> syncVmMap = new ConcurrentHashMap<>();
    @Override
    public void run() {
        kubeVmDao = SpringUtil.getBean(KubeVmDao.class);
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
        AtomicBoolean flag = new AtomicBoolean(true);
        while (flag.get()){
            synchronized (this){
                try {
                    List<Pod> items = kubeclinet.pods().inNamespace(namespace).list().getItems();
                    System.out.println(deploymentName+"---deploymentName-----"+namespace+"----items="+items.size());
                    List<Pod> collect = items.stream().filter((a -> a.getMetadata().getName().indexOf(deploymentName) != -1 &&
                            KubeVm.VM_STATUS_RUNNING.equals(a.getStatus().getPhase()))).collect(Collectors.toList());
                    System.out.println("collect创建数量=="+collect.size());
                    List<KubeVm> kubeVmList = new ArrayList<>();
                    collect.parallelStream().forEach((vm) -> {
                        ObjectMeta metadata = vm.getMetadata();
                        PodStatus status = vm.getStatus();
                        KubeVm kubeVm = new KubeVm();
                        kubeVm.setVmName(metadata.getName());
                        kubeVm.setVmIp(status.getPodIP());
                        kubeVm.setHostIp(status.getHostIP());
                        kubeVm.setVmStatus(KubeVm.VmStatus.Running.ordinal());
                        kubeVm.setDeploymentName(deploymentName);
                        kubeVm.setNamespace(namespace);
                        kubeVm.setApplyId(applyId);
                        kubeVm.setType(type == null ? 0 : type);
                        kubeVm.setUserId(new User(userId));
                        status.getContainerStatuses().forEach(st->{
                            String containerID = st.getContainerID();
                            if(StringUtils.isNotEmpty(containerID)){
                                //去掉  docker://
                                String substring = containerID.substring(9, containerID.length());
                                kubeVm.setContainerId(substring);
                            }
                        });
                        // kubeVm.setVmStartDate(new Date(status.getStartTime()));
                        kubeVm.setImagesId(imagesId);
                       // kubeVmDao.save(kubeVm);
                        kubeVmList.add(kubeVm);
                    });
                    if(kubeVmList.size() > 0 && kubeVmList != null){
                        kubeVmDao.insertBatch(kubeVmList);
                        kubeVmList.clear();
                    }
                    System.out.println("保存虚拟机=="+kubeVmList.size());
                    if(collect.size() == 0){
                        try {
                            Thread.sleep(30000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            flag.set(false);
                            System.out.println("同步-----虚拟机异常退出===");
                            return;
                        }
                    }else{
                        long count = collect.stream().filter((b -> !KubeVm.VM_STATUS_RUNNING.equals(b.getStatus().getPhase()))).count();
                        System.out.println("同步-----虚拟机==="+count);
                        if (count != 0){
                            try {
                                Thread.sleep(60000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                flag.set(false);
                                System.out.println("同步-----虚拟机异常退出==="+count);
                                return;
                            }
                        }else {
                            flag.set(false);
                            System.out.println("同步-----虚拟机完成退出==="+count);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

}

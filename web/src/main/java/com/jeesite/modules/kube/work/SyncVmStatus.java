package com.jeesite.modules.kube.work;

import com.jeesite.common.idgen.IdGen;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.dao.vm.KubeVmDao;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.kube.utlis.SpringUtil;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @ClassName SyncVmStatus
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/23 15:01
 */
public class SyncVmStatus extends  Thread{

    private String namespace;
    private String podName;
    private String id;
    private Integer MAX_SYNC_COUNT = 20; //同步数次退出

    private KubeVmService vmService;

    public SyncVmStatus() { }

    public SyncVmStatus(String namespace, String podName,String id) {
    this.namespace = namespace;
    this.podName = podName;
    this.id = id;
   }


    //kay:deployment name   value : images_id
    @Override
    public void run() {
        vmService = SpringUtil.getBean(KubeVmService.class);
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
        AtomicBoolean flag = new AtomicBoolean(true);
        Integer syncCount = 0;
        while (flag.get()) {
            synchronized (this) {
                Pod pod = kubeclinet.pods().inNamespace(namespace).withName(podName).get();
                String status = pod.getStatus().getPhase();
                KubeVm kubeVm = vmService.get(id);
                kubeVm.setVmStatus(KubeVm.VM_STATUS_MAP.get(status));
                vmService.update(kubeVm);
                if(syncCount == MAX_SYNC_COUNT){
                    System.out.println(kubeVm.getVmName()+"同步数次退出--->" +MAX_SYNC_COUNT);
                    break;
                }
                if(status == KubeVm.VM_STATUS_RUNNING){
                    flag.set(false);
                    break;
                }else{
                    //等
                    try {
                        syncCount++;
                        Thread.sleep(30000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

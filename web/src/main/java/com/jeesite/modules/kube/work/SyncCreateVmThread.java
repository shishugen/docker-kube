package com.jeesite.modules.kube.work;

import com.jeesite.modules.kube.core.Kubeclinet;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@Component
public class SyncCreateVmThread  extends  Thread{

    @Autowired
    private KubeVmService kubeVmService;

    //kay:deployment name   value : images_id
    public static Map<String,String> syncVmMap = new ConcurrentHashMap<>();
    @Override
    public void run() {
        KubernetesClient kubeclinet = Kubeclinet.getKubeclinet();
         AtomicBoolean flag = new AtomicBoolean(true);
        while (flag.get()){
            synchronized (this){
                try {
                    if (syncVmMap.size() > 0){
                        syncVmMap.forEach((deploymentName_namespace, imagesId) -> {
                            String[] split = deploymentName_namespace.split("__");
                            String deploymentName = split[0];
                            String namespace = split[1];
                            List<Pod> items = kubeclinet.pods().inNamespace(namespace).list().getItems();
                            List<Pod> collect = items.stream().filter((a -> a.getMetadata().getName().indexOf(deploymentName) != -1)).collect(Collectors.toList());
                            collect.parallelStream().forEach((vm) -> {
                                ObjectMeta metadata = vm.getMetadata();
                                PodStatus status = vm.getStatus();
                                KubeVm kubeVm = new KubeVm();
                                kubeVm.setVmName(metadata.getName());
                                kubeVm.setVmIp(status.getPodIP());
                                kubeVm.setHostIp(status.getHostIP());
                               // kubeVm.setVmStartDate(new Date(status.getStartTime()));
                                kubeVm.setImagesId(imagesId);
                               // kubeVmService.save(kubeVm);
                            });
                            long count = collect.stream().filter((b -> !KubeVm.VM_STATUS_RUNNING.equals(b.getStatus().getPhase()))).count();
                            if (count != 0){
                                try {
                                    Thread.sleep(60000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }else {
                                flag.set(false);
                            }
                        });
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

}

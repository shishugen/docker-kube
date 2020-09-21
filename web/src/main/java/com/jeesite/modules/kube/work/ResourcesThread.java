package com.jeesite.modules.kube.work;

import com.jeesite.modules.kube.core.KubeClinet;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName ResourcesThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/30 10:08
 */
public class ResourcesThread {

    public static Integer  CPU = 0;
    public static Integer  MEMORY = 0;

    private void getResources(){
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
        //  List<Pod> items = kubeclinet.pods().inNamespace("default").list().getItems();
        while (true){
            List<Pod> items = kubeclinet.pods().list().getItems();
            System.out.println(items);
            AtomicReference<Integer> cpu = new AtomicReference<>(0);
            AtomicReference<Integer> memory = new AtomicReference<>(0);
            items.forEach(pod -> {
                pod.getSpec().getContainers().forEach(container -> {
                    ResourceRequirements resources = container.getResources();
                    if (resources == null)return;
                    Map<String, Quantity> limits = resources.getLimits();
                    Map<String, Quantity> requests = resources.getRequests();
                    if (limits !=null && limits.size() > 0){
                        Quantity quantityCpu = limits.get("cpu");
                        Quantity quantityMemory = limits.get("memory");
                        if(quantityCpu != null){
                            Integer podCpu = strToNumber(quantityCpu.getAmount());
                            cpu.updateAndGet(v -> v + podCpu);
                        }
                        if(quantityMemory != null){
                            Integer podMemory = strToNumber(quantityMemory.getAmount());
                            memory.updateAndGet(m-> m+podMemory);
                        }
                    }else if(requests !=null && requests.size() > 0){
                        Quantity quantityCpu = requests.get("cpu");
                        Quantity quantityMemory = requests.get("memory");
                        if(quantityCpu != null){
                            Integer podCpu = strToNumber(quantityCpu.getAmount());
                            cpu.updateAndGet(v -> v + podCpu);
                        }
                        if(quantityMemory != null){
                            Integer podMemory = strToNumber(quantityMemory.getAmount());
                            memory.updateAndGet(m-> m+podMemory);
                        }
                    }
                    // quantityCpu.
                });
            });
            try {
                Thread.sleep(60000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CPU = cpu.get();
            MEMORY = memory.get();
            System.out.println(cpu);
            System.out.println(memory);
        }

    }


    private static Integer strToNumber(String str){
        str = str.trim();
        System.out.println(str);
        StringBuilder sb = new StringBuilder();
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    sb.append(str.charAt(i)) ;
                } else {
                    System.out.println("单位"+str.charAt(i));
                    if("M".equalsIgnoreCase(str.charAt(i)+"")){
                        int unitG = Integer.valueOf(sb.toString()) / 1024;
                        sb.delete(0,sb.length());
                        sb.append(unitG);
                    }
                    break;
                }
            }
        }
        return Integer.valueOf(sb.toString());
    }

    public static void main(String[] args) {
        if("m".equalsIgnoreCase("M")){
            System.out.println("22");
        }
    }
}

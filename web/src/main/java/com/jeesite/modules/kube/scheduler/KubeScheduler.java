package com.jeesite.modules.kube.scheduler;

import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.service.apply.KubeApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
public class KubeScheduler {

    @Autowired
    private KubeApplyService kubeApplyService;
    
    
    @Scheduled(cron="0 */1 * * * ?")
    public void bindingVm(){
        System.out.println("bindingVm--启动");
        kubeApplyService.bindingVm();

    }

    @Scheduled(cron="0 */1 * * * ?")
    public void releaseVm(){
        System.out.println("releaseVm--启动");
        kubeApplyService.releaseVm();
    }
}

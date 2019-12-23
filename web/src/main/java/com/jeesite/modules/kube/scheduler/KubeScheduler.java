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
    public void courseApply(){
        System.out.println("1111111111111111111222222222");
        kubeApplyService.findByStartDate();

    }

    @Scheduled(cron="0 */1 * * * ?")
    public void courseDown(){
        System.out.println("1111111111111111111222222222");
      //  kubeApplyService.findBydneDate();

    }
}

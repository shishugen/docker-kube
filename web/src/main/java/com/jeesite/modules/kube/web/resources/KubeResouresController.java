/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.resources;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.kube.work.ResourcesThread;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 虚拟机Controller
 * @author ssg
 * @version 2019-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/kube/resoures/KubeResoures")
public class KubeResouresController extends BaseController {

	@Autowired
	private KubeVmService kubeVmService;

	private static final String DEFAULT_NAMESPACE = "default";
	

	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:resoures:KubeResoures:view")
	@RequestMapping(value = {"list", ""})
	public String list(KubeVm kubeVm, Model model) {
		model.addAttribute("CPU", ResourcesThread.CPU);
		model.addAttribute("MEMORY", ResourcesThread.MEMORY);
		return "modules/kube/resoures/kubeResoures";
	}

	public static void main(String[] args) {
		KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
		NodeList list = kubeclinet.nodes().list();
		List<Node> items1 = list.getItems();
		System.out.println(items1);
		//总
		AtomicInteger totalCpu = new AtomicInteger();
		AtomicInteger totalMemory = new AtomicInteger();
		AtomicLong totalStorage = new AtomicLong();

		//可用
		AtomicInteger allocatableCpu = new AtomicInteger();
		AtomicInteger allocatableMemory = new AtomicInteger();
        AtomicLong allocatableStorage = new AtomicLong();
		//后续优化
		List<Node> collect = items1.stream().filter(a -> a.getSpec().getTaints().size() != 1).collect(Collectors.toList());
        items1.stream().forEach(node->{
			NodeStatus status = node.getStatus();
			Map<String, Quantity> capacity = status.getCapacity();
			capacity.forEach((k, v)->{
				Quantity quantity = capacity.get(k);
				switch (k){
					case "cpu":
						String amount = quantity.getAmount();
						totalCpu.addAndGet(Integer.valueOf(amount));
						break;
					case "ephemeral-storage":
						String nodeStorage = quantity.getAmount();
						if(nodeStorage.indexOf("Mi") != -1){
							totalStorage.addAndGet(Long.valueOf(nodeStorage.substring(0,nodeStorage.length()-2))/1024);
						}else{
                            BigDecimal bigDecimal = new BigDecimal(nodeStorage);
                            totalStorage.addAndGet(Long.valueOf(nodeStorage)/ 1024 / 1024 / 1024);
						}
						break;
					case "memory":
						String modeMemory = quantity.getAmount();
						String substring = modeMemory.substring(0, modeMemory.length() - 2);
						//totalMemory.addAndGet(Integer.valueOf(substring) / 1024 / 1024);
						totalMemory.addAndGet(Integer.valueOf(substring) );
						break;
				}

			});
			//allocatable
			Map<String, Quantity> allocatable = status.getAllocatable();
			allocatable.forEach((k,v)->{
				Quantity quantity = allocatable.get(k);
				switch (k){
					case "cpu":
						String amount = quantity.getAmount();
						allocatableCpu.addAndGet(Integer.valueOf(amount));
						break;
					case "ephemeral-storage":
						String nodeStorage = quantity.getAmount();
						if(nodeStorage.indexOf("Mi") != -1){
							allocatableStorage.addAndGet(Long.valueOf(nodeStorage.substring(0,nodeStorage.length()-2))/1024);
						}else{
							allocatableStorage.addAndGet(Long.valueOf(nodeStorage)/1024/1024/1024);
						}
						break;
					case "memory":
						String modeMemory = quantity.getAmount();
						String substring = modeMemory.substring(0, modeMemory.length() - 2);
						allocatableMemory.addAndGet(Integer.valueOf(substring) );
						//allocatableMemory.addAndGet(Integer.valueOf(substring) / 1024 / 1024);
						break;
				}
			});


		});
		System.out.println("allocatableMemory--->"+allocatableMemory);
		System.out.println("allocatableCpu----->"+allocatableCpu);
		System.out.println("allocatableStorage----->"+allocatableStorage);

		System.out.println("totalMemory--->"+totalMemory);
		System.out.println("totalCpu----->"+totalCpu);
		System.out.println("totalStorage----->"+totalStorage);

	}


}
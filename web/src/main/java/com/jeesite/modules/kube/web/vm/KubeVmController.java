/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.vm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesite.modules.kube.core.Kubeclinet;
import com.jeesite.modules.kube.work.SyncCreateVmThread;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
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

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;

import java.util.*;

/**
 * 虚拟机Controller
 * @author ssg
 * @version 2019-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/kube/vm/kubeVm")
public class KubeVmController extends BaseController {

	@Autowired
	private KubeVmService kubeVmService;

	private static final String DEFAULT_NAMESPACE = "default";
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public KubeVm get(String id, boolean isNewRecord) {
		return kubeVmService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:vm:kubeVm:view")
	@RequestMapping(value = {"list", ""})
	public String list(KubeVm kubeVm, Model model) {
		model.addAttribute("kubeVm", kubeVm);
		return "modules/kube/vm/kubeVmList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("kube:vm:kubeVm:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<KubeVm> listData(KubeVm kubeVm, HttpServletRequest request, HttpServletResponse response) {
		kubeVm.setPage(new Page<>(request, response));
		Page<KubeVm> page = kubeVmService.findPage(kubeVm);
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("kube:vm:kubeVm:view")
	@RequestMapping(value = "form")
	public String form(KubeVm kubeVm, Model model) {
		model.addAttribute("kubeVm", kubeVm);
		return "modules/kube/vm/kubeVmForm";
	}

	/**
	 * 保存虚拟机
	 */
	@RequiresPermissions("kube:vm:kubeVm:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated KubeVm kubeVm) {
		Deployment deployment = createDeployment(1, "1", "1");
		ObjectMeta metadata = deployment.getMetadata();
		String deploymentName = metadata.getName();
		String namespace = metadata.getNamespace();

		kubeVm.setDeploymentName(deploymentName);
		kubeVm.setNamespace(namespace);
		//kubeVmService.save(kubeVm);
		SyncCreateVmThread syncCreateVmThread = new SyncCreateVmThread();
		//SyncCreateVmThread.syncVmMap.put(deploymentName+"__"+DEFAULT_NAMESPACE,"imageId");
		SyncCreateVmThread.syncVmMap.put("c2870fdd38ee4ae8bef8331e521f54d1"+"__"+DEFAULT_NAMESPACE,"imageId");
		syncCreateVmThread.start();


		return renderResult(Global.TRUE, text("保存虚拟机成功！"));
	}
	
	/**
	 * 删除虚拟机
	 */
	@RequiresPermissions("kube:vm:kubeVm:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(KubeVm kubeVm) {
		kubeVmService.delete(kubeVm);
		return renderResult(Global.TRUE, text("删除虚拟机成功！"));
	}

	public Deployment createDeployment(int sun , String cpu, String memory){
		String imagesName = "registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh:v1.0";

		KubernetesClient kubeclinet = Kubeclinet.getKubeclinet();
		Deployment deployment = new Deployment();

		DeploymentSpec deploymentSpec = new DeploymentSpec();

		ObjectMeta metadata0 = new ObjectMeta();
		metadata0.setName(UUID.randomUUID().toString().replaceAll("-","")); //设置名字
		deployment.setMetadata(metadata0);

		LabelSelector selector = new LabelSelector();
		Map<String, String> matchLabels = new HashMap<>();
		matchLabels.put("app","centos-ssh");
		selector.setMatchLabels(matchLabels);
		deploymentSpec.setSelector(selector);

		deploymentSpec.setReplicas(sun);

		PodTemplateSpec template = new PodTemplateSpec();

		ObjectMeta metadata = new ObjectMeta();
		Map<String, String> labels = new HashMap<>();
		labels.put("app","centos-ssh");
		metadata.setLabels(labels);
		template.setMetadata(metadata);

		PodSpec spec = new PodSpec();
		List<Container> containerList = new ArrayList<>();
		Container container = new Container();
		container.setName("centos-01");
		container.setImage(imagesName);

		ResourceRequirements resources = new ResourceRequirements();
		Map<String, Quantity> requestsMap = new HashMap<>();
		requestsMap.put("cpu",new Quantity(cpu,"G"));
		requestsMap.put("memory",new Quantity(memory,"Mi"));
		resources.setRequests(requestsMap);

		container.setResources(resources);

		List<String> commandList = new ArrayList<>();
		commandList.add("/usr/sbin/sshd");
		commandList.add("-D");
		container.setCommand(commandList);

		List<ContainerPort> ports = new ArrayList<>();
		ContainerPort containerPort = new ContainerPort();
		containerPort.setProtocol("TCP");
		//containerPort.setHostPort(22);
		containerPort.setContainerPort(22);
		ports.add(containerPort);
		container.setPorts(ports);

		containerList.add(container);
		spec.setContainers(containerList);
		template.setSpec(spec);

		deploymentSpec.setTemplate(template);
		deployment.setSpec(deploymentSpec);
		Deployment backData = kubeclinet.apps().deployments().inNamespace(DEFAULT_NAMESPACE).create(deployment);

		return backData;
	}
	
}
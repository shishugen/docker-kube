/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.vm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.work.SyncCreateVmThread;
import com.jeesite.modules.sys.utils.UserUtils;
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
		if(!"system".equals(UserUtils.getUser().getUserCode())){
			kubeVm.setUserId(UserUtils.getUser());
		}
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
		kubeVmService.save(kubeVm);
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
	@RequiresPermissions("kube:vm:kubeVm:edit")
	@RequestMapping(value = "saveContainer")
	@ResponseBody
	public String saveContainer(KubeVm kubeVm){
		kubeVmService.saveContainer(kubeVm);
		return renderResult(Global.TRUE, text("成功！"));
	}
	
}
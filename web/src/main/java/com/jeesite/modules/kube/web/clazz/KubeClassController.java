/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.clazz;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.service.clazz.KubeClassService;

/**
 * 班级Controller
 * @author SSg
 * @version 2019-12-22
 */
@Controller
@RequestMapping(value = "${adminPath}/kube/clazz/kubeClass")
public class KubeClassController extends BaseController {

	@Autowired
	private KubeClassService kubeClassService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public KubeClass get(String id, boolean isNewRecord) {
		return kubeClassService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:clazz:kubeClass:view")
	@RequestMapping(value = {"list", ""})
	public String list(KubeClass kubeClass, Model model) {
		model.addAttribute("kubeClass", kubeClass);
		return "modules/kube/clazz/kubeClassList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("kube:clazz:kubeClass:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<KubeClass> listData(KubeClass kubeClass, HttpServletRequest request, HttpServletResponse response) {
		kubeClass.setPage(new Page<>(request, response));
		Page<KubeClass> page = kubeClassService.findPage(kubeClass);
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("kube:clazz:kubeClass:view")
	@RequestMapping(value = "form")
	public String form(KubeClass kubeClass, Model model) {
		model.addAttribute("kubeClass", kubeClass);
		return "modules/kube/clazz/kubeClassForm";
	}

	/**
	 * 保存班级
	 */
	@RequiresPermissions("kube:clazz:kubeClass:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated KubeClass kubeClass) {
		kubeClassService.save(kubeClass);
		return renderResult(Global.TRUE, text("保存班级成功！"));
	}
	
	/**
	 * 删除班级
	 */
	@RequiresPermissions("kube:clazz:kubeClass:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(KubeClass kubeClass) {
		kubeClassService.delete(kubeClass);
		return renderResult(Global.TRUE, text("删除班级成功！"));
	}
	
}
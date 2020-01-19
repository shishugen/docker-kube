/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.userimages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesite.modules.sys.utils.UserUtils;
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
import com.jeesite.modules.kube.entity.userimages.KubeUserImages;
import com.jeesite.modules.kube.service.userimages.KubeUserImagesService;

/**
 * 用户镜像Controller
 * @author ssg
 * @version 2020-01-02
 */
@Controller
@RequestMapping(value = "${adminPath}/kube/userimages/kubeUserImages")
public class KubeUserImagesController extends BaseController {

	@Autowired
	private KubeUserImagesService kubeUserImagesService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public KubeUserImages get(String id, boolean isNewRecord) {
		return kubeUserImagesService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:userimages:kubeUserImages:view")
	@RequestMapping(value = {"list", ""})
	public String list(KubeUserImages kubeUserImages, Model model) {
		model.addAttribute("kubeUserImages", kubeUserImages);
		return "modules/kube/userimages/kubeUserImagesList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("kube:userimages:kubeUserImages:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<KubeUserImages> listData(KubeUserImages kubeUserImages, HttpServletRequest request, HttpServletResponse response) {
		kubeUserImages.setPage(new Page<>(request, response));
		if(!"system".equals(UserUtils.getUser().getUserCode())){
			kubeUserImages.setUserId(UserUtils.getUser().getId());
		}
		Page<KubeUserImages> page = kubeUserImagesService.findPage(kubeUserImages);
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("kube:userimages:kubeUserImages:view")
	@RequestMapping(value = "form")
	public String form(KubeUserImages kubeUserImages, Model model) {
		model.addAttribute("kubeUserImages", kubeUserImages);
		return "modules/kube/userimages/kubeUserImagesForm";
	}

	/**
	 * 保存用户镜像
	 */
	@RequiresPermissions("kube:userimages:kubeUserImages:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated KubeUserImages kubeUserImages) {
		kubeUserImagesService.save(kubeUserImages);
		return renderResult(Global.TRUE, text("保存用户镜像成功！"));
	}
	
	/**
	 * 删除用户镜像
	 */
	@RequiresPermissions("kube:userimages:kubeUserImages:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(KubeUserImages kubeUserImages) {
		kubeUserImagesService.delete(kubeUserImages);
		return renderResult(Global.TRUE, text("删除用户镜像成功！"));
	}

	/**
	 * 创建虚拟机镜像
	 */
	@RequiresPermissions("kube:userimages:kubeUserImages:edit")
	@RequestMapping(value = "createVm")
	@ResponseBody
	public String createVm(KubeUserImages kubeUserImages) {
		kubeUserImagesService.createVm(kubeUserImages);
		return renderResult(Global.TRUE, text("创建虚拟机镜像成功！"));
	}
	
}
/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.apply;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.course.KubeCourse;
import com.jeesite.modules.kube.service.clazz.KubeClassService;
import com.jeesite.modules.kube.service.course.KubeCourseService;
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
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.service.apply.KubeApplyService;

import java.util.List;

/**
 * 申请预约Controller
 * @author ssg
 * @version 2019-12-22
 */
@Controller
@RequestMapping(value = "${adminPath}/kube/apply/kubeApply")
public class KubeApplyController extends BaseController {

	@Autowired
	private KubeApplyService kubeApplyService;

	@Autowired
	private KubeClassService kubeClassService;

	@Autowired
	private KubeCourseService kubeCourseService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public KubeApply get(String id, boolean isNewRecord) {
		return kubeApplyService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:apply:kubeApply:view")
	@RequestMapping(value = {"list", ""})
	public String list(KubeApply kubeApply, Model model) {
		model.addAttribute("kubeApply", kubeApply);
		return "modules/kube/apply/kubeApplyList";
	}
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:apply:kubeApply:view")
	@RequestMapping(value = {"oneList"})
	public String oneList(KubeApply kubeApply, Model model) {
		model.addAttribute("kubeApply", kubeApply);
		return "modules/kube/apply/kubeApplyOneList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("kube:apply:kubeApply:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<KubeApply> listData(KubeApply kubeApply, HttpServletRequest request, HttpServletResponse response) {
		kubeApply.setPage(new Page<>(request, response));
		kubeApply.setType(KubeApply.ApplyTyep.CLASS_APPLY.ordinal());
		Page<KubeApply> page = kubeApplyService.findPage(kubeApply);
		return page;
	}
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("kube:apply:kubeApply:view")
	@RequestMapping(value = "oneListData")
	@ResponseBody
	public Page<KubeApply> oneListData(KubeApply kubeApply, HttpServletRequest request, HttpServletResponse response) {
		kubeApply.setPage(new Page<>(request, response));
		kubeApply.setType(KubeApply.ApplyTyep.ONE_APPLY.ordinal());
		Page<KubeApply> page = kubeApplyService.findPage(kubeApply);
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("kube:apply:kubeApply:view")
	@RequestMapping(value = "form")
	public String form(KubeApply kubeApply, Model model) {
		List<KubeCourse> courseList = kubeCourseService.findList(new KubeCourse());
		List<KubeClass> classList = kubeClassService.findList(new KubeClass());
		model.addAttribute("classList", classList);
		model.addAttribute("kubeApply", kubeApply);
		model.addAttribute("courseList", courseList);
		return "modules/kube/apply/kubeApplyForm";
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("kube:apply:kubeApply:view")
	@RequestMapping(value = "oneForm")
	public String oneForm(KubeApply kubeApply, Model model) {
		List<KubeCourse> courseList = kubeCourseService.findList(new KubeCourse());
		model.addAttribute("kubeApply", kubeApply);
		model.addAttribute("courseList", courseList);
		return "modules/kube/apply/kubeApplyOneForm";
	}

	/**
	 * 保存申请预约
	 */
	@RequiresPermissions("kube:apply:kubeApply:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated KubeApply kubeApply) {
		kubeApply.setType(KubeApply.ApplyTyep.CLASS_APPLY.ordinal());
		kubeApplyService.save(kubeApply);
		return renderResult(Global.TRUE, text("保存申请预约成功！"));
	}

		/**
	 * 保存申请预约
	 */
	@RequiresPermissions("kube:apply:kubeApply:edit")
	@PostMapping(value = "oneSave")
	@ResponseBody
	public String oneSave(@Validated KubeApply kubeApply) {
		kubeApply.setType(KubeApply.ApplyTyep.ONE_APPLY.ordinal());
		kubeApplyService.save(kubeApply);
		return renderResult(Global.TRUE, text("保存申请预约成功！"));
	}

	/**
	 * 删除申请预约
	 */
	@RequiresPermissions("kube:apply:kubeApply:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(KubeApply kubeApply) {
		kubeApplyService.delete(kubeApply);
		return renderResult(Global.TRUE, text("删除申请预约成功！"));
	}
	
}
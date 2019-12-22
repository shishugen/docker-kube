/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.course;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.service.courseimages.KubeCourseImagesService;
import com.jeesite.modules.kube.service.image.KubeImagesService;
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
import com.jeesite.modules.kube.entity.course.KubeCourse;
import com.jeesite.modules.kube.service.course.KubeCourseService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程Controller
 * @author ssg
 * @version 2019-12-22
 */
@Controller
@RequestMapping(value = "${adminPath}/kube/course/kubeCourse")
public class KubeCourseController extends BaseController {

	@Autowired
	private KubeCourseService kubeCourseService;

	@Autowired
	private KubeImagesService kubeImagesService;

	@Autowired
	private KubeCourseImagesService kubeCourseImagesService;
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public KubeCourse get(String id, boolean isNewRecord) {
		return kubeCourseService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:course:kubeCourse:view")
	@RequestMapping(value = {"list", ""})
	public String list(KubeCourse kubeCourse, Model model) {
		model.addAttribute("kubeCourse", kubeCourse);
		return "modules/kube/course/kubeCourseList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("kube:course:kubeCourse:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<KubeCourse> listData(KubeCourse kubeCourse, HttpServletRequest request, HttpServletResponse response) {
		kubeCourse.setPage(new Page<>(request, response));
		Page<KubeCourse> page = kubeCourseService.findPage(kubeCourse);
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("kube:course:kubeCourse:view")
	@RequestMapping(value = "form")
	public String form(KubeCourse kubeCourse, Model model) {
		List<KubeImages> imagesList = kubeImagesService.findList(new KubeImages());
		KubeCourseImages kubeCourseImages= new KubeCourseImages();
		kubeCourseImages.setCourseId(kubeCourse.getId());
		List<KubeCourseImages> list = kubeCourseImagesService.findList(kubeCourseImages);
		List<String> imagesIdsList = list.stream().map(a -> a.getImagesId()).distinct().collect(Collectors.toList());
		String imagesIds = StringUtils.join(imagesIdsList.toArray(), ",");
		kubeCourse.setImagesIds(imagesIds);
		model.addAttribute("kubeCourse", kubeCourse);
		model.addAttribute("imagesList", imagesList);
		return "modules/kube/course/kubeCourseForm";
	}

	/**
	 * 保存课程
	 */
	@RequiresPermissions("kube:course:kubeCourse:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated KubeCourse kubeCourse) {
		kubeCourseService.save(kubeCourse);
		return renderResult(Global.TRUE, text("保存课程成功！"));
	}
	
	/**
	 * 删除课程
	 */
	@RequiresPermissions("kube:course:kubeCourse:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(KubeCourse kubeCourse) {
		kubeCourseService.delete(kubeCourse);
		return renderResult(Global.TRUE, text("删除课程成功！"));
	}

}
/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.image;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.jeesite.modules.kube.core.DockerClinet;
import com.jeesite.modules.kube.utlis.DateUtlis;
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
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.service.image.KubeImagesService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 本地镜像Controller
 * @author ssg
 * @version 2019-12-21
 */
@Controller
@RequestMapping(value = "${adminPath}/kube/image/kubeImages")
public class KubeImagesController extends BaseController {


	private Map<String,List<KubeImages>> kubeImageCache= new HashMap<>();
	private static final String KUBE_IMAGE_KEY ="kube_image_key";

	@Autowired
	private KubeImagesService kubeImagesService;

	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public KubeImages get(String id, boolean isNewRecord) {
		return kubeImagesService.get(id, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("kube:image:kubeImages:view")
	@RequestMapping(value = {"list", ""})
	public String list(KubeImages kubeImages, Model model) {
		model.addAttribute("kubeImages", kubeImages);
		return "modules/kube/image/kubeImagesList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("kube:image:kubeImages:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<KubeImages> listData(KubeImages kubeImages, HttpServletRequest request, HttpServletResponse response) {
		kubeImages.setPage(new Page<>(request, response));
		Page<KubeImages> page = kubeImagesService.findPage(kubeImages);
		return page;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("kube:image:kubeImages:view")
	@RequestMapping(value = "form")
	public String form(KubeImages kubeImages, Model model) {
		DockerClient dockerClient = DockerClinet.getDockerClient();
		List<Image> imageList = dockerClient.listImagesCmd().exec();
		List<List<KubeImages>> collect = imageList.stream().filter((Image a) ->a.getRepoTags() != null &&
				a.getRepoTags().length > 0).map(a -> {
			List<KubeImages> collect1 = Arrays.stream(a.getRepoTags()).filter(b->!b.equals("<none>:<none>")).map(b -> {
						 KubeImages kubeImage = new KubeImages();
				         kubeImage.setRepositoryName(b);
				         kubeImage.setImageId(a.getId());
				         kubeImage.setImageCreateDate(new Date(a.getCreated()*1000));
				         kubeImage.setSize(a.getSize()/1024/1024);
						return kubeImage;
					}
			).collect(Collectors.toList());
			return collect1;
		}).collect(Collectors.toList());
/*		List<KubeImages> kubeImagesList = new ArrayList<>();
		for (List<KubeImages> list :collect){
			for (KubeImages s :list){
				kubeImagesList.add(s);
			}
		}*/
		List<KubeImages> kubeImagesList = collect.stream().findAny().get();
		model.addAttribute("kubeImages", kubeImages);
		model.addAttribute("imageList",kubeImagesList );
		kubeImageCache.put(KUBE_IMAGE_KEY,kubeImagesList);
		return "modules/kube/image/kubeImagesForm";
	}

	/**
	 * 保存本地镜像
	 */
	@RequiresPermissions("kube:image:kubeImages:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated KubeImages kubeImages) {
		List<KubeImages> list = kubeImageCache.get(KUBE_IMAGE_KEY);
		KubeImages any = list.stream().filter(a -> a.getRepositoryName().equals(kubeImages.getRepositoryName())).findAny().get();
		any.setImageName(kubeImages.getImageName());
		any.setCpu(kubeImages.getCpu());
		any.setMemory(kubeImages.getMemory());
		kubeImagesService.save(any);
		return renderResult(Global.TRUE, text("保存本地镜像成功！"));
	}
	
	/**
	 * 删除本地镜像
	 */
	@RequiresPermissions("kube:image:kubeImages:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(KubeImages kubeImages) {
		kubeImagesService.delete(kubeImages);
		return renderResult(Global.TRUE, text("删除本地镜像成功！"));
	}

}
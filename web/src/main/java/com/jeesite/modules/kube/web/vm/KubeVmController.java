/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.web.vm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.Files;
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
import org.springframework.web.bind.annotation.*;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
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
		if(!"system".equals(UserUtils.getUser().getUserCode())){
			kubeVm.setUserId(UserUtils.getUser());
		}
		List<KubeVm> list = kubeVmService.findList(kubeVm);
		model.addAttribute("kubeVmList", list);
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
	@RequestMapping("/uploadToFile")
	@ResponseBody
	public boolean uploadToUser(@RequestParam("file") MultipartFile file,@RequestParam("id")String id , @RequestParam("path")String path) throws IOException {
		KubeVm kubeVm = kubeVmService.get(id);
		File toFile = null;
		InputStream ins = null;
			ins = file.getInputStream();
			toFile = new File(file.getOriginalFilename());
			inputStreamToFile(ins, toFile);
			ins.close();
		Boolean upload = KubeClinet.getKubeclinet().pods().inNamespace(kubeVm.getNamespace()).withName(kubeVm.getVmName()).file(path+"/"+file.getOriginalFilename()).upload(toFile.toPath());
		return upload; //
	}

	@RequestMapping("/downloadFile")
	public void downloadFile(@RequestParam("file") String file,@RequestParam("id")String id ,@RequestParam("path")String path,HttpServletResponse response) throws IOException {
		KubeVm kubeVm = kubeVmService.get(id);
		if(kubeVm != null){
			InputStream read = KubeClinet.getKubeclinet().pods().inNamespace(kubeVm.getNamespace())
					.withName(kubeVm.getVmName())
					.file(path).read();
			byte[] bytes = readInputStream(read);
			OutputStream outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=" + new String(file.getBytes("gb2312"),"ISO8859-1"));
			outputStream.write(bytes);
			outputStream.close();
		}
	}

	public static  byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	//获取流文件
	private static void inputStreamToFile(InputStream ins, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	/*	Pod pod = KubeClinet.getKubeclinet().pods().inNamespace("class-test-user2")
				.withName("class-test-user2-0").get();
		System.out.println(pod);*/

		File tmpDir = new File("C:\\Users\\Administrator\\Documents\\Tencent Files\\576108653\\FileRecv\\MobileFile\\1.jpg");
		//File tmpDir = Files.createTempDir();
		Boolean upload = KubeClinet.getKubeclinet().pods().inNamespace("class-test-user2")
				.withName("class-test-user2-0").file("/home/test.jpg").upload(tmpDir.toPath());
		System.out.println(upload);
	}


}
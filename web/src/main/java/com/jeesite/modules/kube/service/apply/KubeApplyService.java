/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.apply;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.dao.clazz.KubeClassStudentsDao;
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.entity.vmlog.KubeVmLog;
import com.jeesite.modules.kube.service.courseimages.KubeCourseImagesService;
import com.jeesite.modules.kube.service.image.KubeImagesService;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.kube.service.vmlog.KubeVmLogService;
import com.jeesite.modules.kube.work.BindVmThread;
import com.jeesite.modules.kube.work.CreateVmThread;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.dao.apply.KubeApplyDao;

import static java.util.stream.Collectors.groupingBy;

/**
 * 申请预约Service
 * @author ssg
 * @version 2019-12-22
 */
@Service
@Transactional(readOnly=true)
public class KubeApplyService extends CrudService<KubeApplyDao, KubeApply> {

	@Autowired
    private KubeVmLogService kubeVmLogService;

	@Autowired
    private KubeVmService kubeVmService;
	@Autowired
    private KubeClassStudentsDao kubeClassStudentsDao;

	@Autowired
    private KubeCourseImagesService kubeCourseImagesService;
	@Autowired
    private KubeImagesService kubeImagesService;

	/**
	 * 获取单条数据
	 * @param kubeApply
	 * @return
	 */
	@Override
	public KubeApply get(KubeApply kubeApply) {
		return super.get(kubeApply);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeApply 查询条件
	 * @param
	 * @return
	 */
	@Override
	public Page<KubeApply> findPage(KubeApply kubeApply) {
		return super.findPage(kubeApply);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeApply kubeApply) {
		if (KubeApply.ApplyTyep.ONE_APPLY.ordinal()==kubeApply.getType()){
			kubeApply.setClassId("");
			kubeApply.setUserId(UserUtils.getUser().getId());
		}
		super.save(kubeApply);
	}
	
	/**
	 * 更新状态
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeApply kubeApply) {
		super.updateStatus(kubeApply);
	}
	
	/**
	 * 删除数据
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeApply kubeApply) {
		super.delete(kubeApply);
	}

	@Transactional(readOnly=false)
	public void bindingVm() {
		KubeApply kubeApply = new KubeApply();
		List<KubeApply> list = dao.findByStartDate(kubeApply);
		if(list == null || list.size() ==0){
			System.out.println("无申请预约=====》");
			return;
		}
		System.out.println("申请预约=====》"+list.size());
		list.forEach((apply->{
			String applyId = apply.getId();
			String classId = apply.getClassId();
			String courseId = apply.getCourseId();
			String userId = apply.getUserId();
			Integer type = apply.getType();
			//查找当前课程的镜像
			List<KubeCourseImages> courseImagesList = this.getImagesByCourseId(courseId);
			courseImagesList.forEach(image->{
				 KubeImages images = kubeImagesService.get(image.getImagesId());
				 String repositoryNam = images.getRepositoryName();
				String cpu = images.getCpu();
				String memory = images.getMemory();
				Integer number = courseImagesList.size();
				if (StringUtils.isNotBlank(classId)){
					long count = kubeClassStudentsDao.findCount(new KubeClassStudents(new KubeClass(classId)));
					number = Integer.valueOf("0" + count);
				}
				CreateVmThread createVmThread = new CreateVmThread(cpu,memory,repositoryNam,number,applyId);
				createVmThread.start();
			});
			KubeVmLog vmLog = new KubeVmLog(applyId, KubeVmLog.VmStatus.create.ordinal());
			kubeVmLogService.save(vmLog);
			BindVmThread bindVmThread = new BindVmThread(classId,userId,applyId,type);
			try {
				Thread.sleep(30000L); //等待30秒
				bindVmThread.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}));
	}


	/**
	 * @Author ssg
	 * @Description //TODO 释放资源
	 * @Date  2019/12/25 10:43
	 * @Param []
	 * @return void
	 **/
	@Transactional(readOnly=false)
	public void releaseVm() {
		List<KubeApply> applyList = dao.findByEndDate(new KubeApply());
		if(applyList == null || applyList.size() ==0){
			System.out.println("无释放资源=====》");
			return;
		}
		KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
		applyList.forEach(apply->{
			KubeVm vm = new KubeVm();
			vm.setApplyId(apply.getId());
			List<KubeVm> list = kubeVmService.findList(vm);
			AtomicBoolean flag = new AtomicBoolean(true);
			list.forEach(kubeVm -> {
				kubeVmService.delete(kubeVm);
				System.err.println("放资源==成功");
				if (flag.get()){
					if(kubeclinet.apps().deployments()
							.inNamespace(kubeVm.getNamespace())
							.withName(kubeVm.getDeploymentName()).delete()){
						System.err.println("放资源服务器资源----==成功");
						flag.set(false);
					}else{
						System.err.println("放资源服务器资源----==失败---");
					}
				}
				/*if(deploymentName == null || namespace == null
						|| !namespace.equals(kubeVm.getNamespace())
						|| !deploymentName.equals(kubeVm.getDeploymentName())){
					deploymentName = kubeVm.getDeploymentName();
					namespace = kubeVm.getNamespace();

				}*/
			});
			dao.delete(apply);
		});
	}

	public List<KubeCourseImages> getImagesByCourseId(String courseId){
		 return kubeCourseImagesService.findList(new KubeCourseImages(courseId,null));
	}

}
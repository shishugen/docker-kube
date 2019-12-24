/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.apply;

import java.util.Date;
import java.util.List;

import com.jeesite.modules.kube.dao.clazz.KubeClassStudentsDao;
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.entity.vmlog.KubeVmLog;
import com.jeesite.modules.kube.service.courseimages.KubeCourseImagesService;
import com.jeesite.modules.kube.service.image.KubeImagesService;
import com.jeesite.modules.kube.service.vmlog.KubeVmLogService;
import com.jeesite.modules.kube.utlis.SpringUtil;
import com.jeesite.modules.kube.work.BindVmThread;
import com.jeesite.modules.kube.work.CreateVmThread;
import com.jeesite.modules.sys.entity.Employee;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.utils.UserUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.dao.apply.KubeApplyDao;

import javax.validation.constraints.NotBlank;

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
	 * @param kubeApply.page 分页对象
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
	public void findByStartDate() {
		KubeApply kubeApply = new KubeApply();
		List<KubeApply> list = dao.findByStartDate(kubeApply);
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

	public void findBydneDate() {

	}

	public List<KubeCourseImages> getImagesByCourseId(String courseId){
		 return kubeCourseImagesService.findList(new KubeCourseImages(courseId,null));
	}

}
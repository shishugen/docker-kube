/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.apply;

import java.util.Date;
import java.util.List;

import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.entity.vmlog.KubeVmLog;
import com.jeesite.modules.kube.service.vmlog.KubeVmLogService;
import com.jeesite.modules.kube.work.CreateVmThread;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.dao.apply.KubeApplyDao;

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
		if ("2".equals(kubeApply.getType())){
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

	public void findByStartDate() {
		KubeApply kubeApply = new KubeApply();
		List<KubeApply> list = dao.findByStartDate(kubeApply);
		list.forEach((a->{
			KubeImages kubeImages = a.getKubeImages();
			CreateVmThread createVmThread = new CreateVmThread(kubeImages.getCpu(),kubeImages.getMemory(),kubeImages.getRepositoryName(),1);
			createVmThread.start();
			KubeVmLog vmLog = new KubeVmLog(a.getId(), KubeVmLog.VmStatus.create.ordinal());
			kubeVmLogService.save(vmLog);
		}));
		list.forEach(System.out::print);
		System.out.println(list);
	}

	public void findBydneDate() {

	}
}
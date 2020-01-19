/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.userimages;

import java.util.List;

import com.jeesite.modules.kube.core.KubeConfig;
import com.jeesite.modules.kube.work.CreateVmThread;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.userimages.KubeUserImages;
import com.jeesite.modules.kube.dao.userimages.KubeUserImagesDao;

/**
 * 用户镜像Service
 * @author ssg
 * @version 2020-01-02
 */
@Service
@Transactional(readOnly=true)
public class KubeUserImagesService extends CrudService<KubeUserImagesDao, KubeUserImages> {
	
	/**
	 * 获取单条数据
	 * @param kubeUserImages
	 * @return
	 */
	@Override
	public KubeUserImages get(KubeUserImages kubeUserImages) {
		return super.get(kubeUserImages);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeUserImages 查询条件
	 * @param kubeUserImages.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeUserImages> findPage(KubeUserImages kubeUserImages) {
		return super.findPage(kubeUserImages);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeUserImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeUserImages kubeUserImages) {
		super.save(kubeUserImages);
	}
	
	/**
	 * 更新状态
	 * @param kubeUserImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeUserImages kubeUserImages) {
		super.updateStatus(kubeUserImages);
	}
	
	/**
	 * 删除数据
	 * @param kubeUserImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeUserImages kubeUserImages) {
		super.delete(kubeUserImages);
	}

	@Transactional(readOnly=false)
    public void createVm(KubeUserImages kubeUserImages) {
		 String repositoryNam =KubeConfig.HARBOR_REGISTRY_URL+"/"+kubeUserImages.getRepository()+
				 ":"+kubeUserImages.getVersion();
		String cpu = "1";
		String memory = "1";
		Integer number = 1;
		Integer type = 1;
		String applyId = kubeUserImages.getId();
		String userId = kubeUserImages.getUserId();
		CreateVmThread createVmThread = new CreateVmThread(cpu,memory,repositoryNam,number,applyId,type,userId);
		createVmThread.start();

    }
}
/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.image;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.dao.image.KubeImagesDao;

/**
 * 本地镜像Service
 * @author ssg
 * @version 2019-12-21
 */
@Service
@Transactional(readOnly=true)
public class KubeImagesService extends CrudService<KubeImagesDao, KubeImages> {
	
	/**
	 * 获取单条数据
	 * @param kubeImages
	 * @return
	 */
	@Override
	public KubeImages get(KubeImages kubeImages) {
		return super.get(kubeImages);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeImages 查询条件
	 * @param kubeImages.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeImages> findPage(KubeImages kubeImages) {
		return super.findPage(kubeImages);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeImages kubeImages) {
		super.save(kubeImages);
	}
	
	/**
	 * 更新状态
	 * @param kubeImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeImages kubeImages) {
		super.updateStatus(kubeImages);
	}
	
	/**
	 * 删除数据
	 * @param kubeImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeImages kubeImages) {
		super.delete(kubeImages);
	}
	
}
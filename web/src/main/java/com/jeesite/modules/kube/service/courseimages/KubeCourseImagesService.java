/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.courseimages;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.dao.courseimages.KubeCourseImagesDao;

/**
 * 课程与镜像绑定Service
 * @author ssg
 * @version 2019-12-22
 */
@Service
@Transactional(readOnly=true)
public class KubeCourseImagesService extends CrudService<KubeCourseImagesDao, KubeCourseImages> {
	
	/**
	 * 获取单条数据
	 * @param kubeCourseImages
	 * @return
	 */
	@Override
	public KubeCourseImages get(KubeCourseImages kubeCourseImages) {
		return super.get(kubeCourseImages);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeCourseImages 查询条件
	 * @param kubeCourseImages.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeCourseImages> findPage(KubeCourseImages kubeCourseImages) {
		return super.findPage(kubeCourseImages);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeCourseImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeCourseImages kubeCourseImages) {
		super.save(kubeCourseImages);
	}
	
	/**
	 * 更新状态
	 * @param kubeCourseImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeCourseImages kubeCourseImages) {
		super.updateStatus(kubeCourseImages);
	}
	
	/**
	 * 删除数据
	 * @param kubeCourseImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeCourseImages kubeCourseImages) {
		super.delete(kubeCourseImages);
	}
	
}
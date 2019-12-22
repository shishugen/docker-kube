/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.course;

import java.util.Arrays;
import java.util.List;

import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.service.courseimages.KubeCourseImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.course.KubeCourse;
import com.jeesite.modules.kube.dao.course.KubeCourseDao;

/**
 * 课程Service
 * @author ssg
 * @version 2019-12-22
 */
@Service
@Transactional(readOnly=true)
public class KubeCourseService extends CrudService<KubeCourseDao, KubeCourse> {

	@Autowired
	private KubeCourseImagesService kubeCourseImagesService;
	/**
	 * 获取单条数据
	 * @param kubeCourse
	 * @return
	 */
	@Override
	public KubeCourse get(KubeCourse kubeCourse) {
		return super.get(kubeCourse);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeCourse 查询条件
	 * @param kubeCourse.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeCourse> findPage(KubeCourse kubeCourse) {
		return super.findPage(kubeCourse);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeCourse
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeCourse kubeCourse) {
		super.save(kubeCourse);
		String courseId = kubeCourse.getId();
		if(StringUtils.isNoneBlank(kubeCourse.getImagesIds())){
			String[] imageIds = kubeCourse.getImagesIds().split(",");
			Arrays.stream(imageIds).forEach(a->{
				KubeCourseImages kubeCourseImages = new KubeCourseImages(courseId,a);
				kubeCourseImagesService.insert(kubeCourseImages);
			});
		}
	}
	
	/**
	 * 更新状态
	 * @param kubeCourse
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeCourse kubeCourse) {
		super.updateStatus(kubeCourse);
	}
	
	/**
	 * 删除数据
	 * @param kubeCourse
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeCourse kubeCourse) {
		super.delete(kubeCourse);
	}
	
}
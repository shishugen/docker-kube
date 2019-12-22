/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.course;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.course.KubeCourse;

/**
 * 课程DAO接口
 * @author ssg
 * @version 2019-12-22
 */
@MyBatisDao
public interface KubeCourseDao extends CrudDao<KubeCourse> {
	
}
/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.courseimages;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;

/**
 * 课程与镜像绑定DAO接口
 * @author ssg
 * @version 2019-12-22
 */
@MyBatisDao
public interface KubeCourseImagesDao extends CrudDao<KubeCourseImages> {
	
}
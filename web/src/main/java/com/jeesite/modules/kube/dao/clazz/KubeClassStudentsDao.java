/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.clazz;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;

/**
 * 班级DAO接口
 * @author SSg
 * @version 2019-12-22
 */
@MyBatisDao
public interface KubeClassStudentsDao extends CrudDao<KubeClassStudents> {
	
}
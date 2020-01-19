/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.userimages;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.userimages.KubeUserImages;

/**
 * 用户镜像DAO接口
 * @author ssg
 * @version 2020-01-02
 */
@MyBatisDao
public interface KubeUserImagesDao extends CrudDao<KubeUserImages> {
	
}
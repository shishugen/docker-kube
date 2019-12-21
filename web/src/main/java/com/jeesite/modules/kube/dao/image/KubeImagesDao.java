/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.image;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.image.KubeImages;

/**
 * 本地镜像DAO接口
 * @author ssg
 * @version 2019-12-21
 */
@MyBatisDao
public interface KubeImagesDao extends CrudDao<KubeImages> {
	
}
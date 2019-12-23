/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.vmlog;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.vmlog.KubeVmLog;

/**
 * 虚拟机日志DAO接口
 * @author ssg
 * @version 2019-12-23
 */
@MyBatisDao
public interface KubeVmLogDao extends CrudDao<KubeVmLog> {
	
}
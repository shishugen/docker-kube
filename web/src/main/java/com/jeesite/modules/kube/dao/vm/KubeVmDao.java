/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.vm;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.vm.KubeVm;

import java.util.List;

/**
 * 虚拟机DAO接口
 * @author ssg
 * @version 2019-12-23
 */
@MyBatisDao
public interface KubeVmDao extends CrudDao<KubeVm> {

    List<KubeVm> findApplyIdNotBind(KubeVm kubeVm);
}
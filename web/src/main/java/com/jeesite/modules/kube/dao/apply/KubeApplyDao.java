/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.dao.apply;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.kube.entity.apply.KubeApply;

import java.util.List;

/**
 * 申请预约DAO接口
 * @author ssg
 * @version 2019-12-22
 */
@MyBatisDao
public interface KubeApplyDao extends CrudDao<KubeApply> {

    List<KubeApply> findByStartDate(KubeApply kubeApply);

    List<KubeApply> findByEndDate(KubeApply kubeApply);
}
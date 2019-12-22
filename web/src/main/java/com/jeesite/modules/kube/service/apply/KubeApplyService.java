/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.apply;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.dao.apply.KubeApplyDao;

/**
 * 申请预约Service
 * @author ssg
 * @version 2019-12-22
 */
@Service
@Transactional(readOnly=true)
public class KubeApplyService extends CrudService<KubeApplyDao, KubeApply> {
	
	/**
	 * 获取单条数据
	 * @param kubeApply
	 * @return
	 */
	@Override
	public KubeApply get(KubeApply kubeApply) {
		return super.get(kubeApply);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeApply 查询条件
	 * @param kubeApply.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeApply> findPage(KubeApply kubeApply) {
		return super.findPage(kubeApply);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeApply kubeApply) {
		super.save(kubeApply);
	}
	
	/**
	 * 更新状态
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeApply kubeApply) {
		super.updateStatus(kubeApply);
	}
	
	/**
	 * 删除数据
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeApply kubeApply) {
		super.delete(kubeApply);
	}
	
}
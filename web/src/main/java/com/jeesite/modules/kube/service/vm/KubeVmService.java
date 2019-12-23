/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.vm;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.dao.vm.KubeVmDao;

/**
 * 虚拟机Service
 * @author ssg
 * @version 2019-12-23
 */
@Service
@Transactional(readOnly=true)
public class KubeVmService extends CrudService<KubeVmDao, KubeVm> {
	
	/**
	 * 获取单条数据
	 * @param kubeVm
	 * @return
	 */
	@Override
	public KubeVm get(KubeVm kubeVm) {
		return super.get(kubeVm);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeVm 查询条件
	 * @param kubeVm.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeVm> findPage(KubeVm kubeVm) {
		return super.findPage(kubeVm);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeVm
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeVm kubeVm) {
		super.save(kubeVm);
	}
	
	/**
	 * 更新状态
	 * @param kubeVm
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeVm kubeVm) {
		super.updateStatus(kubeVm);
	}
	
	/**
	 * 删除数据
	 * @param kubeVm
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeVm kubeVm) {
		super.delete(kubeVm);
	}
	
}
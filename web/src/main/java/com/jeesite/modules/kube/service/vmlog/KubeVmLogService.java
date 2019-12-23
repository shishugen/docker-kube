/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.vmlog;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.vmlog.KubeVmLog;
import com.jeesite.modules.kube.dao.vmlog.KubeVmLogDao;

/**
 * 虚拟机日志Service
 * @author ssg
 * @version 2019-12-23
 */
@Service
@Transactional(readOnly=true)
public class KubeVmLogService extends CrudService<KubeVmLogDao, KubeVmLog> {
	
	/**
	 * 获取单条数据
	 * @param kubeVmLog
	 * @return
	 */
	@Override
	public KubeVmLog get(KubeVmLog kubeVmLog) {
		return super.get(kubeVmLog);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeVmLog 查询条件
	 * @param kubeVmLog.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeVmLog> findPage(KubeVmLog kubeVmLog) {
		return super.findPage(kubeVmLog);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeVmLog
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeVmLog kubeVmLog) {
		super.save(kubeVmLog);
	}
	
	/**
	 * 更新状态
	 * @param kubeVmLog
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeVmLog kubeVmLog) {
		super.updateStatus(kubeVmLog);
	}
	
	/**
	 * 删除数据
	 * @param kubeVmLog
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeVmLog kubeVmLog) {
		super.delete(kubeVmLog);
	}
	
}
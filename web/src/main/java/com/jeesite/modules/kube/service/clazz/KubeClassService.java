/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.clazz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.dao.clazz.KubeClassDao;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.dao.clazz.KubeClassStudentsDao;

/**
 * 班级Service
 * @author SSg
 * @version 2019-12-22
 */
@Service
@Transactional(readOnly=true)
public class KubeClassService extends CrudService<KubeClassDao, KubeClass> {
	
	@Autowired
	private KubeClassStudentsDao kubeClassStudentsDao;
	
	/**
	 * 获取单条数据
	 * @param kubeClass
	 * @return
	 */
	@Override
	public KubeClass get(KubeClass kubeClass) {
		KubeClass entity = super.get(kubeClass);
		if (entity != null){
			KubeClassStudents kubeClassStudents = new KubeClassStudents(entity);
			kubeClassStudents.setStatus(KubeClassStudents.STATUS_NORMAL);
			entity.setKubeClassStudentsList(kubeClassStudentsDao.findList(kubeClassStudents));
		}
		return entity;
	}
	
	/**
	 * 查询分页数据
	 * @param kubeClass 查询条件
	 * @param kubeClass.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeClass> findPage(KubeClass kubeClass) {
		return super.findPage(kubeClass);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeClass
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeClass kubeClass) {
		super.save(kubeClass);
		// 保存 KubeClass子表
		for (KubeClassStudents kubeClassStudents : kubeClass.getKubeClassStudentsList()){
			if (!KubeClassStudents.STATUS_DELETE.equals(kubeClassStudents.getStatus())){
				kubeClassStudents.setClassId(kubeClass);
				if (kubeClassStudents.getIsNewRecord()){
					kubeClassStudentsDao.insert(kubeClassStudents);
				}else{
					kubeClassStudentsDao.update(kubeClassStudents);
				}
			}else{
				kubeClassStudentsDao.delete(kubeClassStudents);
			}
		}
	}
	
	/**
	 * 更新状态
	 * @param kubeClass
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeClass kubeClass) {
		super.updateStatus(kubeClass);
	}
	
	/**
	 * 删除数据
	 * @param kubeClass
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeClass kubeClass) {
		super.delete(kubeClass);
		KubeClassStudents kubeClassStudents = new KubeClassStudents();
		kubeClassStudents.setClassId(kubeClass);
		kubeClassStudentsDao.deleteByEntity(kubeClassStudents);
	}
	
}
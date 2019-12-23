/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.vmlog;

import org.hibernate.validator.constraints.Length;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 虚拟机日志Entity
 * @author ssg
 * @version 2019-12-23
 */
@Table(name="kube_vm_log", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="apply_id", attrName="applyId", label="申请ID"),
		@Column(name="vm_status", attrName="vmStatus", label="1", comment="1:创建，2:销毁"),
		@Column(includeEntity=DataEntity.class),
	}, orderBy="a.update_date DESC"
)
public class KubeVmLog extends DataEntity<KubeVmLog> {

	public enum VmStatus{
		create,destroy;
	}
	
	private static final long serialVersionUID = 1L;
	private String applyId;		// 申请ID
	private Integer vmStatus;		// 1:创建，2:销毁
	
	public KubeVmLog() {
		this(null);
	}

	public KubeVmLog(String applyId, Integer vmStatus) {
		this.applyId = applyId;
		this.vmStatus = vmStatus;
	}

	public KubeVmLog(String id){
		super(id);
	}


	
	@Length(min=0, max=64, message="申请ID长度不能超过 64 个字符")
	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}
	
	@Length(min=0, max=1, message="1长度不能超过 1 个字符")
	public Integer getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(Integer vmStatus) {
		this.vmStatus = vmStatus;
	}
	
}
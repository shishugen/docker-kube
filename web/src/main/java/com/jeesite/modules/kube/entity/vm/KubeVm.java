/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.vm;

import org.hibernate.validator.constraints.Length;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.common.mybatis.annotation.JoinTable;
import com.jeesite.common.mybatis.annotation.JoinTable.Type;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 虚拟机Entity
 * @author ssg
 * @version 2019-12-23
 */
@Table(name="kube_vm", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="vm_name", attrName="vmName", label="虚拟机名称", queryType=QueryType.LIKE),
		@Column(name="vm_status", attrName="vmStatus", label="虚拟机状态"),
		@Column(name="vm_ip", attrName="vmIp", label="虚拟机IP"),
		@Column(name="host_ip", attrName="hostIp", label="虚拟机服务器IP"),
		@Column(name="namespace", attrName="namespace", label="命名空间"),
		@Column(name="deployment_name", attrName="deploymentName", label="deployment名", queryType=QueryType.LIKE),
		@Column(name="images_id", attrName="imagesId", label="镜像表ID", isQuery=false),
		@Column(name="user_id", attrName="userId.userCode", label="用户"),
		@Column(name="vm_start_date", attrName="vmStartDate", label="虚拟机启动时间"),
		@Column(includeEntity=DataEntity.class),
	}, joinTable={

		@JoinTable(type=Type.LEFT_JOIN, entity=User.class, attrName="userId", alias="u9",
			on="u9.user_code = a.user_id", columns={
				@Column(name="user_code", label="用户编码", isPK=true),
				@Column(name="user_name", label="用户名称", isQuery=false),
		}),
	}, orderBy="a.update_date DESC"
)
public class KubeVm extends DataEntity<KubeVm> {

	public static final  String VM_STATUS_RUNNING ="Running";
	public enum VmStatus{
		Running;
	}
	
	private static final long serialVersionUID = 1L;
	private String vmName;		// 虚拟机名称
	private String vmStatus;		// 虚拟机状态
	private String vmIp;		// 虚拟机IP
	private String hostIp;		// 虚拟机服务器IP
	private String namespace;		// 命名空间
	private String deploymentName;		// deployment名
	private String imagesId;		// 镜像表ID
	private User userId;		// 用户
	private Date vmStartDate;		// 虚拟机启动时间
	
	public KubeVm(String deploymentName, String namespace) {
		this.deploymentName =deploymentName;
		this.namespace =namespace;

	}

	public KubeVm(){
	}

	public KubeVm(String id){
		super(id);
	}

	@Length(min=0, max=200, message="虚拟机名称长度不能超过 200 个字符")
	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	@Length(min=0, max=1, message="虚拟机状态长度不能超过 1 个字符")
	public String getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(String vmStatus) {
		this.vmStatus = vmStatus;
	}
	
	@Length(min=0, max=18, message="虚拟机IP长度不能超过 18 个字符")
	public String getVmIp() {
		return vmIp;
	}

	public void setVmIp(String vmIp) {
		this.vmIp = vmIp;
	}
	
	@Length(min=0, max=18, message="虚拟机服务器IP长度不能超过 18 个字符")
	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	
	@Length(min=0, max=200, message="命名空间长度不能超过 200 个字符")
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	@Length(min=0, max=100, message="deployment名长度不能超过 100 个字符")
	public String getDeploymentName() {
		return deploymentName;
	}

	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	
	@Length(min=0, max=64, message="镜像表ID长度不能超过 64 个字符")
	public String getImagesId() {
		return imagesId;
	}

	public void setImagesId(String imagesId) {
		this.imagesId = imagesId;
	}
	
	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getVmStartDate() {
		return vmStartDate;
	}

	public void setVmStartDate(Date vmStartDate) {
		this.vmStartDate = vmStartDate;
	}
	
}
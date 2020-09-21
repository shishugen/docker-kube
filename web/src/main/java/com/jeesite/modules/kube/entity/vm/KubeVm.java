/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.vm;

import org.hibernate.validator.constraints.Length;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.common.mybatis.annotation.JoinTable;
import com.jeesite.common.mybatis.annotation.JoinTable.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
		@Column(name="type", attrName="type", label="0:生成，1:保存容器生成"),
		@Column(name="container_id", attrName="containerId", label="containerID"),
		@Column(name="vm_ip", attrName="vmIp", label="虚拟机IP"),
		@Column(name="host_ip", attrName="hostIp", label="虚拟机服务器IP"),
		@Column(name="namespace", attrName="namespace", label="命名空间"),
		@Column(name="deployment_name", attrName="deploymentName", label="deployment名", queryType=QueryType.LIKE),
		@Column(name="images_id", attrName="imagesId", label="镜像表ID", isQuery=false),
		@Column(name="user_id", attrName="userId.userCode", label="用户"),
		@Column(name="vm_start_date", attrName="vmStartDate", label="虚拟机启动时间"),
		@Column(name="apply_id", attrName="applyId", label="申请ID"),
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

	/***
	 *      状态　　　　	             描述　　　　　　
	 * CrashLoopBackOff　　　　	容器退出，kubelet正在将它重启
	 * InvalidImageName	    无法解析镜像名称
	 * ImageInspectError	无法校验镜像
	 * ErrImageNeverPull	策略禁止拉取镜像
	 * ImagePullBackOff	    正在重试拉取
	 * RegistryUnavailable	 连接不到镜像中心
	 * ErrImagePull	          通用的拉取镜像出错
	 * CreateContainerConfigError	 不能创建kubelet使用的容器配置
	 * CreateContainerError      	创建容器失败
	 * m.internalLifecycle.PreStartContainer	执行hook报错
	 * RunContainerError	 启动容器失败
	 * PostStartHookError	 执行hook报错
	 * ContainersNotInitialized	  容器没有初始化完毕
	 * ContainersNotRead	  容器没有准备完毕
	 * ContainerCreating	  容器创建中
	 * PodInitializing	      pod 初始化中
	 * DockerDaemonNotReady	   docker还没有完全启动
	 * NetworkPluginNotReady	网络插件还没有完全启动
	 */



	public static final  String VM_STATUS_RUNNING ="Running";  //运行中
	public static final  String VM_STATUS_PENDING ="Pending";  //等待中
	public static final  String VM_STATUS_SUCCEEDED ="Succeeded";  //正常终止
	public static final  String VM_STATUS_FAILED ="Failed";  //异常停止
	public static final  String VM_STATUS_UNKONWN ="Unkonwn";  //未知状态


	public static final  String VM_STATUS_CRASHLOOPBACKOFF ="CrashLoopBackOff";
	public static final  String VM_STATUS_INVALIDIMAGENAME ="InvalidImageName";
	public static final  String VM_STATUS_IMAGEINSPECTERROR ="ImageInspectError";
	public static final  String VM_STATUS_ERRIMAGENEVERPULL ="ErrImageNeverPull";
	public static final  String VM_STATUS_IMAGEPULLBACKOFF ="ImagePullBackOff";
	public static final  String VM_STATUS_REGISTRYUNAVAILABLE ="RegistryUnavailable";
	public static final  String VM_STATUS_ERRIMAGEPULL ="ErrImagePull";
	public static final  String VM_STATUS_CREATECONTAINERCONFIGERROR ="CreateContainerConfigError";
	public static final  String VM_STATUS_CREATE_CONTAINER_ERROR ="CreateContainerError";
	public static final  String VM_STATUS_RUN_CONTAINER_ERROR ="RunContainerError";
	public static final  String VM_STATUS_POST_START_HOOK_ERROR ="PostStartHookError";
	public static final  String VM_STATUS_CONTAINERS_NOT_INITIALIZED ="ContainersNotInitialized";
	public static final  String VM_STATUS_CONTAINER_CREATING ="ContainerCreating";
	public static final  String VM_STATUS_POD_INITIALIZING ="PodInitializing";
	public static final  String VM_STATUS_DOCKER_DAEMON_NOT_READY ="DockerDaemonNotReady";
	public static final  String VM_STATUS_NETWORK_PLUGIN_NOT_READY ="NetworkPluginNotReady";

	public static final Map<String,Integer>  VM_STATUS_MAP = new HashMap<>();
	{
		VM_STATUS_MAP.put(VM_STATUS_RUNNING,0);
		VM_STATUS_MAP.put(VM_STATUS_PENDING,1);
		VM_STATUS_MAP.put(VM_STATUS_SUCCEEDED,2);
		VM_STATUS_MAP.put(VM_STATUS_FAILED,3);
		VM_STATUS_MAP.put(VM_STATUS_UNKONWN,4);

		VM_STATUS_MAP.put(VM_STATUS_CRASHLOOPBACKOFF,5);
		VM_STATUS_MAP.put(VM_STATUS_INVALIDIMAGENAME,6);
		VM_STATUS_MAP.put(VM_STATUS_IMAGEINSPECTERROR,7);
		VM_STATUS_MAP.put(VM_STATUS_ERRIMAGENEVERPULL,8);
		VM_STATUS_MAP.put(VM_STATUS_IMAGEPULLBACKOFF,9);
		VM_STATUS_MAP.put(VM_STATUS_REGISTRYUNAVAILABLE,10);
		VM_STATUS_MAP.put(VM_STATUS_CREATECONTAINERCONFIGERROR,11);
		VM_STATUS_MAP.put(VM_STATUS_ERRIMAGEPULL,12);
		VM_STATUS_MAP.put(VM_STATUS_CREATE_CONTAINER_ERROR,13);
		VM_STATUS_MAP.put(VM_STATUS_RUN_CONTAINER_ERROR,14);
		VM_STATUS_MAP.put(VM_STATUS_POST_START_HOOK_ERROR,15);
		VM_STATUS_MAP.put(VM_STATUS_CONTAINERS_NOT_INITIALIZED,16);
		VM_STATUS_MAP.put(VM_STATUS_CONTAINER_CREATING,17);
		VM_STATUS_MAP.put(VM_STATUS_POD_INITIALIZING,18);
		VM_STATUS_MAP.put(VM_STATUS_DOCKER_DAEMON_NOT_READY,19);
		VM_STATUS_MAP.put(VM_STATUS_NETWORK_PLUGIN_NOT_READY,20);

	}



	public static final  String VM_STATUS_ ="Running";



	public enum VmStatus{
		Running;
	}

	private static final long serialVersionUID = 1L;
	private String vmName;		// 虚拟机名称
	private Integer vmStatus;		// 虚拟机状态
	private Integer type;		// 0:生成，1:保存容器生成
	private String vmIp;		// 虚拟机IP
	private String hostIp;		// 虚拟机服务器IP
	private String namespace;		// 命名空间
	private String deploymentName;		// deployment名
	private String imagesId;		// 镜像表ID
	private User userId;		// 用户
	private Date vmStartDate;		// 虚拟机启动时间
	private String applyId;
	private String containerId; //

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
	public Integer getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(Integer vmStatus) {
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

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}


	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "KubeVm{" +
				"vmName='" + vmName + '\'' +
				", vmStatus=" + vmStatus +
				", type=" + type +
				", vmIp='" + vmIp + '\'' +
				", hostIp='" + hostIp + '\'' +
				", namespace='" + namespace + '\'' +
				", deploymentName='" + deploymentName + '\'' +
				", imagesId='" + imagesId + '\'' +
				", userId=" + userId +
				", vmStartDate=" + vmStartDate +
				", applyId='" + applyId + '\'' +
				", containerId='" + containerId + '\'' +
				'}';
	}
}
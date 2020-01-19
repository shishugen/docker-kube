/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.userimages;

import org.hibernate.validator.constraints.Length;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 用户镜像Entity
 * @author ssg
 * @version 2020-01-02
 */
@Table(name="kube_user_images", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="user_id", attrName="userId", label="用户ID"),
		@Column(name="work_node_ip", attrName="workNodeIp", label="节点IP"),
		@Column(name="repository_host", attrName="repositoryHost", label="仓库主机"),
		@Column(name="repository", attrName="repository", label="仓库名"),
		@Column(name="version", attrName="version", label="版本", isQuery=false),
		@Column(name="course_id", attrName="courseId", label="课程ID", isQuery=false),
		@Column(name="images_id", attrName="imagesId", label="docker_images_id", isQuery=false),
		@Column(name="type", attrName="type", label="类型", comment="类型(0:上课，1：个人)"),
		@Column(includeEntity=DataEntity.class),
	}, orderBy="a.update_date DESC"
)
public class KubeUserImages extends DataEntity<KubeUserImages> {
	
	private static final long serialVersionUID = 1L;
	private String userId;		// 用户ID
	private String workNodeIp;		// 节点IP
	private String repositoryHost;		// 仓库主机
	private String repository;		// 仓库名
	private double version;		// 版本
	private String courseId;		// 课程ID
	private String imagesId;		// docker_images_id
	private Integer type;		// 类型(0:上课，1：个人)
	
	public KubeUserImages() {
		this(null);
	}

	public KubeUserImages(String id){
		super(id);
	}
	
	@Length(min=0, max=64, message="用户ID长度不能超过 64 个字符")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Length(min=0, max=64, message="节点IP长度不能超过 64 个字符")
	public String getWorkNodeIp() {
		return workNodeIp;
	}

	public void setWorkNodeIp(String workNodeIp) {
		this.workNodeIp = workNodeIp;
	}
	
	@Length(min=0, max=64, message="仓库主机长度不能超过 64 个字符")
	public String getRepositoryHost() {
		return repositoryHost;
	}

	public void setRepositoryHost(String repositoryHost) {
		this.repositoryHost = repositoryHost;
	}
	
	@Length(min=0, max=64, message="仓库名长度不能超过 64 个字符")
	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}
	
	@Length(min=0, max=20, message="版本长度不能超过 20 个字符")
	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}
	
	@Length(min=0, max=64, message="课程ID长度不能超过 64 个字符")
	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	@Length(min=0, max=200, message="docker_images_id长度不能超过 200 个字符")
	public String getImagesId() {
		return imagesId;
	}

	public void setImagesId(String imagesId) {
		this.imagesId = imagesId;
	}
	
	@Length(min=0, max=1, message="类型长度不能超过 1 个字符")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
}
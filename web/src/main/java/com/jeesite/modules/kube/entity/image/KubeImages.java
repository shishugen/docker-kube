/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.image;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.jeesite.common.mybatis.annotation.JoinTable;
import com.jeesite.common.mybatis.annotation.JoinTable.Type;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 本地镜像Entity
 * @author ssg
 * @version 2019-12-21
 */
@Table(name="kube_images", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="image_id", attrName="imageId", label="镜像ID", isQuery=false),
		@Column(name="image_name", attrName="imageName", label="镜像自定义名", isQuery=false),
		@Column(name="repository_name", attrName="repositoryName", label="仓库名", queryType=QueryType.LIKE),
		@Column(name="tag", attrName="tag", label="版本"),
		@Column(name="size", attrName="size", label="大小M", isQuery=false),
		@Column(name="image_create_date", attrName="imageCreateDate", label="镜像创建时间", isQuery=false),
		@Column(includeEntity=DataEntity.class),
	}, orderBy="a.update_date DESC"
)
public class KubeImages extends DataEntity<KubeImages> {
	
	private static final long serialVersionUID = 1L;
	private String imageId;		// 镜像ID
	private String imageName;		// 镜像自定义名
	private String repositoryName;		// 仓库名
	private String tag;		// 版本
	private Long size;		// 大小M
	private Date imageCreateDate;		// 镜像创建时间
	private KubeImages kubeImages;

	
	public KubeImages() {
		this(null);
	}

	public KubeImages(String id){
		super(id);
	}
	
	//otBlank(message="镜像ID不能为空")
	@Length(min=0, max=120, message="镜像ID长度不能超过 120 个字符")
	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	@NotBlank(message="镜像名不能为空")
	@Length(min=0, max=64, message="镜像名长度不能超过 120 个字符")
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	//otBlank(message="仓库名不能为空")
	@Length(min=0, max=200, message="仓库名长度不能超过 200 个字符")
	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	
	//@NotBlank(message="版本不能为空")
	@Length(min=0, max=10, message="版本长度不能超过 10 个字符")
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getImageCreateDate() {
		return imageCreateDate;
	}

	public void setImageCreateDate(Date imageCreateDate) {
		this.imageCreateDate = imageCreateDate;
	}

	public KubeImages getKubeImages() {
		return kubeImages;
	}

	public void setKubeImages(KubeImages kubeImages) {
		this.kubeImages = kubeImages;
	}
}
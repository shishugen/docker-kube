/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.courseimages;

import javax.validation.constraints.NotBlank;

import com.jeesite.common.mybatis.annotation.JoinTable;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 课程与镜像绑定Entity
 * @author ssg
 * @version 2019-12-22
 */
@Table(name="kube_course_images", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="course_id", attrName="courseId", label="课程ID"),
		@Column(name="images_id", attrName="imagesId", label="镜像ID"),
		@Column(includeEntity=DataEntity.class),
			}, joinTable={
		@JoinTable(type=JoinTable.Type.LEFT_JOIN, entity=KubeImages.class, attrName="images", alias="i9",
				on="i9.id = a.images_id", columns={
				@Column(name="repository_name", label="仓库名" ),
				@Column(name="cpu", label="cpu" ),
				@Column(name="memory", label="memory" ),
		})}, orderBy="a.update_date DESC"
)
public class KubeCourseImages extends DataEntity<KubeCourseImages> {
	
	private static final long serialVersionUID = 1L;
	private String courseId;		// 课程ID
	private String imagesId;		// 镜像ID

	private KubeImages images;		// 镜像


	public KubeCourseImages() {
		this(null);
	}
	public KubeCourseImages(String courseId,String imagesId) {
	      this.courseId = courseId;
	      this.imagesId = imagesId;
	}

	public KubeCourseImages(String id){
		super(id);
	}
	
	@NotBlank(message="课程ID不能为空")
	@Length(min=0, max=32, message="课程ID长度不能超过 32 个字符")
	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	@NotBlank(message="镜像ID不能为空")
	@Length(min=0, max=32, message="镜像ID长度不能超过 32 个字符")
	public String getImagesId() {
		return imagesId;
	}

	public void setImagesId(String imagesId) {
		this.imagesId = imagesId;
	}

	public KubeImages getImages() {
		return images;
	}

	public void setImages(KubeImages images) {
		this.images = images;
	}
}
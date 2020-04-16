/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.course;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 课程Entity
 * @author ssg
 * @version 2019-12-22
 */
@Table(name="kube_course", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="code", attrName="code", label="编码"),
		@Column(name="course_name", attrName="courseName", label="课程名", queryType=QueryType.LIKE),
		@Column(name="course_type", attrName="courseType", label="课程类型"),
		@Column(name="content", attrName="content", label="课程内容", isQuery=false),
		@Column(includeEntity=DataEntity.class),
	}, orderBy="a.update_date DESC"
)
public class KubeCourse extends DataEntity<KubeCourse> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 编码
	private String courseName;		// 课程名
	private String courseType;		// 课程类型
	private String content;		// 课程内容
	private String imagesIds; //镜像ID
	
	public KubeCourse() {
		this(null);
	}

	public KubeCourse(String id){
		super(id);
	}
	
	@NotBlank(message="课程名不能为空")
	@Length(min=0, max=64, message="课程名长度不能超过 64 个字符")
	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
	@NotBlank(message="课程类型不能为空")
	@Length(min=0, max=1, message="课程类型长度不能超过 1 个字符")
	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
	
	@NotBlank(message="课程内容不能为空")
	@Length(min=0, max=200, message="课程内容长度不能超过 200 个字符")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImagesIds() {
		return imagesIds;
	}

	public void setImagesIds(String imagesIds) {
		this.imagesIds = imagesIds;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
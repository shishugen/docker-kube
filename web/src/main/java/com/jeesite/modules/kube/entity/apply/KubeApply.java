/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.apply;

import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.course.KubeCourse;
import com.jeesite.modules.sys.entity.User;
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
 * 申请预约Entity
 * @author ssg
 * @version 2019-12-22
 */
@Table(name="kube_apply", alias="a", columns= {
		@Column(name = "id", attrName = "id", label = "编号", isPK = true),
		@Column(name = "class_id", attrName = "classId", label = "班级ID"),
		@Column(name = "course_id", attrName = "courseId", label = "课程ID"),
		@Column(name = "start_date", attrName = "startDate", label = "开始时间"),
		@Column(name = "end_date", attrName = "endDate", label = "结束时间"),
		@Column(name = "type", attrName = "type", label = "类型", comment = "类型(1:上课，2：个人)"),
		@Column(includeEntity = DataEntity.class),
},joinTable={
		@JoinTable(type=Type.LEFT_JOIN, entity=KubeCourse.class, attrName="kubeCourse", alias="c1",
				on="c1.id = a.course_id", columns={
				@Column(name="course_name", label="课程名称", isQuery=false),
		}),
		@JoinTable(type=Type.LEFT_JOIN, entity=KubeClass.class, attrName="kubeClass", alias="c2",
						on="c2.id = a.class_id", columns={
						@Column(name="class_name", label="班名称", isQuery=false)
				}
		),

	}, orderBy="a.update_date DESC"
)
public class KubeApply extends DataEntity<KubeApply> {
	
	private static final long serialVersionUID = 1L;
	private String classId;		// 班级ID
	private String courseId;		// 课程ID
	private Date startDate;		// 开始时间
	private Date endDate;		// 结束时间
	private String type;		// 类型(1:上课，2：个人)
	private KubeCourse kubeCourse;
	private KubeClass kubeClass;


	
	public KubeApply() {
		this(null);
	}

	public KubeApply(String id){
		super(id);
	}
	
	@Length(min=0, max=64, message="班级ID长度不能超过 64 个字符")
	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}
	
	@Length(min=0, max=64, message="课程ID长度不能超过 64 个字符")
	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Length(min=0, max=1, message="类型长度不能超过 1 个字符")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public KubeCourse getKubeCourse() {
		return kubeCourse;
	}

	public void setKubeCourse(KubeCourse kubeCourse) {
		this.kubeCourse = kubeCourse;
	}

	public KubeClass getKubeClass() {
		return kubeClass;
	}

	public void setKubeClass(KubeClass kubeClass) {
		this.kubeClass = kubeClass;
	}
}
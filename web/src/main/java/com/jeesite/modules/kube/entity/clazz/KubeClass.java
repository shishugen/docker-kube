/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.clazz;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import java.util.List;
import com.jeesite.common.collect.ListUtils;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 班级Entity
 * @author SSg
 * @version 2019-12-22
 */
@Table(name="kube_class", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="class_name", attrName="className", label="班名称", queryType=QueryType.LIKE),
		@Column(name="class_term", attrName="classTerm", label="学期", queryType=QueryType.LIKE),
		@Column(includeEntity=DataEntity.class),
	}, orderBy="a.update_date DESC"
)
public class KubeClass extends DataEntity<KubeClass> {
	
	private static final long serialVersionUID = 1L;
	private String className;		// 班名称
	private String classTerm;		// 学期
	private List<KubeClassStudents> kubeClassStudentsList = ListUtils.newArrayList();		// 子表列表
	
	public KubeClass() {
		this(null);
	}

	public KubeClass(String id){
		super(id);
	}
	
	@NotBlank(message="班名称不能为空")
	@Length(min=0, max=64, message="班名称长度不能超过 64 个字符")
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	@Length(min=0, max=64, message="学期长度不能超过 64 个字符")
	public String getClassTerm() {
		return classTerm;
	}

	public void setClassTerm(String classTerm) {
		this.classTerm = classTerm;
	}
	
	public List<KubeClassStudents> getKubeClassStudentsList() {
		return kubeClassStudentsList;
	}

	public void setKubeClassStudentsList(List<KubeClassStudents> kubeClassStudentsList) {
		this.kubeClassStudentsList = kubeClassStudentsList;
	}
	
}
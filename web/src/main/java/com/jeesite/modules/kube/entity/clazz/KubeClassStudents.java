/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.entity.clazz;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.common.mybatis.annotation.JoinTable;
import com.jeesite.common.mybatis.annotation.JoinTable.Type;
import javax.validation.constraints.NotNull;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * 班级Entity
 * @author SSg
 * @version 2019-12-22
 */
@Table(name="kube_class_students", alias="a", columns={
		@Column(name="id", attrName="id", label="编号", isPK=true),
		@Column(name="class_id", attrName="classId.id", label="班级ID"),
		@Column(name="user_id", attrName="userId.userCode", label="用户ID"),
		@Column(includeEntity=DataEntity.class),
	}, joinTable={

		@JoinTable(type=Type.LEFT_JOIN, entity=User.class, attrName="userId", alias="u3",
			on="u3.user_code = a.user_id", columns={
				@Column(name="user_code", label="用户编码", isPK=true),
				@Column(name="user_name", label="用户名称", isQuery=false),
		}),
	}, orderBy="a.create_date ASC"
)
public class KubeClassStudents extends DataEntity<KubeClassStudents> {
	
	private static final long serialVersionUID = 1L;
	private KubeClass classId;		// 班级ID 父类
	private User userId;		// 用户ID
	
	public KubeClassStudents() {
		this(null);
	}


	public KubeClassStudents(KubeClass classId){
		this.classId = classId;
	}
	
	@NotBlank(message="班级ID不能为空")
	@Length(min=0, max=64, message="班级ID长度不能超过 64 个字符")
	public KubeClass getClassId() {
		return classId;
	}

	public void setClassId(KubeClass classId) {
		this.classId = classId;
	}
	
	@NotNull(message="用户ID不能为空")
	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}
	
}
/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.test;

import com.jeesite.modules.Application;
import com.jeesite.modules.kube.dao.clazz.KubeClassStudentsDao;
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * 初始化核心表数据
 * @author ThinkGem
 * @version 2017-10-22
 */
@ActiveProfiles("test")
@SpringBootTest(classes=Application.class)
@Rollback(false)
public class KubectTest extends com.jeesite.modules.sys.db.InitCoreData {


	@Autowired
	private KubeClassStudentsDao kubeClassStudentsDao;

	@Test
	public void initCoreData() throws Exception{
		KubeClassStudents kubeClassStudents = new KubeClassStudents();
		kubeClassStudents.setApplyId("1209388506417901568");
		kubeClassStudents.setClassId(new KubeClass("1208624803403313152"));
		List<KubeClassStudents> studentsList = kubeClassStudentsDao.findApplyIdNotBind(kubeClassStudents);
		System.out.println(studentsList);
	}
	
}

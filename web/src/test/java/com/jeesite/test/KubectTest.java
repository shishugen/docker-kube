/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.test;

import com.jeesite.modules.Application;
import com.jeesite.modules.kube.dao.clazz.KubeClassStudentsDao;
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.service.courseimages.KubeCourseImagesService;
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
	private KubeCourseImagesService kubeCourseImagesService;

	@Test
	public void initCoreData() throws Exception{
		List<KubeCourseImages> list = kubeCourseImagesService.findList(new KubeCourseImages("1249909188377997312"));
		System.out.println(list);
	}
	
}

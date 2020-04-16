/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.apply;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.dao.clazz.KubeClassStudentsDao;
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.course.KubeCourse;
import com.jeesite.modules.kube.entity.courseimages.KubeCourseImages;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.entity.vmlog.KubeVmLog;
import com.jeesite.modules.kube.service.course.KubeCourseService;
import com.jeesite.modules.kube.service.courseimages.KubeCourseImagesService;
import com.jeesite.modules.kube.service.image.KubeImagesService;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.kube.service.vmlog.KubeVmLogService;
import com.jeesite.modules.kube.work.BindVmThread;
import com.jeesite.modules.kube.work.CreateVmThread;
import com.jeesite.modules.kube.work.CreateVmThread2;
import com.jeesite.modules.kube.work.ThreadPool;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.dao.apply.KubeApplyDao;

import static java.util.stream.Collectors.groupingBy;

/**
 * 申请预约Service
 * @author ssg
 * @version 2019-12-22
 */
@Service
@Transactional(readOnly=true)
public class KubeApplyService extends CrudService<KubeApplyDao, KubeApply> {

	@Autowired
    private KubeVmLogService kubeVmLogService;

	@Autowired
    private KubeVmService kubeVmService;
	@Autowired
    private KubeClassStudentsDao kubeClassStudentsDao;

	@Autowired
    private KubeCourseImagesService kubeCourseImagesService;
	@Autowired
    private KubeImagesService kubeImagesService;

	@Autowired
	private KubeCourseService kubeCourseService;

	/**
	 * 获取单条数据
	 * @param kubeApply
	 * @return
	 */
	@Override
	public KubeApply get(KubeApply kubeApply) {
		return super.get(kubeApply);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeApply 查询条件
	 * @param
	 * @return
	 */
	@Override
	public Page<KubeApply> findPage(KubeApply kubeApply) {
		return super.findPage(kubeApply);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeApply kubeApply) {
		if (KubeApply.CLASS_APPLY==kubeApply.getType()){
		    kubeApply.setUserId(UserUtils.getUser().getId());
		}else{
			kubeApply.setClassId("");
		}
		super.save(kubeApply);
	}
	
	/**
	 * 更新状态
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeApply kubeApply) {
		super.updateStatus(kubeApply);
	}
	
	/**
	 * 删除数据
	 * @param kubeApply
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeApply kubeApply) {
		super.delete(kubeApply);
	}

	@Transactional(readOnly=false)
	public void bindingVm() {
		KubeApply kubeApply = new KubeApply();
		List<KubeApply> list = dao.findByStartDate(kubeApply);
		if(list == null || list.size() ==0){
			System.out.println("无申请预约=====》");
			return;
		}
		System.out.println("申请预约=====》"+list.size());
		list.forEach((apply->{
			String applyId = apply.getId();
			String classId = apply.getClassId();
			String courseId = apply.getCourseId();
			String userId = apply.getUserId();
			Integer type = apply.getType();
			//查找当前课程的镜像
			List<KubeCourseImages> courseImagesList = this.getImagesByCourseId(courseId);
			System.out.println("镜像=====》"+courseImagesList.size());
			courseImagesList.forEach(image->{
				 KubeImages images = kubeImagesService.get(image.getImagesId());
				 String repositoryNam = images.getRepositoryName();
				String cpu = images.getCpu();
				String memory = images.getMemory();
				Integer number = courseImagesList.size();
				System.out.println("镜像=====》"+repositoryNam+"==cpu="+cpu+"===memory+"+number);
				if (StringUtils.isNotBlank(classId)){
					long count = kubeClassStudentsDao.findCount(new KubeClassStudents(new KubeClass(classId)));
					number = Integer.valueOf("0" + count);
				}
				System.out.println("镜像=====》"+repositoryNam+"==cpu="+cpu+"===memory+"+number);
				ThreadPool.executorService.submit( new CreateVmThread(cpu,memory,repositoryNam,number,type,applyId));
			});
			KubeVmLog vmLog = new KubeVmLog(applyId, KubeVmLog.VmStatus.create.ordinal());
			kubeVmLogService.save(vmLog);
			ThreadPool.executorService.submit(new BindVmThread(classId,userId,applyId,type));
		}));
	}


	/**
	 * @Author ssg
	 * @Description //TODO 释放资源
	 * @Date  2019/12/25 10:43
	 * @Param []
	 * @return void
	 **/
	@Transactional(readOnly=false)
	public void releaseVm() {
		List<KubeApply> applyList = dao.findByEndDate(new KubeApply());
		if(applyList == null || applyList.size() ==0){
			System.out.println("无释放资源=====》");
			return;
		}
		applyList.forEach(apply->{
			KubeVm vm = new KubeVm();
			vm.setApplyId(apply.getId());
			List<KubeVm> list = kubeVmService.findList(vm);
			AtomicBoolean flag = new AtomicBoolean(true);
			list.forEach(kubeVm -> {
				kubeVmService.delete(kubeVm);
				System.err.println("放资源==成功");
				if (flag.get()){
					if(KubeClinet.dalDeploment(kubeVm.getDeploymentName())){
						System.err.println("放资源服务器资源----==成功");
						flag.set(false);
					}else{
						System.err.println("放资源服务器资源----==失败---");
					}
				}
				/*if(deploymentName == null || namespace == null
						|| !namespace.equals(kubeVm.getNamespace())
						|| !deploymentName.equals(kubeVm.getDeploymentName())){
					deploymentName = kubeVm.getDeploymentName();
					namespace = kubeVm.getNamespace();

				}*/
			});
			dao.delete(apply);
		});
	}

	public List<KubeCourseImages> getImagesByCourseId(String courseId){
		 return kubeCourseImagesService.findList(new KubeCourseImages(courseId,null));
	}

    private static Integer strToNumber(String str){
        str = str.trim();
        System.out.println(str);
        StringBuilder sb = new StringBuilder();
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    sb.append(str.charAt(i)) ;
                } else {
                 //   System.out.println("单位"+str.charAt(i));
                    break;
                }
            }
        }
        return Integer.valueOf(sb.toString());
    }
    public static void main(String[] args) {
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
        //  List<Pod> items = kubeclinet.pods().inNamespace("default").list().getItems();
         List<Pod> items = kubeclinet.pods().list().getItems();
        System.out.println(items);

        AtomicReference<Integer> cpu = new AtomicReference<>(0);
        AtomicReference<Integer> memory = new AtomicReference<>(0);
        items.forEach(pod -> {
            pod.getSpec().getContainers().forEach(container -> {
                ResourceRequirements resources = container.getResources();
                if (resources == null)return;
                Map<String, Quantity> limits = resources.getLimits();
                Map<String, Quantity> requests = resources.getRequests();
                if (limits !=null && limits.size() > 0){
                    Quantity quantityCpu = limits.get("cpu");
                    Quantity quantityMemory = limits.get("memory");
                    if(quantityCpu != null){
                        Integer podCpu = strToNumber(quantityCpu.getAmount());
                        cpu.updateAndGet(v -> v + podCpu);
                    }
                    if(quantityMemory != null){
                        Integer podMemory = strToNumber(quantityMemory.getAmount());
                        memory.updateAndGet(m-> m+podMemory);
                    }
                }else if(requests !=null && requests.size() > 0){
                    Quantity quantityCpu = requests.get("cpu");
                    Quantity quantityMemory = requests.get("memory");
                    if(quantityCpu != null){
                        Integer podCpu = strToNumber(quantityCpu.getAmount());
                        cpu.updateAndGet(v -> v + podCpu);
                    }
                   if(quantityMemory != null){
                        Integer podMemory = strToNumber(quantityMemory.getAmount());
                        memory.updateAndGet(m-> m+podMemory);
                    }
                }

               // quantityCpu.
            });
        });

        System.out.println(cpu);
        System.out.println(memory);
    }



	@Transactional(readOnly=false)
	public void bindingVm2() {
		KubeApply kubeApply = new KubeApply();
		List<KubeApply> list = dao.findByStartDate(kubeApply);
		if(list == null || list.size() ==0){
			System.out.println("无申请预约=====》");
			return;
		}
		System.out.println("申请预约=====》"+list.size());
		list.forEach((apply-> {
			String applyId = apply.getId();
			String classId = apply.getClassId();
			String courseId = apply.getCourseId();

			KubeCourse kubeCourse = kubeCourseService.get(courseId);

			//查找当前课程的镜像
			List<KubeCourseImages> courseImagesList = this.getImagesByCourseId(courseId);

			KubeClassStudents kubeClassStudents = new KubeClassStudents();
			kubeClassStudents.setApplyId(applyId);
			kubeClassStudents.setClassId(new KubeClass(classId));
			List<KubeClassStudents> studentsList = kubeClassStudentsDao.findApplyIdNotBind(kubeClassStudents);

			ThreadPool.executorService.submit( new CreateVmThread2(studentsList,courseImagesList,apply,kubeCourse.getCode()));

			//保存日志
			KubeVmLog vmLog = new KubeVmLog(applyId, KubeVmLog.VmStatus.create.ordinal());
			kubeVmLogService.save(vmLog);
		}));



	}


}
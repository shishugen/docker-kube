/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.image;

import java.util.List;

import com.jcraft.jsch.Session;
import com.jeesite.common.idgen.IdGen;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.core.SSHRemoteCall;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.image.KubeImages;
import com.jeesite.modules.kube.dao.image.KubeImagesDao;

/**
 * 本地镜像Service
 * @author ssg
 * @version 2019-12-21
 */
@Service
@Transactional(readOnly=true)
public class KubeImagesService extends CrudService<KubeImagesDao, KubeImages> {

	@Autowired
	private KubeVmService kubeVmService;
	/**
	 * 获取单条数据
	 * @param kubeImages
	 * @return
	 */
	@Override
	public KubeImages get(KubeImages kubeImages) {
		return super.get(kubeImages);
	}
	
	/**
	 * 查询分页数据
	 * @param kubeImages 查询条件
	 * @param kubeImages.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeImages> findPage(KubeImages kubeImages) {
		return super.findPage(kubeImages);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param kubeImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(KubeImages kubeImages) {
		super.save(kubeImages);
	}
	
	/**
	 * 更新状态
	 * @param kubeImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(KubeImages kubeImages) {
		super.updateStatus(kubeImages);
	}
	
	/**
	 * 删除数据
	 * @param kubeImages
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(KubeImages kubeImages) {
		super.delete(kubeImages);
	}

	@Transactional(readOnly=false)
    public KubeVm createPod(KubeImages kubeImages) {
	    String podname = IdGen.nextId();
		String namespace = "image-"+podname;
		Pod pod = KubeClinet.watch(namespace,podname,podname,kubeImages.getRepositoryName(),
				kubeImages.getCpu(),kubeImages.getMemory());

		ObjectMeta metadata = pod.getMetadata();
		PodStatus status = pod.getStatus();
		String hostIp = status.getHostIP();
		KubeVm kubeVm = new KubeVm();
		kubeVm.setVmName(metadata.getName());
		kubeVm.setVmIp(status.getPodIP());
		kubeVm.setHostIp(status.getHostIP());
		Integer integer = KubeVm.VM_STATUS_MAP.get(status.getPhase());
		kubeVm.setVmStatus(integer == null ? 404 : integer);
		// kubeVm.setDeploymentName(deploymentName);
		kubeVm.setNamespace(namespace);
		kubeVm.setType(1);
		kubeVm.setUserId(UserUtils.getUser());
		status.getContainerStatuses().forEach(st->{
			String containerID = st.getContainerID();
			if(StringUtils.isNotEmpty(containerID)){
				//去掉  docker://
				containerID = containerID.substring(9, containerID.length());
				kubeVm.setContainerId(containerID);
				try {
					Session session = new SSHRemoteCall().getInstance2().sshRemoteCallLogin(hostIp);
					SSHRemoteCall.sshMap.put(containerID,session);
				} catch (Exception e) {
					logger.error("SSH登录失败!");
					e.printStackTrace();
				}
			}
		});
		kubeVmService.save(kubeVm);
      return  kubeVm;
	}
}
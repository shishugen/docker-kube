/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.kube.service.vm;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.core.DockerClinet;
import com.jeesite.modules.kube.core.KubeClinet;
import com.jeesite.modules.kube.core.KubeConfig;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.entity.userimages.KubeUserImages;
import com.jeesite.modules.kube.service.apply.KubeApplyService;
import com.jeesite.modules.kube.service.userimages.KubeUserImagesService;
import com.jeesite.modules.sys.utils.UserUtils;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.dao.vm.KubeVmDao;

/**
 * 虚拟机Service
 * @author ssg
 * @version 2019-12-23
 */
@Service
@Transactional(readOnly=true)
public class KubeVmService extends CrudService<KubeVmDao, KubeVm> {

    @Autowired
    KubeUserImagesService kubeUserImagesService;

    @Autowired
    private KubeApplyService kubeApplyService;

    /**
	 * 获取单条数据
	 *
	 * @param kubeVm
	 * @return
	 */
	@Override
	public KubeVm get(KubeVm kubeVm) {
		return super.get(kubeVm);
	}

	/**
	 * 查询分页数据
	 *
	 * @param kubeVm      查询条件
	 * @param kubeVm.page 分页对象
	 * @return
	 */
	@Override
	public Page<KubeVm> findPage(KubeVm kubeVm) {
		return super.findPage(kubeVm);
	}

	/**
	 * 保存数据（插入或更新）
	 *
	 * @param kubeVm
	 */
	@Override
	@Transactional(readOnly = false)
	public void save(KubeVm kubeVm) {
		super.save(kubeVm);
	}

	/**
	 * 更新状态
	 *
	 * @param kubeVm
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateStatus(KubeVm kubeVm) {
		super.updateStatus(kubeVm);
	}

	/**
	 * 删除数据
	 *
	 * @param kubeVm
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(KubeVm kubeVm) {
		KubeClinet.dalDeploment(kubeVm.getDeploymentName());
		super.delete(kubeVm);
	}

	public List<KubeVm> findApplyIdNotBind(KubeVm kubeVm) {
		return dao.findApplyIdNotBind(kubeVm);
	}

	/**
	 * @Author ssg
	 * @Description //TODO 保存容器
	 * @Date  2019/12/30 10:31
	 * @Param
	 * @return
	 **/
    @Transactional(readOnly = false)
	public void saveContainer(KubeVm kubeVm) {
		DockerClient dockerClient = DockerClinet.getDockerClient(kubeVm.getHostIp());
		String vmName = kubeVm.getVmName();
        double version = 1.0;
        KubeUserImages kubeUserImage = null;
		if (kubeVm != null && vmName != null) {
            if(kubeVm.getType()==0){
               // KubeApply kubeApply = kubeApplyService.get(kubeVm.getApplyId());

            }else{
                //查询是否已有保存过容器
                 kubeUserImage = kubeUserImagesService.get(kubeVm.getApplyId());
                if(kubeUserImage != null){
                    version = kubeUserImage.getVersion() + 1;
                }
            }
            //上传验证的私服仓库的镜像必须设置
            AuthConfig authConfig = new AuthConfig();
            authConfig.withRegistryAddress(KubeConfig.HARBOR_REGISTRY_URL);
            authConfig.withUsername(KubeConfig.REGISTRY_USER_NAME);
            authConfig.withPassword(KubeConfig.REGISTRY_PASSWORD);
            String containerId =kubeVm.getContainerId();
            String hostIp = kubeVm.getHostIp(); //节点
            String repository =KubeConfig.REGISTRY_REPOSITORY_IP+KubeConfig.REGISTRY_REPOSITORY_PROJECT;
            String loginName = UserUtils.getUser().getLoginCode();
            System.out.println(repository+":"+version);
            String backImagesId = dockerClient.commitCmd(containerId).withRepository(repository+"/"+loginName).withTag(version+"").exec();

            //保存
            KubeUserImages userImages = new KubeUserImages();
            if(StringUtils.isNotEmpty(backImagesId)){
                String imagsId = backImagesId.substring(7, backImagesId.length());
                userImages.setImagesId(imagsId);
            }
            userImages.setType(1);
            userImages.setVersion(version);
            userImages.setRepositoryHost(KubeConfig.REGISTRY_REPOSITORY_IP);
            userImages.setWorkNodeIp(hostIp);
            userImages.setUserId(UserUtils.getUser().getId());
            userImages.setRepository(KubeConfig.REGISTRY_REPOSITORY_PROJECT+"/"+loginName);
            kubeUserImagesService.save(userImages);
            PushImage pushImage = new PushImage();

            //push
            dockerClient.pushImageCmd(repository+"/"+loginName).withTag(version+"").withAuthConfig(authConfig).exec(pushImage ).awaitSuccess();
            System.out.println("OK");
          //  Boolean success = pushImage.isSuccess();
          /*  if(success && kubeUserImage != null){
                DockerClinet.delImage(kubeUserImage.getImagesId());
                kubeUserImagesService.delete(kubeUserImage);
            }*/
        }
	}

	private class PushImage extends PushImageResultCallback{
	    private Boolean flag = false;
        @Override
        public void onNext(PushResponseItem item) {
            System.out.println("id:" + item.getId() + " status: " + item.getStatus());
            super.onNext(item);
            if("Pushed".equals(item.getStatus())){
                System.out.println("成功");
            }
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("Image pushed completed!"+throwable);
            super.onError(throwable);
        }

        @Override
        public void onComplete() {
            System.out.println("Image pushed completed!");
            System.out.println("完成");
            super.onComplete();
        }

        @Override
        public void close() throws IOException {
            super.close();
        }

        public Boolean isSuccess(){
            return this.flag;
        }
    }




	private static final String DEFAULT_NAMESPACE = "default";
	private static final String DEFAULT_IMAGES_NAME = "registry.cn-hangzhou.aliyuncs.com/centos7-01/centos7-ssh:v1.0";

	public static void main(String[] args) {

        DockerClient dockerClient = DockerClinet.getDockerClient();

		//List<Image> exec = dockerClient.listImagesCmd().exec();
		//System.out.println(exec);
		//String str ="b4f5bb6c56e9";
		//	String exec1 = dockerClient.commitCmd(str).withRepository("test001001").withTag("123").exec();
		//	System.out.println(exec1);
		//test("100",1,1,DEFAULT_IMAGES_NAME);
		//List<Pod> items = kubeclinet.pods().inNamespace(DEFAULT_NAMESPACE).list().getItems();
	//	String image = dockerClient.commitCmd(containerId).withRepository("test001001").withTag("v2").exec();

		//上传验证的私服仓库的镜像必须设置
		AuthConfig authConfig = new AuthConfig();
		authConfig.withUsername("admin");
		authConfig.withPassword("Harbor12345");
		String coentid ="ddd51729142b3f5c6901f7fa405003f866185b9b396d81d25506fb41b7b14968";
		String repository ="192.168.103.236/centos-test/";
		String loginName = "user1";
		String tag ="1.0";
		//String exec1 = dockerClient.commitCmd(coentid).withRepository(repository+loginName).withTag(tag).exec();
		//System.out.println(exec1);//sha256:5a193119a1e1e5e4cc1e3a97a7d1234daa9041dbea1514bc4acb59f6547dee33
		//String imag ="192.168.103.235/centos-ssh/nginx-test";
		String imageId = "5a193119a1e1e5e4cc1e3a97a7d1234daa9041dbea1514bc4acb59f6547dee33";

        dockerClient.pushImageCmd(repository+loginName).withTag(tag).withAuthConfig(authConfig).exec(new PushImageResultCallback() {
			@Override
			public void onNext(PushResponseItem item) {
				System.out.println("id:" + item.getId() + " status: " + item.getStatus());
				System.out.println("item=====->>"+item);
				super.onNext(item);
				if("Pushed".equals(item.getStatus())){
					System.out.println("成功");
				}else{

				}
			}

			@Override
			public void onComplete() {
				System.out.println("Image pushed completed!");
				super.onComplete();
			}

			@Override
			public void onError(Throwable throwable) {
				System.out.println("Image pushed onError!");
				super.onError(throwable);
			}
		}).awaitSuccess();
		System.out.println("OK");

        PushImageResultCallback pushImageResultCallback = new PushImageResultCallback();


    }

    public  void test(){
        KubernetesClient kubeclinet = KubeClinet.getKubeclinet();
        NodeList list = kubeclinet.nodes().list();
        List<Node> items1 = list.getItems();
        System.out.println(items1);
        //总
        AtomicInteger totalCpu = new AtomicInteger();
        AtomicInteger totalMemory = new AtomicInteger();
        AtomicInteger totalStorage = new AtomicInteger();

        //可用
        AtomicInteger allocatableCpu = new AtomicInteger();
        AtomicInteger allocatableMemory = new AtomicInteger();
        AtomicInteger allocatableStorage = new AtomicInteger();
        //后续优化
        List<Node> collect = items1.stream().filter(a -> a.getSpec().getTaints().size() != 1).collect(Collectors.toList());
        collect.stream().forEach(node->{
            NodeStatus status = node.getStatus();
            Map<String, Quantity> capacity = status.getCapacity();
            capacity.forEach((k, v)->{
                Quantity quantity = capacity.get(k);
                switch (k){
                    case "cpu":
                        String amount = quantity.getAmount();
                        totalCpu.addAndGet(Integer.valueOf(amount));
                        break;
                    case "ephemeral-storage":
                        String nodeStorage = quantity.getAmount();
                        if(nodeStorage.indexOf("Mi") != -1){
                            totalStorage.addAndGet(Integer.valueOf(nodeStorage.substring(0,nodeStorage.length()-2))/1024);
                        }else{
                            totalStorage.addAndGet(Integer.valueOf(nodeStorage)/1024/1024/1024);
                        }
                        break;
                    case "memory":
                        String modeMemory = quantity.getAmount();
                        String substring = modeMemory.substring(0, modeMemory.length() - 2);
                        totalMemory.addAndGet(Integer.valueOf(substring) / 1024 / 1024);
                        break;
                }

            });
            //allocatable
            Map<String, Quantity> allocatable = status.getAllocatable();
            allocatable.forEach((k,v)->{
                Quantity quantity = capacity.get(k);
                switch (k){
                    case "cpu":
                        String amount = quantity.getAmount();
                        allocatableCpu.addAndGet(Integer.valueOf(amount));
                        break;
                    case "ephemeral-storage":
                        String nodeStorage = quantity.getAmount();
                        if(nodeStorage.indexOf("Mi") != -1){
                            allocatableStorage.addAndGet(Integer.valueOf(nodeStorage.substring(0,nodeStorage.length()-2))/1024);
                        }else{
                            allocatableStorage.addAndGet(Integer.valueOf(nodeStorage)/1024/1024/1024);
                        }
                        break;
                    case "memory":
                        String modeMemory = quantity.getAmount();
                        String substring = modeMemory.substring(0, modeMemory.length() - 2);
                        allocatableMemory.addAndGet(Integer.valueOf(substring) / 1024 / 1024);
                        break;
                }
            });


        });
        System.out.println("allocatableMemory--->"+allocatableMemory);
        System.out.println("allocatableCpu----->"+allocatableCpu);
        System.out.println("allocatableStorage----->"+allocatableStorage);

        System.out.println("totalMemory--->"+totalMemory);
        System.out.println("totalCpu----->"+totalCpu);
        System.out.println("totalStorage----->"+totalStorage);
    }


}
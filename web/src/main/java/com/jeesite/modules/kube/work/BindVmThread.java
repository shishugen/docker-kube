package com.jeesite.modules.kube.work;

import com.jeesite.modules.kube.dao.clazz.KubeClassStudentsDao;
import com.jeesite.modules.kube.entity.apply.KubeApply;
import com.jeesite.modules.kube.entity.clazz.KubeClass;
import com.jeesite.modules.kube.entity.clazz.KubeClassStudents;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.clazz.KubeClassService;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import com.jeesite.modules.kube.utlis.SpringUtil;
import com.jeesite.modules.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName BindVmThread
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/24 9:59
 */
public class BindVmThread extends ThreadPool {

    static {

    }
    @Autowired
    private KubeVmService kubeVmService;

    @Autowired
    private KubeClassStudentsDao kubeClassStudentsDao;

    private String applyId;
    private String classId;
    private String userId;
    private Integer type;

    public BindVmThread(String classId, String userId, String applyId,Integer type) {
        this.applyId = applyId;
        this.classId = classId;
        this.userId = userId;
        this.type = type;
    }

    @Override
    public void run() {
        kubeVmService = SpringUtil.getBean(KubeVmService.class);
        kubeClassStudentsDao = SpringUtil.getBean(KubeClassStudentsDao.class);
        try {
            AtomicBoolean flag = new AtomicBoolean(true);
            while (flag.get()){
                synchronized (this){
                    KubeVm kubeVm = new KubeVm(applyId);
                    kubeVm.setApplyId(applyId);
                    List<KubeVm> list =  kubeVmService.findApplyIdNotBind(kubeVm);
                    //班级预约
                    if (KubeApply.CLASS_APPLY == type && classId != null){
                        KubeClassStudents kubeClassStudents = new KubeClassStudents();
                        kubeClassStudents.setApplyId(applyId);
                        kubeClassStudents.setClassId(new KubeClass(classId));
                        List<KubeClassStudents> studentsList = kubeClassStudentsDao.findApplyIdNotBind(kubeClassStudents);
                        System.out.println("studentsList----"+studentsList.size()+"==list"+list.size());
                        //虚拟机数量一定不能小于学生数量
                            for (int i =0; i < list.size(); i++){
                                if(studentsList.get(i) !=null){
                                    KubeVm vm = list.get(i);
                                    vm.setUserId(studentsList.get(i).getUserId());
                                    kubeVmService.save(vm);
                                    System.err.println("绑定----保存"+i);
                                }
                            }
                        if(studentsList.size() == 0){
                            System.err.println("绑定完成----退出");
                            flag.set(false);
                        }else{
                            Thread.sleep(30000L);
                            System.err.println("绑定sleep----");
                        }
                        //个人
                    }else if((KubeApply.ONE_APPLY == type)){
                        list.forEach(vm->{
                            vm.setUserId(new User(userId));
                            kubeVmService.save(vm);
                            flag.set(false);
                        });
                         Thread.sleep(30000L);
                    }else{
                        System.err.println("预约类型---异常");
                        flag.set(false);
                    }
                }
            }
        }catch (Exception e){
            System.err.println("绑定---异常");
            e.printStackTrace();
        }
    }
}

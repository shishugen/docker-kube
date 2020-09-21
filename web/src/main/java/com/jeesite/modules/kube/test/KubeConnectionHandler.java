package com.jeesite.modules.kube.test;

import io.fabric8.kubernetes.client.KubernetesClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class KubeConnectionHandler implements InvocationHandler {

    private static final String close_Method_Name = "close";//方法名
    //false为正闲
    private boolean inuse = false;//是否可用

    private KubernetesClient kubecon = null;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object obj = null;
        try{
            if(close_Method_Name.equals(method.getName())){
                System.out.println("返回到连接池");
                this.setInuse(false);//返回到连接池
            }else{
                obj = method.invoke(kubecon,args);//直接返回没有接管的对象con
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return obj;

    }
    public KubeConnectionHandler(KubernetesClient kubecon,boolean inuse){
        this.kubecon = kubecon;
        this.inuse = inuse;
    }

    /**
     *
     *返回数据库连接con的接管类,以便截住close
     *
     **/
    public KubernetesClient getConnection(){
        KubernetesClient con1 = (KubernetesClient)Proxy.newProxyInstance(
                kubecon.getClass().getClassLoader(),
                kubecon.getClass().getInterfaces(),this);
        return con1;
    }

    /**
     *
     *直接关闭连接,由于con没有被接管的属性，直接关闭
     *
     **/

    public void close(){
        try{

            kubecon.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     *设置inuse
     *
     **/

    public void setInuse(boolean b){

        this.inuse = b;
    }

    /**
     *
     *得到inuse
     *
     **/

    public boolean isInuse(){

        return this.inuse;
    }
}

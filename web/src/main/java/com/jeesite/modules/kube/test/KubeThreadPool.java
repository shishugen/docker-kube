package com.jeesite.modules.kube.test;

import com.jeesite.modules.kube.core.KubeClinet;
import io.fabric8.kubernetes.client.*;
import lombok.SneakyThrows;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Vector;

public class KubeThreadPool {

    private final static  String KUBECONFIG_FILE = filePath(KubeClinet.class.getResource("/kube-docker/kube/kube-config"));

    private int maxconnections = 200;//最大连接数,

    private int curconnections = 10;//当前连接数，如果当等于最大时，再需要连接时，则抛出异常

    private Vector kubeConnections = null;

    private  Config config = null;

    public KubeThreadPool(){}


    public KubeThreadPool(int maxconnections,int curconnections){
       this.maxconnections = maxconnections;
       this.curconnections = curconnections;
    }



    /**
     *
     *返回当前连接数
     *
     **/

    public int getCurlinks(){
        return this.curconnections;
    }

    /**
     *
     *创建一个连接池
     *
     **/

    public synchronized void createPool() {
        //如果连接池已创建，不会为空
        if (kubeConnections != null) {
            return;
        }
        try{
            //创建保存连接的向量
            kubeConnections = new Vector();
            if (config == null) {
                synchronized (KubeThreadPool.class) {
                    System.setProperty(Config.KUBERNETES_KUBECONFIG_FILE, KUBECONFIG_FILE);
                    config = new ConfigBuilder().build();
                }
            }
            for(int i = 0; i < this.curconnections; i++){
                kubeConnections.addElement(new KubeConnectionHandler(new DefaultKubernetesClient(config),false));
            }
            //连接池建立
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     *
     *创建一个connection
     *
     *
     */
    public KubernetesClient newConnection(){
        KubernetesClient kubeConnection = null;
        try{
            kubeConnection = new DefaultKubernetesClient(config);
        }catch(Exception e){
            e.printStackTrace();
        }
        return kubeConnection;
    }

    /**
     *
     *返回一个连接，如果当前连接数小于最大的连接时，增加一个新的连接
     *
     **/

    public KubernetesClient getKubeConnection(){
        KubernetesClient con = this.getFreeKubeConnection();
        if(con == null){//如果连接池已满
            if(this.curconnections < this.maxconnections){
                KubernetesClient con1 = this.newConnection();
                KubeConnectionHandler cons1 = new KubeConnectionHandler(con1,true);
                synchronized(kubeConnections){//实现同步
                    kubeConnections.addElement(cons1);//创建一个新的连接
                    this.curconnections++;//当前连接池中的连接数增加一个。
                }
                con = cons1.getConnection();
            }else{
               try{
                    throw new KubernetesClientException("连接数已经达到最大");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }//end if(con == null)
        return con;
    }


    /**
     *
     *返回一个可用连接的连接
     *
     **/

    public synchronized KubernetesClient getFreeKubeConnection(){

        KubernetesClient con = null;
        KubeConnectionHandler pcon = null;
        try{

            //得确保连接池已经创建
            if(kubeConnections == null){
                return null;//连接池还没创建
            }

            Enumeration it = this.kubeConnections.elements();
            while(it.hasMoreElements()){
                pcon = (KubeConnectionHandler)it.nextElement();

                if(!pcon.isInuse()){//如果可用
                    con = pcon.getConnection();//闲时返回其状态
                    pcon.setInuse(true);//设置其已经不可用
                    break;
                }

            }//end while

        }catch(Exception e){
            e.printStackTrace();
        }
        //如果没用，则返回一个null
        return con;
    }

    public static String filePath(URL path) {
        try {
            return Paths.get(path.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        KubeThreadPool kubeThreadPool = new KubeThreadPool();
        kubeThreadPool.createPool();

        new Thread();
        for (int i = 0 ; i < 10 ; i++){
            KubernetesClient kubeConnection = kubeThreadPool.getKubeConnection();
                ThreadDemo threadDemo = new ThreadDemo(kubeConnection);
                threadDemo.start();
        }
    }
    static class  ThreadDemo extends   Thread {
        KubernetesClient kubernetesClient;

       public ThreadDemo (KubernetesClient kubernetesClient){
         this.kubernetesClient=kubernetesClient;
       }
        @SneakyThrows
        @Override
        public void run(){
            System.out.println(kubernetesClient);
            if(kubernetesClient != null){

                kubernetesClient.close();
            }
        }
    }
}

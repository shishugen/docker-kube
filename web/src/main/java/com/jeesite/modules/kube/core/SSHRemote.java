package com.jeesite.modules.kube.core;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @ClassName: SSHRemote
 * @Description: TODO
 * @Author: shugen
 * @Date: 2020/8/20 15:38
 */
public class SSHRemote {

    // 私有的对象
    private static SSHRemote sshRemoteCall;
    /**
     * 私有的构造方法
     */
    public SSHRemote() {
    }

    // 懒汉式,线程不安全,适合单线程
    public static SSHRemote getInstance() {
        if (sshRemoteCall == null) {
            sshRemoteCall = new SSHRemote();
        }
        return sshRemoteCall;
    }

    // 懒汉式,线程安全,适合多线程
    public static synchronized SSHRemote getInstance2() {
        if (sshRemoteCall == null) {
            sshRemoteCall = new SSHRemote();
        }
        return sshRemoteCall;
    }

    private static final int DEFAULT_PORT = 22;// 默认端口号
    private int port;// 端口号

    private static String ipAddress = "192.168.103.235";// ip地址
    private static String userName = "root";// 账号
    private static String password = "123456";// 密码

    private Session session;// JSCH session
    private boolean logined = false;// 是否登陆

    /**
     * 构造方法,可以直接使用DEFAULT_PORT
     *
     * @param ipAddress
     * @param userName
     * @param password
     */
    public SSHRemote(String ipAddress, String userName, String password) {
        this(ipAddress, DEFAULT_PORT, userName, password);
    }

    /**
     * 构造方法,方便直接传入ipAddress,userName,password进行调用
     *
     * @param ipAddress
     * @param port
     * @param userName
     * @param password
     */
    public SSHRemote(String ipAddress, int port, String userName, String password) {
        super();
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.password = password;
        this.port = port;
    }

    /**
     * 远程登陆
     *
     * @throws Exception
     */
    public void sshRemoteCallLogin(String ipAddress, String userName, String password) throws Exception {
        // 如果登陆就直接返回
       /* if (logined) {
            return;
        }*/
        // 创建jSch对象
        JSch jSch = new JSch();
        try {
            // 获取到jSch的session, 根据用户名、主机ip、端口号获取一个Session对象
            session = jSch.getSession(userName, ipAddress, DEFAULT_PORT);
            // 设置密码
            session.setPassword(password);
            // 方式一,通过Session建立连接
            // session.setConfig("StrictHostKeyChecking", "no");
            // session.connect();

            // 方式二,通过Session建立连接
            // java.util.Properties;
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);// 为Session对象设置properties
            // session.setTimeout(3000);// 设置超时
            session.connect();//// 通过Session建立连接

            // 设置登陆状态
            logined = true;
        } catch (JSchException e) {
            // 设置登陆状态为false
            logined = false;
            throw new Exception(
                    "主机登录失败, IP = " + ipAddress + ", USERNAME = " + userName + ", Exception:" + e.getMessage());
        }
    }

    /**
     * 关闭连接
     */
    public void closeSession() {
        // 调用session的关闭连接的方法
        if (session != null) {
            // 如果session不为空,调用session的关闭连接的方法
            session.disconnect();
        }

    }

    /**
     * 执行相关的命令
     *
     * @param command
     * @throws IOException
     */
    public String execCommand(String command) throws IOException {
        InputStream in = null;// 输入流(读)
        Channel channel = null;// 定义channel变量
        String processDataStream = "";
        try {
            // 如果命令command不等于null
            if (command != null) {
                // 打开channel
                //说明：exec用于执行命令;sftp用于文件处理
                channel = session.openChannel("exec");
                // 设置command
                ((ChannelExec) channel).setCommand(command);
                // channel进行连接
                channel.connect();
                // 获取到输入流
                in = channel.getInputStream();
                // 执行相关的命令
                processDataStream = processDataStream(in);
                // 打印相关的命令
                System.out.println("1、打印相关返回的命令: " + processDataStream);
            }
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
        return processDataStream;
    }

    /**
     * 对将要执行的linux的命令进行遍历
     *
     * @param in
     * @return
     * @throws Exception
     */
    public String processDataStream(InputStream in) throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String result = "";
        try {
            while ((result = br.readLine()) != null) {
                sb.append(result).append(",");
                // System.out.println(sb.toString());
            }
        } catch (Exception e) {
            throw new Exception("获取数据流失败: " + e);
        } finally {
            br.close();
        }
        return sb.toString();
    }

    public static void main(String[] args)throws Exception {
        SSHRemote instance2 = SSHRemote.getInstance2();

       // String projectName = "\\\"test0012 \\\"";
        String projectName = "test001244";
        String str ="curl -X POST \"http://192.168.103.236/api/projects\" -H \"accept: text/plain\" -H \"authorization: Basic YWRtaW46SGFyYm9yMTIzNDU=\" -H \"Content-Type: application/json\" -d \"{ \\\"project_name\\\":\\\""+projectName+"\\\", \\\"metadata\\\": { \\\"public\\\": \\\"true\\\" }}\"";
        System.out.println(str);
       instance2.sshRemoteCallLogin("192.168.103.236","root","123456");
       String con = "curl -X POST http://192.168.103.236/api/projects -H  'accept: application/json' -H  'authorization: Basic YWRtaW46SGFyYm9yMTIzNDU=' -H 'Content-Type: application/json' -d '{ 'project_name': 'testroject2222888', 'metadata': { 'public': 'true' }}'";
       String ls = instance2.execCommand(str);

    }
}

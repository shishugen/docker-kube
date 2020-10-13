package com.jeesite.modules.kube.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.*;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.modules.kube.entity.vm.KubeVm;
import com.jeesite.modules.kube.service.vm.KubeVmService;
import io.swagger.models.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @Description TODO
 * @author biehl
 * @Date 2018年10月11日 上午10:20:11
 *
 * 说明:exec用于执行命令;sftp用于文件处理
 */
public class SSHRemoteCall2 {

    @Autowired
    private KubeVmService kubeVmService;
    // 私有的对象
    private SSHRemoteCall2 sshRemoteCall;

    public static Map<String,Session> sshMap = new ConcurrentHashMap<>();

    /**
     * 私有的构造方法
     */
    public SSHRemoteCall2() {
    }

    // 懒汉式,线程不安全,适合单线程
    public SSHRemoteCall2 getInstance() {
        if (sshRemoteCall == null) {
            sshRemoteCall = new SSHRemoteCall2();
        }
        return sshRemoteCall;
    }

    // 懒汉式,线程安全,适合多线程
    public  synchronized SSHRemoteCall2 getInstance2() {
        if (sshRemoteCall == null) {
            sshRemoteCall = new SSHRemoteCall2();
        }
        return sshRemoteCall;
    }

    private static final int DEFAULT_PORT = 22;// 默认端口号
    private int port;// 端口号

    private static String ipAddress = "192.168.103.235";// ip地址
    private static String userName = "root";// 账号
    //private static String password = "xykube@123";// 密码
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
    public SSHRemoteCall2(String ipAddress, String userName, String password) {
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
    public SSHRemoteCall2(String ipAddress, int port, String userName, String password) {
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
    public Session sshRemoteCallLogin(String ipAddress) throws Exception {
        return sshRemoteCallLogin(ipAddress, userName, password);
    }
    /**
     * 远程登陆
     *
     * @throws Exception
     */
    public Session sshRemoteCallLogin(String ipAddress, String userName, String password) throws Exception {
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
        return session;
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
    public String execCommand(String command,Session session2) throws IOException {
        InputStream in = null;// 输入流(读)
        Channel channel = null;// 定义channel变量
        String processDataStream = "";
        try {
            // 如果命令command不等于null
            if (command != null) {
                // 打开channel
                //说明：exec用于执行命令;sftp用于文件处理
                channel = session2.openChannel("exec");
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

    /**
     * 上传文件 可参考:https://www.cnblogs.com/longyg/archive/2012/06/25/2556576.html
     *
     * @param directory
     *            上传文件的目录
     * @param uploadFile
     *            将要上传的文件
     */
    public void uploadFile(String directory, String uploadFile) {
        try {
            // 打开channelSftp
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            // 远程连接
            channelSftp.connect();
            // 创建一个文件名称问uploadFile的文件
            File file = new File(uploadFile);
            // 将文件进行上传(sftp协议)
            // 将本地文件名为src的文件上传到目标服务器,目标文件名为dst,若dst为目录,则目标文件名将与src文件名相同.
            // 采用默认的传输模式:OVERWRITE
           // channelSftp.put(new FileInputStream(file), directory, ChannelSftp.OVERWRITE);
            channelSftp.put(new FileInputStream(file), directory, ChannelSftp.OVERWRITE);
            // 切断远程连接
            channelSftp.exit();
            System.out.println("2、" + file.getName() + " 文件上传成功.....");
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 上传文件 可参考:https://www.cnblogs.com/longyg/archive/2012/06/25/2556576.html
     *
     * @param directory
     *            上传文件的目录
     * @param uploadFile
     *            将要上传的文件
     */
    public  void uploadFile(String directory, InputStream inputStream) {
        try {
            // 打开channelSftp
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            // 远程连接
            channelSftp.connect();
            // 创建一个文件名称问uploadFile的文件
          //  File file = new File(uploadFile);
            // 将文件进行上传(sftp协议)
            // 将本地文件名为src的文件上传到目标服务器,目标文件名为dst,若dst为目录,则目标文件名将与src文件名相同.
            // 采用默认的传输模式:OVERWRITE
           // channelSftp.put(new FileInputStream(file), directory, ChannelSftp.OVERWRITE);
            channelSftp.put(inputStream, directory, ChannelSftp.OVERWRITE);
            // 切断远程连接
            channelSftp.exit();
            System.out.println("2、" +  " 文件上传成功.....");
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }

    /**
     * 下载文件 采用默认的传输模式：OVERWRITE
     *
     * @param src
     *            linux服务器文件地址
     * @param dst
     *            本地存放地址
     * @throws JSchException
     * @throws SftpException
     */
    public void fileDownload(String src, String dst) throws JSchException, SftpException {
        // src 是linux服务器文件地址,dst 本地存放地址
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        // 下载文件,多个重载方法
        channelSftp.get(src, dst);
        // 切断远程连接,quit()等同于exit(),都是调用disconnect()
        channelSftp.quit();
        // channelSftp.disconnect();
        System.out.println("3、" + src + " ,下载文件成功.....");
    }

    /**
     * 删除文件
     *
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     * @param sftp
     * @throws SftpException
     * @throws JSchException
     */
    public void deleteFile(String directoryFile) throws SftpException, JSchException {
        // 打开openChannel的sftp
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        // 删除文件
        channelSftp.rm(directoryFile);
        // 切断远程连接
        channelSftp.exit();
        System.out.println("4、" + directoryFile + " 删除的文件.....");
    }

    /**
     * 列出目录下的文件
     *
     * @param directory
     *            要列出的目录
     * @param sftp
     * @return
     * @throws SftpException
     * @throws JSchException
     */
    public JSONArray listFiles(String directory) throws JSchException, SftpException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
           // 远程连接
        channelSftp.connect();
        // 显示目录信息
        Vector ls = channelSftp.ls(directory);
        JSONArray jSONArray = new JSONArray();


        String[] split = ls.toString().replace("]","").split(",");

        for (int i = 2; i <split.length ; i++) {
            String fileName = split[i].substring(split[i].lastIndexOf(" "), split[i].length());
            JSONObject json = new JSONObject();
            json.put("id",(directory.trim()+"/"+fileName.trim()).replaceAll("//","/"));
            json.put("name",fileName);
            System.out.println((directory.trim()+"/"+fileName.trim()).replaceAll("//","/"));
            json.put("isParent",isExistDir((directory.trim()+"/"+fileName.trim()).replaceAll("//","/"),channelSftp));
            System.out.println(json);
            jSONArray.add(json);
        }


       // System.out.println("5、" + ls);
        // 切断连接
        channelSftp.exit();



        return jSONArray;
    }
    public boolean isExistDir(String path,ChannelSftp sftp){
        boolean  isExist=false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(path);
            isExist = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isExist = false;
            }
        }
        return isExist;

    }

    @RequestMapping("/uploadToFile")
    @ResponseBody
    public String uploadToUser(@RequestParam("file") MultipartFile file,@RequestParam("vmIp")String vmIp, HttpServletRequest req, Model model) {

        String fileName = file.getOriginalFilename();
        if (fileName.indexOf("\\") != -1) {
            fileName = fileName.substring(fileName.lastIndexOf("\\"));
        }
        String oriFileName = file.getOriginalFilename();
        String file1 = "/home/"+oriFileName;
        try {
            sshRemoteCall.uploadFile(file1,file.getInputStream());


       // String command = "kubectl cp "+file1+" "+vmName+":/home/";

      //  SSHRemoteCall.getInstance().execCommand(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ""; // 返回文件地址
    }

    private  Session getSession(String containerId, String hostIp) throws Exception {
        Session session2 = null;
        if(!sshMap.containsKey(containerId)){
            Session session = this.getInstance2().sshRemoteCallLogin(hostIp, userName, password);
            sshMap.put(containerId,session);
            session2 = session;
        }else{
            session2 = sshMap.get(containerId);
            if(!session2.isConnected()){
                Session session = this.getInstance2().sshRemoteCallLogin(hostIp, userName, password);
                sshMap.put(containerId,session);
                session2 = session;
            }
        }
        return session2;
    }

    @ResponseBody
    @RequestMapping("/getFileDirectory")
    public JSONArray getFileDirectory(HttpServletRequest request) throws Exception, JSchException {
        JSONArray jSONArray = new JSONArray();
        String id = request.getParameter("id");
        String hostIp = request.getParameter("hostIp");
        String containerId = request.getParameter("containerId");
        SSHRemoteCall2 sshRemoteCall = null;
        Session session2 = this.getSession(containerId, hostIp);
        System.out.println(hostIp+"-------"+id+"----"+containerId);
        String fileDirectory ="";
        String isParent = " ^d";
        if(StringUtils.isBlank(id)){
            //第一次进来
            for (int j = 0; j <2 ; j++) {
                String command = "docker exec  "+ containerId+ " ls -l |grep "+isParent;
                System.out.println(command);
                String files = this.execCommand(command,session2);
                String[] split = files.split(",");
                for (int i = 0; i <split.length ; i++) {
                    String fileName = split[i].substring(split[i].lastIndexOf(" "), split[i].length());
                    JSONObject json = new JSONObject();
                    json.put("name", fileName);
                    json.put("id", ( "/" + fileName.trim()));
                    json.put("name", fileName);
                    json.put("isParent", isParent.equals(" ^d") ? true : false);
                    jSONArray.add(json);
                }
                isParent = " ^-";
            }

        }else{
            fileDirectory = id.trim();
            for (int j = 0; j <2 ; j++) {
                String command = "docker exec  " + containerId + " ls " + fileDirectory + " ls -l | grep "+isParent;
                System.out.println(command);
                String fileDirectorys = this.execCommand(command,session2);
                if(StringUtils.isEmpty(fileDirectorys)){
                    isParent = " ^-";
                    continue;
                }
                String[] split = fileDirectorys.trim().split(",");
                for (int i = 0; i < split.length; i++) {
                    String fileName = split[i].substring(split[i].lastIndexOf(" "), split[i].length());
                    JSONObject json = new JSONObject();
                    json.put("name", fileName);
                    json.put("id", (fileDirectory.trim()+"/" + fileName.trim()));
                    json.put("name", fileName);
                    json.put("isParent", isParent.equals(" ^d") ? true : false);
                    jSONArray.add(json);
                }
                isParent = " ^-";
            }
        }
       //  SSHRemoteCall.getInstance().closeSession();
        return jSONArray;
    }



    @ResponseBody
    @RequestMapping("/fileupload")
    public void uploadPic(HttpServletRequest request, String lastRealPath) throws Exception {
        try{
            //强制转换request
            MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
            String vmIp = request.getParameter("vmIp");
            KubeVm kubeVm = new KubeVm();
            kubeVm.setVmIp(vmIp);

            List<KubeVm> list = kubeVmService.findList(kubeVm);
            String vmName = list.get(0).getVmName();

            //从表单获得文件
            //获得文件类型input的name
            Iterator<String> iter = mr.getFileNames();
            String inputName = iter.next();
            //获得文件
            MultipartFile mf = mr.getFile(inputName);
            //获得后缀名
            String oriFileName = mf.getOriginalFilename();
            // 1、首先远程连接ssh
           // SSHRemoteCall.getInstance().sshRemoteCallLogin(ipAddress, userName, password);
            // 打印信息
            System.out.println("0、连接,ip地址: " + ipAddress + ",账号: " + userName + ",连接成功....."+"-----文件名--"+oriFileName);

            String file = "/home/"+oriFileName;

            sshRemoteCall.uploadFile(file,mf.getInputStream());

            String command = "kubectl cp "+file+" "+vmName+":/home/";

          //  SSHRemoteCall.getInstance().execCommand(command);

          }catch (IOException e) {
            e.printStackTrace();
        }

    }







    public static void main(String[] args) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        // headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String u = "http://192.168.103.236/api/projects";
        MultiValueMap<Object, Object> params2 = new LinkedMultiValueMap<>();
        params2.add("project_name","test2");

        headers.add("authorization", "Basic " +  "YWRtaW46SGFyYm9yMTIzNDU=");
        HttpEntity<MultiValueMap<String, String>> requestEntity2 = new HttpEntity(params2, headers);
        ResponseEntity<JSONObject> exchange = client.exchange(u, method, requestEntity2, JSONObject.class);
        System.out.println(exchange);

//        // 连接到指定的服务器
//        try {
//            SSHRemoteCall.getInstance().sshRemoteCallLogin(ipAddress, userName, password);
//
//            JSONArray jSONArray = new JSONArray();
//            String isParent = " ^d";
//            //第一次进来
//
//            String cid = "0b4d03bba8ba";
//            for (int j = 0; j <2 ; j++) {
//                String command = "docker exec  "+ cid+ " ls -l |grep "+isParent;
//                System.out.println(command);
//                String files = SSHRemoteCall.getInstance().execCommand(command);
//                String[] split = files.split(",");
//                for (int i = 0; i <split.length ; i++) {
//                    String fileName = split[i].substring(split[i].lastIndexOf(" "), split[i].length());
//                    JSONObject json = new JSONObject();
//                    json.put("name", fileName);
//                    json.put("id", ( "//" + fileName.trim()).replaceAll("//", "/"));
//                    json.put("name", fileName);
//                    // System.out.println((fileDirectory.trim() + "/" + fileName.trim()).replaceAll("//", "/"));
//                    json.put("isParent", isParent.equals(" ^d") ? true : false);
//                    jSONArray.add(json);
//                }
//                isParent = " ^-";
//            }
//
//            System.out.println(jSONArray);
            // 2、执行相关的命令
            // 查看目录信息
            // String command = "ls /home/hadoop/package ";
            // 查看文件信息
            // String command = "cat /home/hadoop/package/test ";
            // 查看磁盘空间大小
            // String command = "df -lh ";
            // 查看cpu的使用情况
            // String command = "top -bn 1 -i -c ";
            // 查看内存的使用情况
         //   String command = "kubectl cp /root/centos/wget-log fba45d0788a54336b45e13a6a4ebf5de-bd54dc4d7-47544:/usr/";
        //    SSHRemoteCall.getInstance().execCommand(command);
          //  SSHRemoteCall.getInstance().execCommand("mkdir /hoem/ssg3");


             /*   // 4、下载文件
            // src 是linux服务器文件地址,dst 本地存放地址,采用默认的传输模式：OVERWRITE
            //test为文件名称哈
            String src = "/home/hadoop/package/test";
            String dst = "E:\\";
            SSHRemoteCall.getInstance().fileDownload(src, dst);

            // 5、刪除文件
            String deleteDirectoryFile = "/home/hadoop/package/test";
            SSHRemoteCall.getInstance().deleteFile(deleteDirectoryFile);

            // 6、展示目录下的文件信息
            String lsDirectory = "/home/hadoop/package";
            SSHRemoteCall.getInstance().listFiles(lsDirectory);*/

            // 7、关闭连接
          //  SSHRemoteCall.getInstance().closeSession();
       // } catch (Exception e) {
            // 打印错误信息
            System.err.println("远程连接失败......");


    }

}
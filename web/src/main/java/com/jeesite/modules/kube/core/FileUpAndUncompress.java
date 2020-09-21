package com.jeesite.modules.kube.core;

/**
 * @ClassName: FileUpAndUncompress
 * @Description: TODO
 * @Author: shugen
 * @Date: 2020/8/21 16:16
 */
import java.io.*;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


import java.net.SocketException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by ck on 2017/3/8.
 */
public class FileUpAndUncompress {
    /**
     * 文件上传
     */
    public static void fileUploadByFtp(){
        File imagefile = new File("E:\\petition1.rar");
        String imagefileFileName = "petition1.rar";
//创建ftp客户端
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("GBK");
        String hostname = "192.168.103.236";
        int port = 22;
        String username = "root";
        String password = "123456";
        try {
//链接ftp服务器
            ftpClient.connect(hostname, port);
//登录ftp
            ftpClient.login(username, password);
            ftpClient.setDataTimeout(60000); //设置传输超时时间为60秒
            ftpClient.setConnectTimeout(60000); //连接超时为60秒
            int reply = ftpClient.getReplyCode();
            System.out.println(reply);
//如果reply返回230就算成功了，如果返回530密码用户名错误或当前用户无权限下面有详细的解释。
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return ;
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            ftpClient.makeDirectory("path");//在root目录下创建文件夹
// String remoteFileName = System.currentTimeMillis()+"_"+imagefileFileName;
            InputStream input = new FileInputStream(imagefile);
            ftpClient.storeFile(imagefileFileName, input);//文件你若是不指定就会上传到root目录下
            input.close();
            ftpClient.logout();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (ftpClient.isConnected())
            {
                try
                {
                    ftpClient.disconnect();
                } catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }

        }
    }


    /**
     * 解压zip文件
     * @param sourceFile,待解压的zip文件; toFolder,解压后的存放路径
     * @throws Exception
     **/

    public static void zipToFile(String sourceFile, String toFolder) throws Exception {
        String toDisk = toFolder;//接收解压后的存放路径
        ZipFile zfile = new ZipFile(sourceFile);//连接待解压文件
        System.out.println("要解压的文件是:"+ zfile.getName());
        Enumeration zList = zfile.entries();//得到zip包里的所有元素
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                System.out.println("打开zip文件里的文件夹:"+ ze.getName() +"skipped...");
                continue;
            }
            System.out.println("zip包里的文件:"+ ze.getName() +" "+"大小为:" + ze.getSize() +"KB");
//以ZipEntry为参数得到一个InputStream，并写到OutputStream中
            OutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(getRealFileName(toDisk, ze.getName())));
            InputStream inputStream = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = inputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, readLen);
            }
            inputStream.close();
            outputStream.close();
            System.out.println("已经解压出:"+ ze.getName());
        }
        zfile.close();
    }

    /**

     * 给定根目录，返回一个相对路径所对应的实际文件名.

     * @param zippath 指定根目录

     * @param absFileName 相对路径名，来自于ZipEntry中的name

     * @return java.io.File 实际的文件

     */

    private static File getRealFileName(String zippath, String absFileName){
        String[] dirs = absFileName.split("/", absFileName.length());
        File ret = new File(zippath);// 创建文件对象
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {// 检测文件是否存在
            ret.mkdirs();// 创建此抽象路径名指定的目录
        }
        ret = new File(ret, dirs[dirs.length - 1]);// 根据 ret 抽象路径名和 child 路径名字符串创建一个新 File 实例
        return ret;
    }

    /**
     * 远程解压zip文件
     */
    public static void remoteZipToFile(){
        try {
            Connection connection = new Connection("192.168.103.236");// 创建一个连接实例
            connection.connect();// Now connect
            boolean isAuthenticated = connection.authenticateWithPassword("root", "123456");// Authenticate
            if (isAuthenticated == false)throw new IOException("user and password error");
            Session sess = connection.openSession();// Create a session
            System.out.println("start exec command.......");
            sess.requestPTY("bash");
            sess.startShell();
            InputStream stdout = new StreamGobbler(sess.getStdout());
            InputStream stderr = new StreamGobbler(sess.getStderr());
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
            PrintWriter out = new PrintWriter(sess.getStdin());
            out.println("cd /root/");
            out.println("ll");
          //  out.println("unzip -o -d /root/path logo.zip");
            out.println("ll");
            out.println("exit");
            out.close();
            sess.waitForCondition(ChannelCondition.CLOSED|ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,30000);
            System.out.println("下面是从stdout输出:");
            while (true) {
                String line = stdoutReader.readLine();
                if (line == null)break;
                System.out.println(line);
            }
            System.out.println("下面是从stderr输出:");
            while (true) {
                String line = stderrReader.readLine();
                if (line == null)break;
                System.out.println(line);
            }
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close();/* Close this session */
            connection.close();/* Close the connection */
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }



    public static void main(String[] args) throws Exception {
fileUploadByFtp();
// zipToFile("D:\\logo.zip","D:\\webstorm");
      //  remoteZipToFile();
    }

}



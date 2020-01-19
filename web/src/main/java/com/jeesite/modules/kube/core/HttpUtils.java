package com.jeesite.modules.kube.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @ClassName HttpUtils
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2019/12/25 15:25
 */
public class HttpUtils {
    // http request
    public static String httpRequest(String requestUrl){
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            // http连接
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();

            //
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            //
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            //
            httpUrlConn.disconnect();

        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
}

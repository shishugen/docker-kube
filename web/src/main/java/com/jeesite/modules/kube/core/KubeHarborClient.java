package com.jeesite.modules.kube.core;


import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HarborClient
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2020/1/3 14:12
 */
public class KubeHarborClient {



    public static void main(String[] args) throws  Exception {
        KubeHarborClient kubeHarborClient = new KubeHarborClient();

        kubeHarborClient.loginTest();

    }
    public  static JSONObject login(String url, MultiValueMap<String, String> params){

        //将请求头部和参数合成一个请求
       // HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用JSONObject类格式化
       // ResponseEntity<JSONObject> response = client.exchange(url, method, requestEntity, JSONObject.class);
      //  System.out.println(response);


        return  null;
    }
    public void loginTest(){
        //http://192.168.103.236/harbor/sign-in?login_username=admin&login_password=Harbor12345
        String url= "http://192.168.103.236/c/login";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("principal","admin");
        params.add("password","Harbor12345");
        JSONObject result = login(url, params);
        System.out.println(result);

    }
}


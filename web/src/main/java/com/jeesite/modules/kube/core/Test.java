package com.jeesite.modules.kube.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.*;

/**
 * @ClassName: Test
 * @Description: TODO
 * @Author: shugen
 * @Date: 2020/8/21 9:46
 */
public class Test {
    public static void main(String[] args){

        try {
            test1();
            test4();
        }catch(Exception e){
            System.out.println(e);
        }


    }
    public static void test1(){

        System.out.println("test1");
        test2();
        test3();
    }

    public static void test2() {

        System.out.println("test2");
        try {
          int i =  1 / 0;
        }catch (Exception e){
            throw new KubeException("test2 异常");
        }


    }

    public static void test3(){

        System.out.println("test3");
        System.out.println("test3");
        System.out.println("test3");
        System.out.println("test3");
        System.out.println("test3");

    }
    public static void test4(){

        System.out.println("test4");


    }


}

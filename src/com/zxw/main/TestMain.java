package com.zxw.main;

import com.zxw.bean.FieldBean;
import com.zxw.command.DefaultCommand;
import com.zxw.utils.FileUtils;
import com.zxw.utils.OtherUtils;

import java.util.Map;

/**
 * @author 庄学文
 * @title
 * @email xuewen_zhuang@suishouji.com
 * @create 2017-12-13
 */
public class TestMain {
    public static void main(String[] args) {
        OtherUtils.loadConf(args[0]);
        /*testUpperCaseForeach();
        testLowerCaseForeach();*/
    }

    public static void testLowerCaseForeach() {
        Map<Integer, FieldBean> fieldBeanMap = OtherUtils.analysisJavabeanFileToMap("E:\\idea work\\JavaBeanDemo.java", "UTF-8");
        StringBuffer fileStringBuffer = FileUtils.read("E:\\idea work\\GetSetDemo.java", "UTF-8");
        fileStringBuffer = DefaultCommand.foreachCommand(fieldBeanMap, fileStringBuffer, 2);
        FileUtils.save(fileStringBuffer.toString(), "E:\\idea work\\GetSetDemo2.java", "UTF-8");
        System.out.println(fileStringBuffer);
    }

    public static void testUpperCaseForeach() {
        Map<Integer, FieldBean> fieldBeanMap = OtherUtils.analysisJavabeanFileToMap("E:\\idea work\\JavaBeanDemo.java", "UTF-8");
        StringBuffer fileStringBuffer = FileUtils.read("E:\\idea work\\demo.jsp", "UTF-8");
        fileStringBuffer = DefaultCommand.foreachCommand(fieldBeanMap, fileStringBuffer, 2);
        FileUtils.save(fileStringBuffer.toString(), "E:\\idea work\\demo2.jsp", "UTF-8");
        System.out.println(fileStringBuffer);
    }



    //删除
//        System.out.println(stringBuffer.delete(firstForeachIndexStart,stringBuffer.indexOf("\r\n",firstForeachIndexEnd)));
}

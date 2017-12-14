package com.main;

import com.bean.FieldBean;
import com.bean.MatchKeyBean;
import com.utils.FileUtils;
import com.utils.OtherUtils;

import java.util.Map;

/**
 * @author 庄学文
 * @title
 * @email xuewen_zhuang@suishouji.com
 * @create 2017-12-13
 */
public class TestMain {
    public static void main(String[] args) {
        // map是
        Map<Integer, FieldBean> fieldBeanMap = OtherUtils.analysisJavabeanFileToMap("E:\\idea work\\JavaBeanDemo.java", "UTF-8");
        StringBuffer fileStringBuffer = FileUtils.read("E:\\idea work\\demo.jsp", "UTF-8");
        MatchKeyBean matchKeyBean = OtherUtils.indexFirstMatchKey("[&foreach&]", "[/&foreach&]", fileStringBuffer.toString());
        StringBuilder buildTmpBuilder = new StringBuilder();
        String tplString=matchKeyBean.getPrintString();
        for (Map.Entry<Integer, FieldBean> entry : fieldBeanMap.entrySet()) {
            FieldBean fieldBean = entry.getValue();
            if (fieldBean.getFieldCode() != null && fieldBean.getFieldName() != null) {
                String tmp=tplString.replace("[&fieldName]", fieldBean.getFieldName());
                tmp=tmp.replace("[&fieldCode]",fieldBean.getFieldCode());
                buildTmpBuilder = buildTmpBuilder.append(tmp);
            }
        }
//        System.out.println(buildTmpBuilder);
        fileStringBuffer.delete(matchKeyBean.getStartKeyIndex(),matchKeyBean.getEndStringIndex());
        fileStringBuffer.insert(matchKeyBean.getStartKeyIndex(),buildTmpBuilder);
        FileUtils.save(fileStringBuffer.toString(),"E:\\idea work\\dataProductList2.jsp","UTF-8");
        System.out.println(fileStringBuffer);
      /*  if (matchKeyBean !=null){
            System.out.println(matchKeyBean);
        }*/
    }


    //删除
//        System.out.println(stringBuffer.delete(firstForeachIndexStart,stringBuffer.indexOf("\r\n",firstForeachIndexEnd)));
}

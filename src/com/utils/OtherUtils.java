package com.utils;

import com.bean.FieldBean;
import com.bean.MatchKeyBean;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 庄学文
 * @title
 * @email xuewen_zhuang@suishouji.com
 * @create 2017-12-14
 */
public class OtherUtils {
    /**
     * 字段code  Pattern
     */
    static Pattern FIELD_NAME_PATTERN = Pattern.compile("/\\*\\*[\\s\\S]*?\\*/", Pattern.MULTILINE | Pattern.DOTALL);
    /**
     * 字段中文解释 Pattern
     */
    static Pattern FIELD_CODE_PATTERN = Pattern.compile("private.*;");

    public static MatchKeyBean indexFirstMatchKey(String startKey, String endKey, String inputString) {
        MatchKeyBean matchKeyBean = new MatchKeyBean();
        int firstForeachIndexStart = inputString.indexOf(startKey);
        matchKeyBean.setStartKeyIndex(firstForeachIndexStart);
        int firstForeachIndexEnd;
        if (firstForeachIndexStart == -1) {
            return null;
        }
        firstForeachIndexEnd = inputString.indexOf(endKey, firstForeachIndexStart);
        if (firstForeachIndexEnd == -1) {
            return null;
        }
        matchKeyBean.setEndKeyIndex(firstForeachIndexEnd);
        matchKeyBean.setEndStringIndex(firstForeachIndexEnd + endKey.length());
        matchKeyBean.setPrintString(inputString.substring(firstForeachIndexStart + startKey.length(), firstForeachIndexEnd));
        return matchKeyBean;
    }

    public static Map<Integer, FieldBean> analysisJavabeanFileToMap(String pathName, String readCharsetName) {
        Map<Integer, FieldBean> map = new LinkedHashMap<>();
        try {
            StringBuffer stringBuffer = FileUtils.read(pathName, readCharsetName);
            String tmpString = stringBuffer.toString();
            //匹配字段中文解释
            Matcher fieldNameMatcher = FIELD_NAME_PATTERN.matcher(tmpString);
            while (fieldNameMatcher.find()) {
                if (fieldNameMatcher.group().contains("serialVersionUID")) {
                    continue;
                }
                int endIndex = fieldNameMatcher.end(0);
                String fieldName = fieldNameMatcher.group();
                //截出字段中文解释
                fieldName = fieldName.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5-():：]", "");
                FieldBean fieldBean = new FieldBean(null, fieldName);
                //endIndex +6 = fieldCodeMatcher中的key 其中6实际是\r\n +4 其中4是\t的规范约定实现 4个空格
                map.put(endIndex + 6, fieldBean);
            }
            //匹配字段code
            Matcher fieldCodeMatcher = FIELD_CODE_PATTERN.matcher(tmpString);
            while (fieldCodeMatcher.find()) {
                if (fieldCodeMatcher.group().contains("serialVersionUID")) {
                    continue;
                }
                int key = fieldCodeMatcher.start(0);
                //截出字段code
                String fieldCode = fieldCodeMatcher.group();
                fieldCode = fieldCode.substring(fieldCode.lastIndexOf(" ") + 1, fieldCode.length() - 1);
                FieldBean fieldBean = map.get(key);
                if (fieldBean != null) {
                    fieldBean.setFieldCode(fieldCode);
                } else {
                    map.put(key, new FieldBean(fieldCode, null));
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

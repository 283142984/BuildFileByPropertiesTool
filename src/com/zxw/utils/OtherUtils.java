package com.zxw.utils;

import com.zxw.bean.FieldBean;
import com.zxw.bean.MatchKeyBean;

import java.io.*;
import java.util.*;
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

    /**
     * 找到输入中startkey和endkey对应的位置并组装成MatchKeyBean对象返回
     *
     * @param startKey    开始字符 （要和结束字符配对）
     * @param endKey      结束字符
     * @param inputString 输入String
     * @return MatchKeyBean
     * @author 庄学文
     */
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

    /**
     * 解析javabean的文件目录成一个Map<Integer 索引位置，FieldBean 字段对应bean>
     *
     * @param pathName        文件绝对路径
     * @param readCharsetName 文件编码
     * @return Map<Integer                                                               索引位置                               ，                               FieldBean                                                               字段对应bean>
     * @author 庄学文
     */
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

    /**
     * 替换第一次遇到字符
     *
     * @param inputString 输入字符
     * @param target      替换字符命令 支持多个可以用逗号隔开 如 \t,\n
     * @param replacement 替换成字符   只支持一个
     * @param fromIndex   开始索引位置
     * @return Map<Integer                                                               索引位置                               ，                               FieldBean                                                               字段对应bean>
     * @author 庄学文
     */
    public static String replaceFirstString(String inputString, String target, String replacement, int fromIndex) {
        if (inputString == null || target == null || replacement == null) {
            throw new NullPointerException("替换第一次遇到字符 转入参数有空");
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        int replacementLength = replacement.length();
        StringBuilder out = new StringBuilder(inputString);
        String[] targetSplit = target.split(",");
        System.out.println(Arrays.toString(targetSplit));
//        if(targetSplit)
        for (int i = 0; i < targetSplit.length; i++) {
            String tmpTarget = targetSplit[i];
            int index = out.indexOf(tmpTarget, fromIndex);
            if (index >= 0) {
                out.delete(index, index + tmpTarget.length()).insert(index, replacement);
                fromIndex = fromIndex - (tmpTarget.length() - replacementLength);
            }
        }
        return out.toString();
    }

    public static Map<String, String> loadConf(String path) {
        if (path == null || path.trim().equals("") || !path.endsWith(".properties")) {
            return null;
        }
        Map<String, String> propsMap = new HashMap<>();
        Properties props = new Properties();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(path));
            props.load(reader);
            Iterator<String> it = props.stringPropertyNames().iterator();
            while (it.hasNext()) {
                String key = it.next();
                propsMap.put(key, props.getProperty(key));
                System.out.println(key + ":" + props.getProperty(key));
            }
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
            return propsMap;
        }

    }
}

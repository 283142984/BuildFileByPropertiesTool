package com.constants;
/**
 * @author 庄学文
 * @title 模板常量，未来写成可以配置，现在可以作为默认值
 * @email xuewen_zhuang@suishouji.com
 * @create 2017-12-13
 */
public class TplConstants {
    /**
     * foreach 命令 匹配开始符
     */
    public static final String FOREACH_START_COMMAND ="[&foreach&]";
    /**
     * foreach 命令 匹配结束符
     */
    public static final String FOREACH_END_COMMAND ="[/&foreach&]";
    /**
     * 字段name 普通 匹配符
     */
    public static final String FIELD_NAME_NORMAL=  "[&fieldName]";
    /**
     * 字段code 普通 匹配符
     */
    public static final String FIELD_CODE_NORMAL =  "[&fieldCode]";

}

package com.command;

import com.bean.FieldBean;
import com.bean.MatchKeyBean;
import com.constants.TplConstants;
import com.utils.OtherUtils;

import java.util.Map;

/**
 * @author 庄学文
 * @title 默认命令
 * @email xuewen_zhuang@suishouji.com
 * @create 2017-12-15
 */
public class DefaultCommand {
    /**
     * @param fieldBeanMap     利用OtherUtils.analysisJavabeanFileToMap解析的bean对应map
     * @param fileStringBuffer 要处理的String
     * @param useNumber        调用次数 如果小于0也默认为1
     * @author 庄学文
     * @title 默认命令 foreach
     */
    public static StringBuffer foreachCommand(Map<Integer, FieldBean> fieldBeanMap, StringBuffer fileStringBuffer, int useNumber) {
        if (useNumber <= 0) {
            useNumber = 1;
        }
        StringBuffer outStringBuffer = new StringBuffer(fileStringBuffer);
        for (; useNumber > 0; useNumber--) {
            MatchKeyBean matchKeyBean = OtherUtils.indexFirstMatchKey(TplConstants.FOREACH_START_COMMAND, TplConstants.FOREACH_END_COMMAND, outStringBuffer.toString());
            if (matchKeyBean == null) {
                return outStringBuffer;
            }
            StringBuffer buildTmpBuffer = new StringBuffer();
            String tplString = matchKeyBean.getPrintString();
            for (Map.Entry<Integer, FieldBean> entry : fieldBeanMap.entrySet()) {
                FieldBean fieldBean = entry.getValue();
                if (fieldBean.getFieldCode() != null && fieldBean.getFieldName() != null) {
                    String tmp = tplString.replace(TplConstants.FIELD_NAME_NORMAL, fieldBean.getFieldName());
                    tmp = tmp.replace(TplConstants.FIELD_CODE_NORMAL, fieldBean.getFieldCode());
                    buildTmpBuffer = buildTmpBuffer.append(tmp);
                }
            }
            outStringBuffer.delete(matchKeyBean.getStartKeyIndex(), matchKeyBean.getEndStringIndex());
            outStringBuffer.insert(matchKeyBean.getStartKeyIndex(), buildTmpBuffer);
        }
        return outStringBuffer;
    }

    /**
     * @param fieldBeanMap     利用OtherUtils.analysisJavabeanFileToMap解析的bean对应map
     * @param fileStringBuffer 要处理的String
     * @author 庄学文
     * @title 默认命令 foreach
     */
    public static StringBuffer foreachCommand(Map<Integer, FieldBean> fieldBeanMap, StringBuffer fileStringBuffer) {
        return foreachCommand(fieldBeanMap, fileStringBuffer);
    }
}

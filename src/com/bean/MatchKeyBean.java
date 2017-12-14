package com.bean;

/**
 * @author 庄学文
 * @title
 * @email xuewen_zhuang@suishouji.com
 * @create 2017-12-13
 */
public class MatchKeyBean {
    /**
     * 匹配后 key之间的 输出String
     */
    private String printString;
    /**
     * 开始符号 匹配位置 默认-1
     */
    private int startKeyIndex=-1;
    /**
     * 结束符号 匹配位置 默认-1
     */
    private int endKeyIndex=-1;

    /**
     * 文段结束位置 ：结束符号匹配位置 +匹配符长度  默认-1
     */
    private int endStringIndex=-1;

    public String getPrintString() {
        return printString;
    }

    public void setPrintString(String printString) {
        this.printString = printString;
    }

    public int getStartKeyIndex() {
        return startKeyIndex;
    }

    public void setStartKeyIndex(int startKeyIndex) {
        this.startKeyIndex = startKeyIndex;
    }

    public int getEndKeyIndex() {
        return endKeyIndex;
    }

    public void setEndKeyIndex(int endKeyIndex) {
        this.endKeyIndex = endKeyIndex;
    }

    public int getEndStringIndex() {
        return endStringIndex;
    }

    public void setEndStringIndex(int endStringIndex) {
        this.endStringIndex = endStringIndex;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MatchKeyBean{");
        sb.append("printString='").append(printString).append('\'');
        sb.append(", startKeyIndex=").append(startKeyIndex);
        sb.append(", endKeyIndex=").append(endKeyIndex);
        sb.append(", endStringIndex=").append(endStringIndex);
        sb.append('}');
        return sb.toString();
    }
}

package com.zxw.bean;


import javax.swing.*;
import java.io.Serializable;

/**
 * 保存要新旧路径  序列化类 用于恢复现场
 */
public class PathPaneBean implements Serializable {

    private JTextArea oldPathNametextArea = new JTextArea();//原文件目录
    private JTextArea newPathNameTextArea = new JTextArea();//新文件目录

    public PathPaneBean() {
    }

    public PathPaneBean(JTextArea oldPathNametextArea,
                        JTextArea newPathNameTextArea) {
        super();
        this.oldPathNametextArea = oldPathNametextArea;
        this.newPathNameTextArea = newPathNameTextArea;
    }

    public JTextArea getOldPathNameTextArea() {
        return oldPathNametextArea;
    }

    public void setOldPathNametextArea(JTextArea oldPathNameTextArea) {
        this.oldPathNametextArea = oldPathNameTextArea;
    }

    public JTextArea getNewPathNameTextArea() {
        return newPathNameTextArea;
    }

    public void setNewPathNameTextArea(JTextArea newPathNameTextArea) {
        this.newPathNameTextArea = newPathNameTextArea;
    }

}

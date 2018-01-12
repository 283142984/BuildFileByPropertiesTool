package com.zxw.main;

import com.zxw.bean.FieldBean;
import com.zxw.bean.PathPaneBean;
import com.zxw.bean.ReNamePaneBean;
import com.zxw.command.DefaultCommand;
import com.zxw.utils.FileUtils;
import com.zxw.utils.OtherUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MainJPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    static JFrame frame;
    JTextArea javaBeanNameTextArea = new JTextArea("");
    private JButton browseButton = new JButton("选择配置文件");
    private JButton chooseJavaBeanButton = new JButton("重选javaBean文件");
    private JButton addReNameButton = new JButton("添加替换字段");
    private JButton addTplFileButton = new JButton("添加模板文件");
    private JPanel northPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel centerPathPanel = new JPanel();//中间右方路径设置区域
    private JPanel centerButtonPanel = new JPanel();//中间按钮区域

    //    private Map<Integer, String> pathIndexes = new HashMap<Integer, String>();
    public JTextArea oldFileNameTextArea = new JTextArea();//旧文件名Po
    public JTextArea newFileNameTextArea = new JTextArea();//新文件名Po
    private JLabel oldFileNameLabel = new JLabel("文件旧字段:");
    private JLabel newFileNameLabel = new JLabel("文件替换后字段:");
    public JTextArea textArea;//底部文本显示

    public Map<Integer, ReNamePaneBean> reNamePaneBeanMap = new LinkedHashMap<>();//保存reName对象 Map
    public Map<String, PathPaneBean> pathPaneBeanMap = new ConcurrentHashMap<>();//保存path文件路径对象 Map
    private Map<Integer, FieldBean> fieldBeanMap = null;//保存bean文件路径对象 Map
    public Map<String, String> propertiesMap = null;//保存配置文件配置对象 Map
    private JButton reloadFileNameOldButton = new JButton("重写文件名");
    private JButton buildPropertiesButton = new JButton("生成新配置文件");
    private JButton outButton = new JButton("生成文件");
    private String charsetName = "UTF-8";
    private MainJPanel mainJPanel;//自身引用
    private String tplPath;
    private String beanPath;

    public MainJPanel() {
        initGui();
        mainJPanel = this;
    }

    // 初始化界面
    private void initGui() {
        this.setLayout(new BorderLayout());


//        JScrollPane northPane = new JScrollPane(northPanel);
//        northPane.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        northPane.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        northPane.setPreferredSize(new Dimension(800, 100));
        loadNorthPanel();
        this.add(northPanel, BorderLayout.NORTH);


        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(centerButtonPanel, BorderLayout.SOUTH);
        centerButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        centerButtonPanel.add(reloadFileNameOldButton);
        centerButtonPanel.add(buildPropertiesButton);
        centerButtonPanel.add(outButton);


        JScrollPane pane = new JScrollPane(centerPathPanel);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        centerPanel.add(pane, BorderLayout.CENTER);
        loadCenterPathPanel();
        this.add(centerPanel, BorderLayout.CENTER);


        //南边
        textArea = new JTextArea();
        textArea.setFont(new Font("细明体", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        JScrollPane panel = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.setPreferredSize(new Dimension(0, 300));
        this.add(panel, BorderLayout.SOUTH);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 选择配置文件
                final JFileChooser chooser = new JFileChooser();
//                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setFileFilter(new FileNameExtensionFilter("properties", new String[]{"properties"}));
                int result = chooser.showOpenDialog(MainJPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File dir = chooser.getSelectedFile();
                            propertiesMap = OtherUtils.loadConf(dir.getAbsolutePath());
                            if (propertiesMap == null) {
                                JOptionPane.showMessageDialog(null, "配置读取失败！请重新选择", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                            tplPath = propertiesMap.get("tplPath_criterion");
                            pathPaneBeanMap = new ConcurrentHashMap<>();
                            File file = new File(tplPath);
                            File[] fList = file.listFiles();

                            // 输出所有文件的名字
                            for (File f : fList) {
                                // 是文件吗？
                                if (f.isFile()) {
                                    String propertiesPath = propertiesMap.get(f.getName());
                                    if (propertiesPath != null) {
                                        pathPaneBeanMap.put(f.getName(), new PathPaneBean(new JTextArea(f.getAbsolutePath()), new JTextArea(propertiesPath)));
                                    } else {
                                        //TODO:默认位置未来处理
                                        pathPaneBeanMap.put(f.getName(), new PathPaneBean(new JTextArea(f.getAbsolutePath()), new JTextArea("")));
                                    }
                                }
                            }
                            beanPath = propertiesMap.get("beanPath_criterion");
                            fieldBeanMap = OtherUtils.analysisJavabeanFileToMap(beanPath, charsetName);
                            if (fieldBeanMap == null) {
                                JOptionPane.showMessageDialog(null, "配置bean信息读取失败！手动选择javabean", "错误", JOptionPane.ERROR_MESSAGE);
                            }
//                            String beanName = beanPath.substring(beanPath.lastIndexOf("\\") + 1);
                            javaBeanNameTextArea.setText(beanPath);
                            loadCenterPathPanel();
                        }
                    });
                    t.start();
                }
            }
        });

        chooseJavaBeanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadJavaBeanFile();
            }
        });

        addReNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer MaxKey = 0;
                for (Integer key : reNamePaneBeanMap.keySet()) {
                    MaxKey = key;
                }
                reNamePaneBeanMap.put(MaxKey + 1, new ReNamePaneBean());
                loadNorthPanel();
            }
        });
        //添加模板文件
        addTplFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // 添加模板文件
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = chooser.showOpenDialog(MainJPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File f = chooser.getSelectedFile();
                            String propertiesPath = propertiesMap.get(f.getName());
                            if (propertiesPath != null) {
                                pathPaneBeanMap.put(f.getName(), new PathPaneBean(new JTextArea(f.getAbsolutePath()), new JTextArea(propertiesPath)));
                            } else {
                                //TODO:默认位置未来处理
                                pathPaneBeanMap.put(f.getName(), new PathPaneBean(new JTextArea(f.getAbsolutePath()), new JTextArea("")));
                            }
                            loadCenterPathPanel();
                        }
                    });
                    t.start();
                }

            }
        });

        reloadFileNameOldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldFileName = oldFileNameTextArea.getText();
                String newFileName = newFileNameTextArea.getText();
                for (String key : pathPaneBeanMap.keySet()) {
                    PathPaneBean pathPaneBean = pathPaneBeanMap.get(key);
                    String newFilePath = pathPaneBean.getNewPathNameTextArea().getText();
                    if (newFilePath == null || newFilePath.trim().equals("")) {
                        continue;
                    }
                    newFilePath = newFilePath.replace(oldFileName, newFileName);
                    pathPaneBean.getNewPathNameTextArea().setText(newFilePath);
                    loadCenterPathPanel();
                }
                JOptionPane.showMessageDialog(null, "成功刷新！", "成功", JOptionPane.OK_OPTION);
            }
        });
        buildPropertiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Properties props = new Properties();
                props.setProperty("tplPath_criterion", tplPath);
                props.setProperty("beanPath_criterion", beanPath);
                for (Map.Entry<String, PathPaneBean> entry : pathPaneBeanMap.entrySet()) {
                    props.setProperty(entry.getKey(), entry.getValue().getOldPathNameTextArea().getText());
                }
                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("a.properties")); //设置默认文件名
                fc.setDialogTitle("保存文件");
                fc.setMultiSelectionEnabled(false);
//                fc.showSaveDialog(fc);
                fc.showDialog(fc, "保存文件");
                if (fc.getSelectedFile() == null) {
                    JOptionPane.showMessageDialog(null, "没有选定文件！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                FileOutputStream oFile = null;
                try {
                    oFile = new FileOutputStream(fc.getSelectedFile().getPath());
                    props.store(oFile, "buildByTool");
                    JOptionPane.showMessageDialog(null, "成功！", "成功", JOptionPane.OK_OPTION);
                    oFile.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        if (oFile != null) {
                            oFile.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            //TODO：预留
            /*{
                if (reNamePaneBeanMap.size() == 0) {
                    JOptionPane.showMessageDialog(null, "没有添加设置要替换的字段！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (pathPaneBeanMap.size() == 0) {
                    JOptionPane.showMessageDialog(null, "没有选定文件！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (String key : pathPaneBeanMap.keySet()) {
                    PathPaneBean pathPaneBean = pathPaneBeanMap.get(key);
                    String filePath = pathPaneBean.getOldPathNameTextArea().getText();
                    String fileContent = FileUtils.read(filePath, charsetName).toString();
                    for (Integer k : reNamePaneBeanMap.keySet()) {
                        ReNamePaneBean reNamePaneBean = reNamePaneBeanMap.get(k);
                        fileContent = fileContent.replace(reNamePaneBean.getOldNametextArea().getText(),
                                reNamePaneBean.getNewNametextArea().getText());

                    }
                    FileUtils.save(fileContent, filePath, charsetName);
                }
                JOptionPane.showMessageDialog(null, "成功替换！", "成功", JOptionPane.OK_OPTION);

            }*/
        });
        outButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
         /*       if (reNamePaneBeanMap.size() == 0) {
                    JOptionPane.showMessageDialog(null, "没有添加设置要替换的字段！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }*/
                if (pathPaneBeanMap.size() == 0) {
                    JOptionPane.showMessageDialog(null, "没有选定文件！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (String key : pathPaneBeanMap.keySet()) {
                    PathPaneBean pathPaneBean = pathPaneBeanMap.get(key);
                    String oldFilePath = pathPaneBean.getOldPathNameTextArea().getText();
                    String newFilePath = pathPaneBean.getNewPathNameTextArea().getText().trim();
                    File newFile = new File(newFilePath);
                    File fileParent = newFile.getParentFile();
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                    FileUtils.fileChannelCopy(new File(oldFilePath), newFile);
                    String fileContent = getBuildString(pathPaneBean);
                    FileUtils.save(fileContent, newFilePath, charsetName);
                }
                JOptionPane.showMessageDialog(null, "成功生成文件！", "成功", JOptionPane.OK_OPTION);


            }
        });
    }

    //选择javabean文件读取配置到Map
    private void loadJavaBeanFile() {
        // 如果上面目录有路径就直接使用
        String javaBeanPathStr=javaBeanNameTextArea.getText();
        if(javaBeanPathStr!=null&&!javaBeanPathStr.trim().equals("")){
            beanPath=javaBeanPathStr;
            fieldBeanMap = OtherUtils.analysisJavabeanFileToMap(beanPath, charsetName);
            if (fieldBeanMap == null) {
                JOptionPane.showMessageDialog(null, "配置bean信息读取失败！请重新选择", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(null, "通过自定义路径重加载成功，如果需要通过目录选请清空自定义路径栏", "成功说明", JOptionPane.OK_OPTION);
        }
        else {
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(MainJPanel.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File dir = chooser.getSelectedFile();
                        beanPath = dir.getAbsolutePath();
                        fieldBeanMap = OtherUtils.analysisJavabeanFileToMap(beanPath, charsetName);
                        if (fieldBeanMap == null) {
                            JOptionPane.showMessageDialog(null, "配置bean信息读取失败！请重新选择", "错误", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
//                    String beanName = dir.getName();
                        javaBeanNameTextArea.setText(dir.getAbsolutePath());
                    }
                });
                t.start();
            }
        }
    }

    //生产的逻辑
    private String getBuildString(PathPaneBean pathPaneBean) {
        StringBuffer stringBuffer = FileUtils.read(pathPaneBean.getOldPathNameTextArea().getText(), charsetName);
        stringBuffer = DefaultCommand.foreachCommand(fieldBeanMap, stringBuffer);
        String fileContent = stringBuffer.toString();
        for (Integer k : reNamePaneBeanMap.keySet()) {
            ReNamePaneBean reNamePaneBean = reNamePaneBeanMap.get(k);
            fileContent = fileContent.replace(reNamePaneBean.getOldNametextArea().getText(),
                    reNamePaneBean.getNewNametextArea().getText());

        }
        return fileContent;
    }


    private void loadNorthPanel() {
        northPanel.removeAll();
        northPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;


        javaBeanNameTextArea.setPreferredSize(new Dimension(200, 30));
        javaBeanNameTextArea.setForeground(Color.red);
        northPanel.add(javaBeanNameTextArea);

        s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(javaBeanNameTextArea, s);// 设置组件

        browseButton.setPreferredSize(new Dimension(150, 25));
        s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0.25;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(browseButton, s);// 设置组件
        northPanel.add(browseButton);

        chooseJavaBeanButton.setPreferredSize(new Dimension(150, 25));
        s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0.25;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(chooseJavaBeanButton, s);// 设置组件
        northPanel.add(chooseJavaBeanButton);

        addTplFileButton.setPreferredSize(new Dimension(150, 25));
        s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0.25;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(addTplFileButton, s);// 设置组件
        northPanel.add(addTplFileButton);

        addReNameButton.setPreferredSize(new Dimension(150, 25));
        s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0.25;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(addReNameButton, s);// 设置组件
        northPanel.add(addReNameButton);

        oldFileNameLabel.setPreferredSize(new Dimension(200, 25));
        s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(oldFileNameLabel, s);// 设置组件
        northPanel.add(oldFileNameLabel);

        oldFileNameTextArea.setPreferredSize(new Dimension(150, 25));
        s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0.5;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(oldFileNameTextArea, s);// 设置组件
        northPanel.add(oldFileNameTextArea);
        oldFileNameTextArea.setLineWrap(true);

        newFileNameLabel.setPreferredSize(new Dimension(150, 25));
        s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(newFileNameLabel, s);// 设置组件
        northPanel.add(newFileNameLabel);

        newFileNameTextArea.setPreferredSize(new Dimension(150, 25));
        s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
        s.weightx = 0.5;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
        layout.setConstraints(newFileNameTextArea, s);// 设置组件
        northPanel.add(newFileNameTextArea);
        newFileNameTextArea.setLineWrap(true);

        for (Integer key : reNamePaneBeanMap.keySet()) {
//        	System.out.println(key);
            ReNamePaneBean reNamePaneBean = reNamePaneBeanMap.get(key);

            JLabel oldJLabel = new JLabel("原字段:");
            oldJLabel.setPreferredSize(new Dimension(150, 25));
            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(oldJLabel, s);// 设置组件
            northPanel.add(oldJLabel);

            JTextArea oldNametextArea = reNamePaneBean.getOldNametextArea();
            oldNametextArea.setPreferredSize(new Dimension(150, 25));
            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0.25;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(oldNametextArea, s);// 设置组件
            northPanel.add(oldNametextArea);
            oldNametextArea.setLineWrap(true);

            JLabel newJLabel = new JLabel("该更为:");
            newJLabel.setPreferredSize(new Dimension(150, 25));
            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(newJLabel, s);// 设置组件
            northPanel.add(newJLabel);

            JTextArea newNametextArea = reNamePaneBean.getNewNametextArea();
            newNametextArea.setPreferredSize(new Dimension(150, 25));
            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0.25;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(newNametextArea, s);// 设置组件
            northPanel.add(newNametextArea);
            newNametextArea.setLineWrap(true);

            JButton deleteButton = new JButton("删除");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteReNamePaneBean(key);
                }

            });
            deleteButton.setPreferredSize(new Dimension(150, 25));
            s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(deleteButton, s);// 设置组件
            northPanel.add(deleteButton);
        }
        northPanel.setLayout(layout);
        northPanel.updateUI();
    }

    private void loadCenterPathPanel() {
        centerPathPanel.removeAll();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.HORIZONTAL;
        int ordeyNumber = 0;//第几个
        for (String key : pathPaneBeanMap.keySet()) {

            PathPaneBean pathPaneBean = pathPaneBeanMap.get(key);
            ordeyNumber++;

            JLabel oldPathLabel = new JLabel("第" + ordeyNumber + "个原路径:");
            oldPathLabel.setPreferredSize(new Dimension(150, 20));
            oldPathLabel.setForeground(Color.red);
            centerPathPanel.add(oldPathLabel);

            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(oldPathLabel, s);// 设置组件

            centerPathPanel.add(pathPaneBean.getOldPathNameTextArea());
            pathPaneBean.getOldPathNameTextArea().setLineWrap(true);
            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(pathPaneBean.getOldPathNameTextArea(), s);// 设置组件

            JButton deleteJButton = new JButton("删除");
            deleteJButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deletePathPaneBean(key);
                }
            });
            deleteJButton.setPreferredSize(new Dimension(100, 30));
            centerPathPanel.add(deleteJButton);

            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(deleteJButton, s);// 设置组件

            JButton readJButton = new JButton("查看模板文件");
            readJButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    readFileToTextArea(pathPaneBean.getOldPathNameTextArea().getText());
                }

            });
            readJButton.setPreferredSize(new Dimension(100, 30));
            centerPathPanel.add(readJButton);

            s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(readJButton, s);// 设置组件


            JLabel newPathLabel = new JLabel("生成到:");
            newPathLabel.setPreferredSize(new Dimension(150, 20));
            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(newPathLabel, s);// 设置组件

            centerPathPanel.add(newPathLabel);
            centerPathPanel.add(pathPaneBean.getNewPathNameTextArea());
            pathPaneBean.getNewPathNameTextArea().setLineWrap(true);
            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(pathPaneBean.getNewPathNameTextArea(), s);// 设置组件


            JButton chooseOutPathJButton = new JButton("重选输出位置");
            chooseOutPathJButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chooseOutPath(pathPaneBean);
                }
            });
            chooseOutPathJButton.setPreferredSize(new Dimension(100, 30));
            centerPathPanel.add(chooseOutPathJButton);

            s.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(chooseOutPathJButton, s);// 设置组件


            JButton previewJButton = new JButton("预览输出文件");
            previewJButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    previewOutFileToTextArea(pathPaneBean);
                }
            });
            previewJButton.setPreferredSize(new Dimension(100, 30));
            centerPathPanel.add(previewJButton);

            s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(previewJButton, s);// 设置组件


            //分割线
            JPanel tmpPanel = new JPanel();
            tmpPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
            centerPathPanel.add(tmpPanel);
            s.gridwidth = 0;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
            s.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            s.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
            layout.setConstraints(tmpPanel, s);// 设置组件

        }
        centerPathPanel.setLayout(layout);
//        centerPathPanel.setLayout(new GridLayout(pathPaneBeanMap.size()*2,2));
        centerPathPanel.updateUI();
    }

    //删除替换字段方法
    private void deleteReNamePaneBean(Integer key) {
        reNamePaneBeanMap.remove(key);
        loadNorthPanel();

    }

    //删除替换字段方法
    public void deletePathPaneBean(String key) {
        pathPaneBeanMap.remove(key);
        loadCenterPathPanel();

    }

    public void putPathPaneBean(String oldFilePathName, JTextArea oldPathNameTextArea,
                                JTextArea newPathNameTextArea) {
        pathPaneBeanMap.put(oldFilePathName, new PathPaneBean(oldPathNameTextArea, newPathNameTextArea));
        loadCenterPathPanel();
    }

    // 创建主窗口
    public static void createGUIAndShow() {
        frame = new JFrame("目录结构树");
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 1000;
        int h = 700;
        int x = (ss.width - w) / 2;
        int y = (ss.height - h) / 2 - 40;
        x = x > 0 ? x : 0;
        y = y > 0 ? y : 0;
        frame.setBounds(x, y, w, h);
        frame.setContentPane(new MainJPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void readFileToTextArea(String file) {
        textArea.setText(FileUtils.read(file, charsetName).toString());
    }

    public void previewOutFileToTextArea(PathPaneBean pathPaneBean) {
        String fileContent = getBuildString(pathPaneBean);
        textArea.setText(fileContent);
    }

    public void chooseOutPath(PathPaneBean pathPaneBean) {
        // 选择配置文件
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(MainJPanel.this);

        if (result == JFileChooser.APPROVE_OPTION) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    File dir = chooser.getSelectedFile();
                    JTextArea newPathNameTextArea = pathPaneBean.getNewPathNameTextArea();
                    newPathNameTextArea.setText(dir.getAbsolutePath() + "\\"
                            + FileUtils.getFileNameByPathString(newPathNameTextArea.getText()));
                }
            });
            t.start();
        }
    }
}


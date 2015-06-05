package com.bluejupiter.bookgen.com.bluejupiter.bookgen.ui;

import com.bluejupiter.bookgen.Book;
import com.bluejupiter.bookgen.EpubGen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Will on 15/4/20.
 */
public class MainUI {
    private final String regux;
    private JPanel panel1;
    private JTextField textFieldFileChoose;
    private JButton selectFileButton;
    private JLabel file;
    private JTextField bookNameTextFiled;
    private JButton selectOutputPathButton;
    private JTextField textFieldRegux;
    private JTextField textFieldOutput;
    private JButton buttonPreview;
    private JList listTitle;
    private JButton generateBookButton;
    private JTextField textFieldAuthor;
    private JTextField textEncoding;
    private String fileName;
    private Book book;
    private EpubGen gen;

    public MainUI() {
        book = new Book();

        regux = "^(第(一|二|三|四|五|六|七|八|九|十|百|[0-9])+(章|卷|节|回|补))(.*)";
        textFieldRegux.setText(regux);
        textEncoding.setText("gbk");
//        listTitle.setPreferredSize(new Dimension(200,500));
        listTitle.setFixedCellWidth(200);
        listTitle.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
                    public boolean accept(File file) {
                        if (file.isDirectory())
                            return true;
                        fileName = file.getName();

                        if (fileName.toUpperCase().endsWith("TXT")||fileName.toUpperCase().endsWith("EPUB"))
                            return true;
                        return false;
                    }

                    public String getDescription() {
                        return "读取文本文件";
                    }
                };
                // 建立一个过滤文件类型的过滤器（对话框中正确显示文件），是否启用过滤器决定于下面两句话
                jFileChooser.addChoosableFileFilter(fileFilter);
                jFileChooser.setFileFilter(fileFilter);
                int returnValue = jFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    String fName = file.getAbsolutePath();
                    if (fName != null && fName.length() > 0) {
                        textFieldFileChoose.setText(fName);
                        gen = new EpubGen(fName);
                        book = gen.getBook();

                        bookNameTextFiled.setText(book.getTitle());

                        textFieldOutput.setText(gen.getEpubFile());


                    }
                }
            }
        });
        generateBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gen == null) return;

                if(gen.getTxtFile().toUpperCase().endsWith("EPUB")){
                    try {
                        gen.converToMobi();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    String title = bookNameTextFiled.getText();
                    String output = textFieldOutput.getText();
                    String author = textFieldAuthor.getText();
                    String pattern = textFieldRegux.getText();
                    gen.setPattern(pattern);
                    gen.setEncoding(textEncoding.getText());
                    gen.getBook().setTitle(title);
                    gen.getBook().setAuthor(author);
                    gen.setEpubFile(output);
                    System.out.println(listTitle.getSelectedValuesList());
                    gen.getToc().removeAll(listTitle.getSelectedValuesList());
                    gen.generateEpub(book);
                }
                JOptionPane.showMessageDialog(panel1,"Successfully!");
            }
        });
        buttonPreview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gen == null) return;
                gen.setPattern(textFieldRegux.getText());
                gen.setToc(new Vector());
                gen.setEncoding(textEncoding.getText());
                Vector list = gen.getToc();

                listTitle.setListData(list);
                listTitle.updateUI();
            }
        });
        selectOutputPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gen == null) return;
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnValue = jFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
//                    System.out.println(file);
                    String fName = file.getPath();
//                    System.out.println(fName);
                    if (fName != null && fName.length() > 0) {
                        String bookName = gen.getBook().getTitle();
                        String output = fName +"/" +bookName+ ".epub";
                        textFieldOutput.setText(output);

                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainUI");
        frame.setContentPane(new MainUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

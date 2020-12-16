package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;


public class ServerFrame  extends JFrame implements ActionListener {
    String LOG_PATH = "E:\\CS845-Project\\log\\log.java.txt";

    JTabbedPane tpServer;

//    server board
    JPanel pnlServer, pnlServerInfo;
    JLabel lblNumber, lblServerName, lblIP, lblPort, lblLog;
    JTextField txtNumber, txtServerName, txtIP, txtPort;
    JButton btnStop, btnSaveLog;
    TextArea taLog;

//    user board
    JPanel pnlUser;
    JLabel lblUser;
    JList lstUser;
    JScrollPane spUser;

//    about this project
    JPanel pnlAbout;
    JLabel lblVersionNo, lblSpeak, lblAbout;

    public ServerFrame(){
        super("Chat Server");
        this.initServerWindow();
    }

    public static void main(String args[]){
        new ServerFrame();
    }
/*
*           interface must implement
*           @param evt
*  */
    public void actionPerformed(ActionEvent evt){
    }
    /*
    * init window
    * */

    private void initServerWindow(){
        setSize(550, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();  // Center display
        Dimension fra = this.getSize();

        if(fra.width > scr.width){
            fra.width = scr.width;
        }
        if(fra.height > scr.height){
            fra.height = scr.height;
        }

        this.setLocation((scr.width - fra.width) / 2, (scr.height - fra.height) / 2);

//        server information
        pnlServerInfo = new JPanel(new GridLayout(14, 1));
        pnlServerInfo.setBackground(new Color(52, 130, 203));

        pnlServerInfo.setFont(new Font("Arial", 0, 12));
        pnlServerInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" "),
                BorderFactory.createEmptyBorder(1,1,1,1)));

        lblNumber = new JLabel(" Online People");
        lblNumber.setForeground(Color.YELLOW);
        lblNumber.setFont(new Font("Arial", 0, 12));
        txtNumber = new JTextField("0 People", 12);
        txtNumber.setBackground(Color.decode("#d6f4f2"));
        txtNumber.setFont(new Font("Arial", 0, 12));
        txtNumber.setEditable(false);

        lblServerName = new JLabel("Server Name");
        lblServerName.setForeground(Color.YELLOW);
        lblServerName.setFont(new Font("Arial", 0, 12));
        txtServerName = new JTextField(12);
        txtServerName.setBackground(Color.decode("#d6f4f2"));
        txtServerName.setFont(new Font("Arial", 0, 12));
        txtServerName.setEditable(false);

        lblIP = new JLabel("Server IP");
        lblIP.setForeground(Color.YELLOW);
        lblIP.setFont(new Font("Arial", 0, 12));
        txtIP = new JTextField(12);
        txtIP.setBackground(Color.decode("#d6f4f2"));
        txtIP.setFont(new Font("Arial", 0, 12));
        txtIP.setEditable(false);

        lblPort = new JLabel("Server Port");
        lblPort.setForeground(Color.YELLOW);
        lblPort.setFont(new Font("Arial", 0, 12));
        txtPort = new JTextField("", 12);
        txtPort.setBackground(Color.decode("#d6f4f2"));
        txtPort.setFont(new Font("Arial", 0, 12));
        txtPort.setEditable(false);

        btnStop = new JButton("Close Server(C)");
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeServer();
            }
        });
        btnStop.setBackground(Color.ORANGE);
        btnStop.setFont(new Font("Arial", 0, 12));

        pnlServerInfo.setBounds(5,5,100,400);
        pnlServerInfo.add(lblNumber);
        pnlServerInfo.add(txtNumber);
        pnlServerInfo.add(lblServerName);
        pnlServerInfo.add(txtServerName);
        pnlServerInfo.add(lblIP);
        pnlServerInfo.add(txtIP);
        pnlServerInfo.add(lblPort);
        pnlServerInfo.add(txtPort);

//        client board

        pnlServer = new JPanel();
        pnlServer.setLayout(null);
        pnlServer.setBackground(new Color(52,130,203));

        lblLog = new JLabel("[Server Log]");
        lblLog.setForeground(Color.YELLOW);
        lblLog.setFont(new Font("Arial", 0, 12));
        taLog = new TextArea(20,80);
        taLog.setFont(new Font("Arial", 0, 12));

        btnSaveLog = new JButton("SaveLog(S)");
        btnSaveLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveLog();
            }
        });
        btnSaveLog.setBackground(Color.ORANGE);
        btnSaveLog.setFont(new Font("Arial", 0, 12));

        lblLog.setBounds(110, 5,100,30);
        taLog.setBounds(110,35,300,370);
        btnStop.setBounds(170,410,150,30);
        btnSaveLog.setBounds(320,410,120,30);

//
        pnlServer.add(pnlServerInfo);
        pnlServer.add(lblLog);
        pnlServer.add(taLog);
        pnlServer.add(btnStop);
        pnlServer.add(btnSaveLog);

//        user board
        pnlUser = new JPanel();
        pnlUser.setLayout(null);
        pnlUser.setBackground(new Color(52,130,203));
        pnlUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1,1,1,1)
        ));

        lblUser = new JLabel("[List of online friends]");
        lblUser.setFont(new Font("Arial", 0, 12));
        lblUser.setForeground(Color.YELLOW);

        lstUser = new JList();
        lstUser.setFont(new Font("Arial", 0, 12));
        lstUser.setVisibleRowCount(17);
        lstUser.setFixedCellHeight(18);
        lstUser.setFixedCellWidth(180);

        spUser = new JScrollPane();
        spUser.setBackground(Color.cyan);
        spUser.setFont(new Font("Arial", 0, 12));
        spUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        spUser.getViewport().setView(lstUser);

        pnlUser.setBounds(50,5,300,400);
        lblUser.setBounds(50,10,200,30);
        spUser.setBounds(50,35,200,360);

        pnlUser.add(lblUser);
        pnlUser.add(spUser);

        // chat information
        pnlAbout = new JPanel();
        pnlAbout.setLayout(null);
        pnlAbout.setBackground(new Color(52,130,203));
        pnlAbout.setFont(new Font("Arial", 0, 12));

        lblVersionNo =new JLabel("version 0.1");
        lblVersionNo.setFont(new Font("Arial", 0, 12));
        lblVersionNo.setForeground(Color.YELLOW);

        JTextArea lblSpeak = new JTextArea();
        lblSpeak.setBackground(new Color(52,130,203));
        lblSpeak.setEditable(false);
        lblSpeak.setLineWrap(true);
        lblSpeak.setWrapStyleWord(true);
        lblSpeak.setText("This is the CSC845 project");
        lblSpeak.setForeground(Color.YELLOW);

        lblAbout = new JLabel();
        lblAbout.setFont(new Font("Arial", 0, 12));
        lblAbout.setText("Xinze Zhang");
        lblAbout.setForeground(Color.YELLOW);

        lblVersionNo.setBounds(5,25,100,30);
        lblSpeak.setBounds(5,55,500,80);
        lblAbout.setBounds(5,85,400,120);

        pnlAbout.add(lblVersionNo);
        pnlAbout.add(lblSpeak);
        pnlAbout.add(lblAbout);


//      main board
        tpServer = new JTabbedPane(JTabbedPane.TOP);
        tpServer.setBackground(Color.CYAN);
        tpServer.setFont(new Font("Arial", 0, 12));

        tpServer.add("server management", pnlServer);
        tpServer.add("online user", pnlUser);
        tpServer.add("about this app", pnlAbout);

        this.getContentPane().add(tpServer);
        setVisible(true);
    }
    private  void  closeServer(){
        this.dispose();
        System.exit(0);
    }

    private void saveLog(){
        try{
            FileOutputStream fileOutput = new FileOutputStream(this.LOG_PATH, true);
            String temp = taLog.getText();
            fileOutput.write(temp.getBytes());
            fileOutput.close();
        }catch (Exception e){
            Output.popWindows(e.getMessage(),"save log exception");
        }
            Output.popWindows("Successful Save, file address:" + this.LOG_PATH, "Save Log");
    }
}

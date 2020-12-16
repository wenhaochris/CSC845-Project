package com.company;
import edu.princeton.cs.introcs.Out;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
public class Client extends JFrame implements ActionListener {
    private int PORT = 8000;
    JFrame clientFrame = new JFrame("A Chat project");

    GridBagLayout gl;
    BorderLayout bdl;
    GridBagConstraints gbc;

//    chat front
    JPanel pnlBack, pnlTalk;
    JButton btnTalk;
    JTextArea txtViewTalk;
    JLabel lblTalk, lblTo;
    JComboBox listOnline;

//    login front
    JPanel pnlLogin;
    JLabel lblServerIP, lblName, lblPassword;
    JTextField txtTalk, txtServerIP, txtName;
    JPasswordField txtPassword;
    JButton btnLogin, btnReg, btnExit;

    JDialog dialogLogin = new JDialog(this, "login", true);

    Socket socket = null;
    BufferedReader in = null;
    PrintWriter outStream = null;

    String strSend, strReceive, strKey, strStatus;
    private  StringTokenizer st;

    public Client(){
        this.initWindow();
    }

/*
* event listen
* @param evt
* */



    @Override
    public void actionPerformed(ActionEvent evt) {
        Object evtObj = evt.getSource();
        try{
           if(evtObj.equals(btnExit)){
//               exit
               System.exit(0);
           }else if(evtObj.equals(btnLogin)){
//               login in
               char[] pass = txtPassword.getPassword();
               String password = new String(pass);
               if((txtServerIP.getText().length() > 0 ) && (txtName.getText().length() >0) && (password.length() >0)){
                   this.connect();
                   strSend = "login|" + txtName.getText() + "|"
                           + String.valueOf(txtPassword.getPassword());
                   outStream.println(strSend);
                   this.initLogin();
               }else{
                   Output.popWindows("Please input valid information", "ERROR");
               }
           }else if(evtObj.equals(btnReg)) {
//               register
               char[] pass = txtPassword.getPassword();
               String password = new String(pass);
               if(txtName.getText().length() > 0 && password.length() > 0){
                   this.connect();
                   strSend = "reg|" + txtName.getText() + "|"
                           + String.valueOf(password);
                   outStream.println(strSend);
                   this.initLogin();
               }else{
                   Output.popWindows("Please input valid information", "ERROR");
               }
           }else if(evtObj.equals(btnTalk)){
//               send message
               if(txtTalk.getText().length() > 0){
                   outStream.println("talk|" + txtTalk.getText() + "|" + txtName.getText() +"|" + listOnline.getSelectedItem().toString());
                   txtTalk.setText("");
               }
           }else{
               Output.popWindows("illegal operation ", " EXCEPTIiON");
           }
        }catch (Exception e){
            Output.popWindows(e.getMessage(), " EXCEPTION");
        }
    }

    public  static void main(String args[]){
        new Client();
    }

    /*
    * multiple thread
    * */
    class ClientThread implements Runnable{
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String strReceive, strKey;
        private Thread threadTalk;
        private StringTokenizer st;

        public ClientThread(Socket s) throws IOException{
            this.socket = s;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            threadTalk = new Thread(this);
            threadTalk.start();
        }

        public void run(){
//            synchronized  block the thread,  every time only one thread can run
//            the other thread must wait the last thread terminate then turn to work
            while (true) synchronized (this){
                try{
                    strReceive = in.readLine();
                    st = new StringTokenizer(strReceive, "|");
                    strKey = st.nextToken();
                    if(strKey.equals("talk")){
                        String strTalk = st.nextToken();
                        strTalk = txtViewTalk.getText() + "\r\n     " + strTalk;
                        txtViewTalk.setText(strTalk);
                    }else if(strKey.equals("online")){
                        String strOnline;
                        while (st.hasMoreTokens()){
                            strOnline = st.nextToken();
//                            check
                            boolean exists = false;
                            for(int index = 0; index < listOnline.getItemCount() && !exists; index++){
                                if(strOnline.equals(listOnline.getItemAt(index))){
                                    exists = true;
                                }
                            }
                            if(!exists){
                                listOnline.addItem(strOnline);
                            }
                        }
                    }else if(strKey.equals("remove")){
                        String strRemove;
                        while (st.hasMoreTokens()){
                            strRemove = st.nextToken();
                            listOnline.removeItem(strRemove);
                        }
                    }else if(strKey.equals("warning")){
                        String strWarning = st.nextToken();
                        Output.popWindows(strWarning, " warning");
                    }
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    Output.popWindows(e.getMessage(), " Thread Exception");
                }catch (IOException e){
                    Output.popWindows(e.getMessage(), " Thread IO Exception");
                }
            }
        }
    }
    /*
    * init Login
    * */

    private void initLogin() throws IOException{
        strReceive = in.readLine();
        st = new StringTokenizer(strReceive, "|");
        strKey = st.nextToken();
        if(strKey.equals("login")){
            strStatus = st.nextToken();
            if(strStatus.equals("succeed")){
                btnLogin.setEnabled(false);
                btnTalk.setEnabled(true);
                pnlLogin.setVisible(false);
//                Component monitor resources. release the memory to operation system
                dialogLogin.dispose();
                new ClientThread(socket);
                outStream.println("init|online");
            }
            Output.popWindows(strKey + " " + strStatus + "!", "Login");
        }
        if(strKey.equals("warning")){
            strStatus = st.nextToken();
            Output.popWindows(strStatus, "Register");
        }
    }
    private void connect(){
        try{
            socket = new Socket(txtServerIP.getText(), PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        }catch (ConnectException e){
            Output.popWindows("connect to server fail", " Connection Error");
            txtServerIP.setText("");
            Output.popWindows(e.getMessage(), "Connection Error");
        }catch (Exception e){
            Output.popWindows(e.getMessage(),"ERROR");
        }
    }

    private void initWindow(){
        gl = new GridBagLayout();
        bdl = new BorderLayout();
        gbc = new GridBagConstraints();
        pnlBack = (JPanel) getContentPane();
        pnlBack.setLayout(bdl);


        pnlLogin = new JPanel();
        pnlLogin.setLayout(gl);


        lblServerIP = new JLabel("SERVER IP:");
        lblName = new JLabel("  USER NAME:");
        lblPassword = new JLabel("   PASSWORD:");
        txtServerIP = new JTextField(12);
        txtName = new JTextField(12);
        txtPassword = new JPasswordField(12);

        txtServerIP.setText("10.0.0.34");

        btnLogin = new JButton("Login in");
        btnReg = new JButton("Register");
        btnExit = new JButton(" Close");

        btnTalk = new JButton("Send");
        lblTalk = new JLabel("Message : ");

        lblTo = new JLabel(" To :");
        txtTalk = new JTextField(30);
        pnlTalk = new JPanel();
        txtViewTalk = new JTextArea(18, 40);
        listOnline = new JComboBox();
        txtViewTalk.setForeground(Color.blue);
        btnTalk.addActionListener(this);

        btnLogin.addActionListener(this);
        btnReg.addActionListener(this);
        btnExit.addActionListener(this);

        listOnline.addItem("ALL");

        pnlTalk.add(lblName);
        pnlTalk.add(txtTalk);
        pnlTalk.add(lblTo);
        pnlTalk.add(listOnline);
        pnlTalk.add(btnTalk);
        pnlBack.add("Center", txtViewTalk);
        pnlBack.add("South", pnlTalk);
        pnlTalk.setBackground(Color.cyan);
        btnTalk.setEnabled(false);

        clientFrame.getContentPane().add(pnlBack);
        clientFrame.setSize(800,450);
        clientFrame.setVisible(true);
        clientFrame.setResizable(false);
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dialogLogin.getContentPane().setLayout(new FlowLayout());
        dialogLogin.getContentPane().add(lblServerIP);
        dialogLogin.getContentPane().add(txtServerIP);
        dialogLogin.getContentPane().add(lblName);
        dialogLogin.getContentPane().add(txtName);
        dialogLogin.getContentPane().add(lblPassword);
        dialogLogin.getContentPane().add(txtPassword);
        dialogLogin.getContentPane().add(btnLogin);
        dialogLogin.getContentPane().add(btnReg);
        dialogLogin.getContentPane().add(btnExit);

        dialogLogin.setBounds(300,300,250,200);
        dialogLogin.getContentPane().setBackground(Color.gray);
        dialogLogin.setVisible(true);

    }

}

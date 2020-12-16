package com.company;
import edu.princeton.cs.algs4.Out;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.text.SimpleDateFormat;

public class ServerProcess extends Thread {
    /*
    *       store the password and files, insert db, encode password
    * */
    private final String USER_LIST_FILE = "E:\\CS845-Project\\java_chat_user.md";
//    client sockets
    private Socket socket = null;
    ServerFrame sFrame;

    private BufferedReader in;  // define inputStream
    private PrintWriter outStream;  // define outputStream


//    store online users to memory
    private static Vector onlineUser = new Vector(10, 5);
    private static Vector socketUser = new Vector(10, 5);

    private String strReceive, strKey;
    private StringTokenizer st;

    public ServerProcess(Socket client, ServerFrame frame) throws IOException{
        socket = client;
        sFrame = frame;

//        client receive
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        client output
        outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.start();
    }


//    release thread run function
    public void run(){
        try {
            while (true){
                strReceive = in.readLine();
                st = new StringTokenizer(strReceive, "|");
                strKey = st.nextToken();
                if(strKey.equals("login")){
                    login();
                }else if(strKey.equals("talk")) {
                    talk();
                }else if(strKey.equals("init")){
                    freshClientsOnline();
                }else if(strKey.equals("reg")){
                    register();
                }
            }
        }catch (IOException e){
            String leaveUser = closeSocket();
            String dateTime = this.getCurrentTime();
            log("User" + leaveUser + "Leaved" + " Leaving Time" + dateTime);

            try{
                freshClientsOnline();
            }catch (IOException e1){
                e1.printStackTrace();
            }
            System.out.println("[System]" + leaveUser + " leave chat room!");
            sendAll("talk|>>>" + leaveUser + "Leaving the chat room reluctantly");
        }
    }

    /*
    * login in
    * */
    private void login() throws  IOException{
        String name = st.nextToken();
        String password = st.nextToken().trim();

        boolean succeed = false;
        String dateTime;
        dateTime = this.getCurrentTime();
        log("User" + name + "login..." + "\n" + "Password :" + "\n" + "Port" + socket + dateTime);
        Output.println("[USER LOGIN]" + name + ":" + password + ":" + socket);

        for(int i = 0; i < onlineUser.size(); i++){
            if(onlineUser.elementAt(i).equals(name)){
                Output.println("[ERROR]" + name + " has login!");
                outStream.println("warning|" + name + " has login in chat room" );
            }
        }
//        check username and password
        if(this.checkUserNamePass(name, password)){
            this.userLoginSuccess(name);
            succeed = true;
        }

        if(!succeed){
            outStream.println("warning|" + name + " login fail , please check the input");
            log("User" + name + " login fail " + dateTime);
            Output.println("[System]" + name + "login fail !");
        }
    }

    /*
    * user register
    * */

    private void register() throws IOException{
        String name = st.nextToken();
        String password = st.nextToken().trim();
        if(isExistUser(name)){
            Output.println("[ERROR]" + name + " Register fail!");
            outStream.println("warning|this user has registered, please change name ");
        }else{
            RandomAccessFile userFile = new RandomAccessFile(USER_LIST_FILE, "rw");
            userFile.seek(userFile.length());
//            add new user information
            userFile.writeBytes(name + "|" + password + "\r\n");
            log("User" + name + " register successful" + "register time" + this.getCurrentTime());
            userLoginSuccess(name); // come into chat room
        }
    }
//    talk
    private void talk() throws IOException{
        String strTalkInfo = st.nextToken(); // get talk message
        String strSender = st.nextToken(); // get sender
        String strReceiver = st.nextToken(); //get receiver

        Output.println("[TALK_" + strReceiver + "]" + strTalkInfo);
        Socket socketSend;
        PrintWriter outSend;

//        get current time
        strTalkInfo += "(" + this.getCurrentTime() + ")";

        log("user" + strSender + " to " + strReceiver + "say :" + strTalkInfo + this.getCurrentTime());

        if(strReceiver.equals("ALL")){
            sendAll("talk|" + strSender + " to all people : " + strTalkInfo);
        }else{
            if(strSender.equals(strReceiver)){
                outStream.println("tall|>>>Should not speak to own self !!!");
            }else{
                for(int i = 0; i < onlineUser.size(); i++){
                    if(strReceiver.equals(onlineUser.elementAt(i))){
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println("talk|" + strSender + " : " + strTalkInfo);
                    }else if(strSender.equals(onlineUser.elementAt(i))){
                        socketSend = (Socket)socketUser.elementAt(i);
                        outSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println("talk| to " + strReceiver + " : " + strTalkInfo);
                    }
                }
            }
        }
    }



    /*
    * broadcast message
    * @param strSend
    * */
    private void sendAll(String strSend) {
        Socket socketSend;
        PrintWriter outSend;
        try {
            for(int i = 0; i < socketUser.size(); i++){
                socketSend = (Socket) socketUser.elementAt(i);
                outSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                outSend.println(strSend);
            }
        }catch (IOException e){
            Output.println("[ERROR] send all fail");
        }
    }
    /*
    * check user exist
    * @return boolean
    * */
    private boolean isExistUser(String name){
        String strRead;
        try{
            FileInputStream inputFile = new FileInputStream(USER_LIST_FILE);
            BufferedReader inputData = new BufferedReader(new InputStreamReader(inputFile));
            while((strRead = inputData.readLine())!= null){
                StringTokenizer stUser = new StringTokenizer(strRead, "|");
                if(stUser.nextToken().equals(name)){
                    return true;
                }
            }
        }catch (FileNotFoundException fn){
            Output.println("[ERROR] User File has not exist !" + fn);
            outStream.println("warning|fn check exist read file error");
        }catch (IOException ie){
            Output.println("[ERROR] " + ie.getMessage());
            outStream.println("warning|ie check exist read file error!");
        }
        return false;
    }


    /*
    *check user name and password
    * */

    private boolean checkUserNamePass(String name, String password){
        String strRead;
        try {
            FileInputStream inputFile = new FileInputStream(USER_LIST_FILE);
            BufferedReader inputData = new BufferedReader(new InputStreamReader(inputFile));
            while ((strRead = inputData.readLine()) != null){
                if(strRead.equals(name + "|" + password)){
                    return true;
                }
            }
        }catch (FileNotFoundException fn){
            Output.println("[ERROR] User File has not exist !" + fn);
            outStream.println("warning|fn check exist read file error");
        }catch (IOException ie){
            Output.println("[ERROR] " + ie);
            outStream.println("warning|ie check exist read file error!");
        }
        return false;
    }

    /*
    * set login success
    * add name and sockets to onlineUser, socketUser memory
    * @param name
    * */

    private void userLoginSuccess(String name) throws IOException{
        String dataTime = this.getCurrentTime();
        outStream.println("login|succeed");
        sendAll("online|"+ name);

        onlineUser.addElement(name);
        socketUser.addElement(socket);

        log("user" + name + " login succeed, " + "login time :" + dataTime);

        this.freshClientsOnline();

        sendAll("task|>>>welcome " + name + " to chat room");
        Output.println("[System]" + name + "login succeed !");
    }
    /*
    * close socket
    * @return strUser
    * */

    private String closeSocket(){
        String strUser  = "";
        for (int i = 0; i < socketUser.size(); i++){
            if(socket.equals((Socket)socketUser.elementAt(i))) {
                strUser = onlineUser.elementAt(i).toString();
                socketUser.removeElementAt(i);
                onlineUser.removeElementAt(i);
                try {
                    freshClientsOnline();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendAll("remove|" + strUser);
            }
        }
        try {
            in.close();
            outStream.close();
            socket.close();
        }catch (IOException e){
            Output.println("[ERROR]" + e.getMessage());
        }
        return  strUser;
    }

    /*
    * fresh onlineUser list, travel in memory
    * */
    private void freshClientsOnline() throws IOException{
        String strOnline = "online";
        String[] userList = new String[20];
        String userName = null;

        for(int i = 0; i < onlineUser.size(); i++){
            strOnline += "|" + onlineUser.elementAt(i);
            userName =" " + onlineUser.elementAt(i);
            userList[i] = userName;
        }
//        set Frame
        sFrame.txtNumber.setText("" + onlineUser.size());
        sFrame.lstUser.setListData(userList);
        Output.println(strOnline);
//        send to socks outStream stream
        outStream.println(strOnline);

    }

    /*
    * record log information
    * */

    private void log(String log){
        String newLog = sFrame.taLog.getText() + "\n" + log;
        sFrame.taLog.setText(newLog);
    }

    private String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }




}

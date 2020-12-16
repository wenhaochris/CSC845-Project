package com.company;
import edu.princeton.cs.introcs.Out;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;

public class Server {

    // define listen port
    private final int SERVER_PORT = 8000;
    private final String USER_LIST_FILE = "E:\\CS845-Project\\java_chat_user.md";

//    frame
    ServerFrame serverFrame = null;
//    create server sockets
    ServerSocket serverSocket = null;

    public Server(){
        try {
//        start server
            serverSocket = new ServerSocket(SERVER_PORT);
            serverFrame = new ServerFrame();
            this.setServerIP();
            Output.println("[start] server is opening");
            serverFrame.taLog.setText("[start] server is working");

//            create void file
            RandomAccessFile userFile = new RandomAccessFile(USER_LIST_FILE, "rw");
            userFile.writeBytes("");
            while (true){

                Socket socket = serverSocket.accept();
                // create thread to connect client
               new ServerProcess(socket, serverFrame);
            }

        }catch (BindException e){
            Output.popWindows(e.getMessage(), "Port has been used");
            System.exit(0);

        } catch (IOException e) {
            Output.popWindows(e.getMessage(), " ERROR");
            System.exit(0);
        }
        Output.println("start thread...");

    }
    private void setServerIP(){
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            byte[] ipAddress = serverAddress.getAddress();
            serverFrame.txtServerName.setText(serverAddress.getHostName());
            serverFrame.txtIP.setText(serverAddress.getHostAddress());
            serverFrame.txtPort.setText(SERVER_PORT+"");
            Output.println("Server IP is:" + (ipAddress[0] & 0xff) + "."
                    + (ipAddress[1] & 0xff) + "." + (ipAddress[2] & 0xff) + "."
                    + (ipAddress[3] & 0xff) );
        } catch (Exception e) {
            Output.popWindows(e.getMessage(), "fail to get IP address ");
        }
    }
    public static void main(String args[]){
        new Server();
    }
}

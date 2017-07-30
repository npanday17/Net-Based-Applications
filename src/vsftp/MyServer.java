/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsftp;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author npanday
 */
public class MyServer extends Thread {

    private DatagramSocket serverSocket;
    private Socket s;
    private String fileName;
    private String host;
    private int tcpPort;

    public MyServer(int port) {
        try {
            serverSocket = new DatagramSocket(port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] receiveData = new byte[1024];
                byte[] sendData;
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String rcvdString = new String(receivePacket.getData());
                System.out.println("MESSAGE FROM CLIENT: " + rcvdString);
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String[] rcvdMsg = rcvdString.split("&");
                fileName = rcvdMsg[0];
                host = rcvdMsg[1];
                //tcpPort= Integer.parseInt(rcvdMsg[2]);
                tcpPort=2000;
                String sendAck = "Sending the file: " + fileName + " to client: " + host + " and port: " + tcpPort;
                sendData = sendAck.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);

                s = new Socket(host, tcpPort);
                sendFile(fileName);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void sendFile(String file) {
        try (DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];

            while (fis.read(buffer) > 0) {
                dos.write(buffer);
            }
            System.out.println("File sent");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String args[]) {
        MyServer ms = new MyServer(3000);
        ms.start();
    }
}

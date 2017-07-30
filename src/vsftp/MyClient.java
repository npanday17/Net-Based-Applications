/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsftp;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author npanday
 */
public class MyClient {

    private String file = "group.txt";
    private String sHostName = "localhost";
    private String cHostName = "localhost";
    private String sDir = "C:\\newfolder\\";
    private String cDir = "C:\\newfolder\\recvd\\";
    private int tcpPort = 2000;
    private ServerSocket s;
    private DatagramSocket clientSocket;

    public MyClient() {
        try {
            s = new ServerSocket(tcpPort);
            clientSocket = new DatagramSocket();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void start() {
        try {
            InetAddress IPAddress = InetAddress.getByName(sHostName);//server adress
            String data = sDir + file + "&" + cHostName + "&" + tcpPort;//data to be sent to server via UDP
            byte[] sendData = data.getBytes();
            byte[] receiveData = new byte[1024];

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 3000);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String serverAck = new String(receivePacket.getData());
            System.out.println("MESSAGE FROM SERVER: " + serverAck);
            Socket c = s.accept();
            saveFile(c);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveFile(Socket c) {
        try (DataInputStream dis = new DataInputStream(c.getInputStream());
                FileOutputStream fos = new FileOutputStream(cDir + file)) {

            byte[] buffer = new byte[4096];
            /* File size can be sent in a seperate Packet
            Here 1MB max size is specified */
            int filesize = 1048576;
            int read;
            int totalRead = 0;
            int remaining = filesize;
            while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                System.out.println("Recieved " + totalRead + " bytes.");
                fos.write(buffer, 0, read);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        MyClient client = new MyClient();
        client.start();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webclientserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author npanday
 */
public class WebServer extends Thread {

    private ServerSocket serverSocket;
    private Socket client;
    private String root = "C:\\newfolder\\";
    private String fileName;//"Sample.html";//"IMG_20161030_162157113_HDR.jpg";

    public WebServer(int port) {
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                client = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String input;
                while ((input = in.readLine()) != null) {
                    System.out.println(input);
                    if (input.startsWith("GET")) {

                        String[] parts = input.split("\\s+");
                        fileName = parts[1];

                        if (fileName.matches("http://[a-z]+:[0-9]+/[a-zA-Z0-9]+.?[a-zA-Z]+")) {
                            fileName = fileName.substring(fileName.indexOf("/", 7));
                        }

                        System.out.println(fileName);

                        if (fileName.endsWith(".html")) {
                            System.out.println("html");
                            sendHtml(fileName);
                        } else if (fileName.endsWith(".jpg")) {
                            System.out.println("img");
                            sendFile(fileName);
                        } else {
                            System.out.println("not supported");
                            sendNotSupported();
                        }
                    }
                }

                in.close();
                client.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void sendHtml(String file) {
        try {
            try (PrintWriter out = new PrintWriter(client.getOutputStream());
                    FileInputStream fis = new FileInputStream(root + file);) {
                out.print("HTTP/1.1 200 OK\r\n"); // Version & status code
                out.print("Content-Type: text/html\r\n"); // The type of data
                out.print("\r\n"); // End of headers

                Scanner fileReader = new Scanner(fis);
                while (fileReader.hasNext()) {
                    String line = fileReader.nextLine();
                    out.println(line);
                }
            }
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
            sendError();
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }

    }

    private void sendNotSupported() {
        try {
            try (PrintWriter out = new PrintWriter(client.getOutputStream())) {
                out.print("HTTP/1.1 400 \r\n"); // Version & status code
                out.print("Content-Type: plain\text\r\n"); // The type of data
                out.print("\r\n"); // End of headers
                out.print("functionality not Implemented \r\n");
            }
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }

    }

    private void sendError() {
        try {
            client = serverSocket.accept();
            try (PrintWriter out = new PrintWriter(client.getOutputStream())) {
                out.print("HTTP/1.1 400 \r\n"); // Version & status code
                out.print("Content-Type: plain\text\r\n"); // The type of data
                out.print("\r\n"); // End of headers
                out.print("File Not Found \r\n");
            }
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }

    }

    private void sendFile(String file) {
        try (DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                FileInputStream fis = new FileInputStream(root + file)) {

            dos.writeBytes("HTTP/1.1 200 OK\r\n"); // Version & status code
            dos.writeBytes("Content-Type: image/jpeg\r\n"); // The type of data
            dos.writeBytes("\r\n");// End of headers
            byte[] buffer = new byte[4096];

            while (fis.read(buffer) > 0) {
                dos.write(buffer);
            }

        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
            sendError();
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }
    }

    public static void main(String args[]) {
        WebServer ms = new WebServer(2000);
        ms.start();
    }
}

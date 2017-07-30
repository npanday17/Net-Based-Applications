/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webclientserver;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author npand
 */
public class WebClient {

    //static HashMap<String, String> responseHeaders;
    static String cDir = "C:\\newfolder\\";
    static String file = "saved_page.html";

    public static void main(String[] args) throws URISyntaxException, Exception {

        try {
            System.out.println("Enter a website:");
            Scanner sc = new Scanner(System.in);
            String website = sc.next();
            URL myUrl = new URL(website);
            URI myUri = myUrl.toURI();
            Socket s = new Socket(myUrl.getHost(), 80);

            PrintWriter out = new PrintWriter(s.getOutputStream());

            out.print("GET " + myUri + " HTTP/1.1\r\n");
            out.print("Host: " + myUrl.getHost() + "\r\n\r\n");
            out.flush();

            saveFile(s);

            s.close();
            out.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void saveFile(Socket c) {
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
                //System.out.println("Recieved " + totalRead + " bytes.");
                fos.write(buffer, 0, read);
            }
            c.close();
            //System.out.println("here");
            FileInputStream fis = new FileInputStream(cDir + file);
            Scanner fileReader = new Scanner(fis);
            String line = fileReader.nextLine();
            System.out.println("HTTP Response from Server: " + line);
            System.out.println("Check the following file for more details " + cDir + file);
            fis.close();
            //c.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

import java.net.ServerSocket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
    static final int PORT = 9999;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciat localhost:" + PORT);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            ObjectOutputStream sortida = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(clientSocket.getInputStream());

            sortida.writeObject("Escriu el teu nom:");
            sortida.flush();
            String nom = (String) entrada.readObject();
            System.out.println("Nom rebut: " + nom);

            System.out.println("Fil de xat creat.");
            FilServidorXat fil = new FilServidorXat(entrada, sortida, nom);
            fil.start();
            fil.join();

            clientSocket.close();
            System.out.println("Servidor aturat");

        } catch (Exception e) {
            System.out.println("Error al servidor: " + e.getMessage());
        }
    }
}

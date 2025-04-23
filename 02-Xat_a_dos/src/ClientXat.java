import java.net.Socket;

import java.util.Scanner;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;


public class ClientXat {
    static final int PORT = 9999;
    private static Socket socket;
    private static ObjectOutputStream sortida;
    private static ObjectInputStream entrada;

    public static void connecta() {
        try {
            socket = new Socket("localhost", PORT);
            sortida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client connectat a localhost:" + PORT);
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.out.println("Error en connectar al servidor: " + e.getMessage());
        }
    }

    public static void enviarMissatge(String missatge) {
        try {
            System.out.println("Enviant missatge: " + missatge);
            sortida.writeObject(missatge);
            sortida.flush();
        } catch (IOException e) {
            System.out.println("Error en enviar el missatge.");
        }
    }

    public static void tancarClient() {
        System.out.println("Tancant client...");
        try {
            if (entrada != null) entrada.close();
            if (sortida != null) sortida.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat.");
        } catch (IOException e) {
            System.out.println("Error en tancar el client.");
        }
    }

    public static void main(String[] args) {
        connecta();

        FilLectorCX fil = new FilLectorCX(entrada);
        fil.start();

        Scanner scanner = new Scanner(System.in);
        String missatge;
        
        do {
            missatge = scanner.nextLine();
            enviarMissatge(missatge);
        } while (!missatge.equalsIgnoreCase("sortir"));

        scanner.close();
        tancarClient();
    }
}



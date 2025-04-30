import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;

public class Servidor {
    private final int PORT = 9999;
    private final String HOST = "localhost";

    public Socket connectar(ServerSocket serverSocket) {
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        try {
            System.out.println("Esperant connexio...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connexio acceptada: " + clientSocket.getInetAddress());
            return clientSocket;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void tancarConnexio(Socket socket) {
        try {
            System.out.println("Tancant la connexió amb el client : " + socket.getInetAddress());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarFitxers(Socket socket) {
        try (
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) entrada.readObject();

            if (nomFitxer == null || nomFitxer.trim().isEmpty()) {
                System.out.println("Nom del fitxer buit o null. Sortint...");
                return;
            }

            System.out.println("Nomfitxer rebut: " + nomFitxer);

            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();
            if (contingut != null) {
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                sortida.writeObject(contingut);
                sortida.flush();
                System.out.println("Fitxer enviat al client : " + nomFitxer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try (ServerSocket serverSocket = new ServerSocket(servidor.PORT)) {
            Socket socket = servidor.connectar(serverSocket);
            if (socket != null) {
                servidor.enviarFitxers(socket);
                servidor.tancarConnexio(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

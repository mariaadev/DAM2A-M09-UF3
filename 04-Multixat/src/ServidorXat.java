import java.util.Hashtable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
    private final int PORT = 9999;
    private ServerSocket serverSocket;
    private final Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;
    private final String MSG_SORTIR = "sortir";

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a localhost:" + PORT);

            while (!sortir) {
                Socket socketClient = serverSocket.accept();
                System.out.println("Client connectat: " + socketClient.getInetAddress());

                GestorClients gestor = new GestorClients(socketClient, this);
                gestor.start();  // Iniciem el fil per gestionar el client
            }
        } catch (IOException e) {
            System.out.println("Error al servidor: " + e.getMessage());
        } finally {
            pararServidor();
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor aturat.");
            }
        } catch (IOException e) {
            System.out.println("Error tancant servidor: " + e.getMessage());
        }
    }

    public void finalitzarXat() {
        System.out.println("Tancant tots els clients.");
        enviarMissatgeGrup("DEBUG: multicast sortir");

        for (GestorClients client : clients.values()) {
            try {
                client.enviarMissatge("Servidor", MSG_SORTIR);
            } catch (Exception ignored) {}
        }

        clients.clear();
        sortir = true;

        try {
            pararServidor();
        } catch (Exception e) {
            System.out.println("Error finalitzant el servidor.");
        }

        System.exit(0); // Finalitzem l'aplicació
    }

    public void afegirClient(GestorClients gestorClient) {
        String nom = gestorClient.getNom();
        if (!clients.containsKey(nom)) {
            clients.put(nom, gestorClient);
            enviarMissatgeGrup("DEBUG: multicast Entra: " + nom);
        }
    }

    public void eliminarClient(String nomClient) {
        if (clients.containsKey(nomClient)) {
            clients.remove(nomClient);
            System.out.println(nomClient + " ha sortit.");
        }
    }

    public void enviarMissatgeGrup(String msg) {
        for (GestorClients client : clients.values()) {
            client.enviarMissatge("Servidor", msg);
        }
    }

    public void enviarMissatgePersonal(String nomDestinatari, String remitent, String missatge) {
        GestorClients receptor = clients.get(nomDestinatari);
        if (receptor != null) {
            System.out.println("Missatge personal per (" + nomDestinatari + ") de (" + remitent + "): " + missatge);
            receptor.enviarMissatge(remitent, missatge);
        } else {
            System.out.println("Destinatari no trobat: " + nomDestinatari);
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }
}

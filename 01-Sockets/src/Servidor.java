import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.io.*;
import java.util.logging.Level;

public class Servidor {
    static final int PORT = 7777;
    private static ServerSocket srvSocket;
    private static Socket clientSocket;
    private static boolean end = false;

    public static void connecta() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a localhost: " + PORT);
            System.out.println("Esperant connexions a localhost: " + PORT);

              while (!end) {
                try {
                    clientSocket = srvSocket.accept(); 
                    System.out.println("Client connectat: " + clientSocket.getInetAddress());
                    repDades();  
                } catch (IOException e) {
                    e.printStackTrace();
                    break; 
                }
            }

        } catch (IOException e) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            tanca();  
        }
    }

    public static void repDades() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String linea;
            while ((linea = in.readLine()) != null) {
                System.out.println("Rebut: " + linea); 
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            end = true;
        }
    }

    public static void tanca() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (srvSocket != null && !srvSocket.isClosed()) {
                srvSocket.close();
            }
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connecta();
    }
}

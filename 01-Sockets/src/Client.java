
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    static final int PORT = 7777;
    static final String HOST = "localhost";
    private static Socket socket;
    private static PrintWriter out;

    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connectat a servidor en localhost: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tanca() {
        if (out != null) {
            out.close();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Client tancat.");
        }
    }

    public void envia() {
        connecta();

        String[] messages = {
            "Prova d'enviament 1",
            "Prova d'enviament 2",
            "Adeu"
        };

        for (String msg : messages) {
            System.out.println("Enviat al servidor: " + msg);
            out.println(msg); 
        }

        System.out.println("Prem enter per tancar el client...");
        try {
            System.in.read(); 
        } catch (IOException e) {
            e.printStackTrace();
        }

        tanca();  
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.envia();
    }
}

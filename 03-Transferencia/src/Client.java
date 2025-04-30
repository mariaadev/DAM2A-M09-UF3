import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private final String DIR_ARRIBADA = "/tmp";
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private Socket socket;

    public void connectar() {
        try {
            socket = new Socket("localhost", 9999);
            System.out.println("Connectat a -> localhost:9999");
            sortida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connexió acceptada: " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rebreFitxers() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine();

            if ("sortir".equalsIgnoreCase(nomFitxer)) {
                System.out.println("Sortint...");
                return;
            }

            try {
                sortida.writeObject(nomFitxer);

                String nomFitxerBase = new File(nomFitxer).getName(); 
                String desti = DIR_ARRIBADA + "/" + nomFitxerBase;

                byte[] contingut = (byte[]) entrada.readObject();
                if (contingut != null) {
                    File file = new File(desti);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(contingut);
                        System.out.println("Fitxer rebut i guardat com: " + desti);
                    }
                } else {
                    System.out.println("No s'ha rebut contingut del fitxer.");
                }


                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }

    public void tancarConnexio() {
        try {
            entrada.close();
            sortida.close();
            socket.close();
            System.out.println("Connexió tancada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connectar();
        client.rebreFitxers();
        client.tancarConnexio();
    }
}

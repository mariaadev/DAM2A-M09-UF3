import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            sortida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client connectat a localhost:9999");
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.out.println("Error connectant: " + e.getMessage());
        }
    }

    public String enviarMissatge(String msg) {
        try {
            if (sortida != null) {
                sortida.writeObject(msg);
                System.out.println("Enviant missatge: " + msg);
                return "OK";
            } else {
                System.out.println("oos null. Sortint...");
                return "ERROR";
            }
        } catch (IOException e) {
            System.out.println("Error enviant missatge: " + e.getMessage());
            return "ERROR";
        }
    }

    public void tancarClient() {
        try {
            if (entrada != null) {
                entrada.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (sortida != null) {
                sortida.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Tancant client...");
            }
        } catch (IOException e) {
            System.out.println("Error tancant el client: " + e.getMessage());
        }
    }

    public void iniciarRecepcio() {
        new Thread(() -> {
            try {
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                while (!sortir) {
                    String missatge = (String) entrada.readObject();
                    String codi = Missatge.getCodiMissatge(missatge);
                    String[] parts = Missatge.getPartsMissatge(missatge);
                    if (codi == null || parts == null) continue;

                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                      case Missatge.CODI_MSG_PERSONAL:
                        if ("sortir".equals(parts[2])) {
                            sortir = true;
                            break;
                        }
                        System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                        break;
                        case Missatge.CODI_MSG_GRUP:
                            System.out.println(parts[1]);
                            break;
                        default:
                            System.out.println("Error: codi no reconegut");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error rebent missatge. Sortint...");
                sortir = true;
            }
        }).start();
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public void getLinea(Scanner scanner, String msg, boolean esObligatori) {
        String linia;
        do {
            System.out.print(msg);
            linia = scanner.nextLine();
        } while (esObligatori && linia.trim().isEmpty());
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner scanner = new Scanner(System.in);
        client.connecta();
        client.iniciarRecepcio();
        client.ajuda();

        boolean sortir = false;
        while (!sortir) {
            String opcio = scanner.nextLine().trim();
            switch (opcio) {
                case "":
                case "4":
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    sortir = true;
                    break;
                case "1":
                    System.out.print("Introdueix el nom: ");
                    String nom = scanner.nextLine().trim();
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;
                case "2":
                    System.out.print("Destinatari:: ");
                    String desti = scanner.nextLine();
                    System.out.print("Missatge a enviar: ");
                    String msg = scanner.nextLine();
                    client.enviarMissatge(Missatge.getMissatgePersonal(desti, msg));
                    break;
                case "3":
                    System.out.print("Missatge a enviar al grup: ");
                    String msgGrup = scanner.nextLine();
                    client.enviarMissatge(Missatge.getMissatgeGrup(msgGrup));
                    break;
                case "5":
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    sortir = true;
                    break;
                default:
                    client.ajuda();
            }
        }

        client.tancarClient();
    }
}

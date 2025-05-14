import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread {
    private final Socket socketClient;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private final ServidorXat servidorXat;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket socketClient, ServidorXat servidorXat) {
        this.socketClient = socketClient;
        this.servidorXat = servidorXat;
        try {
            sortida = new ObjectOutputStream(socketClient.getOutputStream());
            entrada = new ObjectInputStream(socketClient.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creant fluxos: " + e.getMessage());
            sortir = true;
        }
        this.nom = "Desconegut";
    }

    public String getNom() {
        return this.nom;
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) entrada.readObject();
                processaMissatge(missatge);
            }
        } catch (Exception e) {
            System.out.println("Error al client: " + nom + " -> " + e.getMessage());
        } finally {
            try {
                entrada.close();
                sortida.close();
                socketClient.close();
            } catch (IOException e) {
                System.out.println("Error tancant connexió amb client: " + nom);
            }
            servidorXat.eliminarClient(nom);
        }
    }

    public void enviarMissatge(String remitent, String msg) {
        try {
            sortida.writeObject(Missatge.getMissatgePersonal(remitent, msg));
        } catch (IOException e) {
            System.out.println("No s'ha pogut enviar el missatge a " + nom);
        }
    }

    public void processaMissatge(String msg) {
        String codi = Missatge.getCodiMissatge(msg);
        String[] parts = Missatge.getPartsMissatge(msg);
        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                this.nom = parts[1];
                servidorXat.afegirClient(this);
                servidorXat.enviarMissatgeGrup("DEBUG: multicast Entra: " + this.nom);
                break;

            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidorXat.eliminarClient(nom);
                break;

            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidorXat.finalitzarXat();
                break;

            case Missatge.CODI_MSG_PERSONAL:
                String destinatari = parts[1];
                String missatge = parts[2];
                servidorXat.enviarMissatgePersonal(destinatari, nom, missatge);
                break;

            case Missatge.CODI_MSG_GRUP:
                servidorXat.enviarMissatgeGrup(parts[1]);
                break;

            default:
                System.out.println("Codi desconegut rebut: " + codi);
        }
    }
}

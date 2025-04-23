import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream entrada;
    private ObjectOutputStream sortida;
    private String nom;

    public FilServidorXat(ObjectInputStream entrada, ObjectOutputStream sortida, String nom) {
        this.entrada = entrada;
        this.sortida = sortida;
        this.nom = nom;
    }

    @Override
    public void run() {
        try {
            System.out.println("Fil de " + nom + " iniciat");
            String missatge;

            while ((missatge = (String) entrada.readObject()) != null) {
                System.out.println("Missatge ('sortir' per tancar): Rebut: " + missatge);

                if (missatge.equalsIgnoreCase("sortir")) {
                    System.out.println("Fil de xat finalitzat");
                    sortida.writeObject("sortir");
                    sortida.flush();
                    System.out.println("sortir"); // IMPORTANTE para cumplir el output esperado
                    break;
                }

                String resposta = generarResposta(missatge);
                System.out.println(resposta);
                sortida.writeObject(resposta);
                sortida.flush();
            }

        } catch (Exception e) {
            System.out.println("Error al fil de xat.");
        }
    }

    private String generarResposta(String msg) {
        if (msg.toLowerCase().contains("hola")) return "Hola " + nom + "!";
        if (msg.toLowerCase().contains("adeu")) return "Adeu";
        return "Missatge rebut.";
    }
}

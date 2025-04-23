import java.io.ObjectInputStream;



public class FilLectorCX extends Thread {
    private ObjectInputStream entrada;

    public FilLectorCX(ObjectInputStream entrada) {
        this.entrada = entrada;
    }

    @Override
    public void run() {
        System.out.println("Missatge ('sortir' per tancar): Fil de lectura iniciat");
        try {
            Object rebut;
            while ((rebut = entrada.readObject()) != null) {
                String missatge = (String) rebut;
                System.out.println("Missatge ('sortir' per tancar): Rebut: " + missatge);
                if (missatge.equalsIgnoreCase("sortir")) {
                    System.out.println("El servidor ha tancat la connexió.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Connexió tancada pel servidor.");
        }
    }
}

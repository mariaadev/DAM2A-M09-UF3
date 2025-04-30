import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() {
        File file = new File(nom);
        if (!file.exists()) {
            System.err.println("Error llegint el fitxer del client: " + nom);
            return null;
        }
        try {
            this.contingut = Files.readAllBytes(file.toPath());
            return contingut;
        } catch (IOException e) {
            System.err.println("Error llegint el fitxer del client: " + e.getMessage());
            return null;
        }
    }

    public String getNom() {
        return nom;
    }
}

package gemini.aventura.service;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageStorageService {

    private final String folder = "image-storage/";

    public void store(String imageName, byte[] bytes) {
        Path path = Paths.get(folder + imageName);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }


    public byte[] get(String imageName) {
        Path path = Paths.get(folder + imageName);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
            return null;
        }
    }
}

package gemini.aventura.controller;

import gemini.aventura.model.AventuraRequest;
import gemini.aventura.model.AventuraResponse;
import gemini.aventura.model.Turno;
import gemini.aventura.service.AventuraService;
import gemini.aventura.service.ImageGenerationService;
import gemini.aventura.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


@RestController
@RequestMapping("/aventura")
@RequiredArgsConstructor
public class AventuraController {

    private final AventuraService aventuraService;
    private final ImageGenerationService imageGenerationService;
    private final ImageStorageService imageStorageService;

    @Autowired
    public AventuraController(ImageGenerationService imageGenerationService, ImageStorageService imageStorageService, AventuraService aventuraService) {
        this.imageGenerationService = imageGenerationService;
        this.imageStorageService = imageStorageService;
        this.aventuraService = aventuraService;
    }

    @PostMapping
    public ResponseEntity<AventuraResponse> crearAventura(@RequestBody AventuraRequest request) {
        AventuraResponse response = aventuraService.crearAventura(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/turno")
    public ResponseEntity<Turno> avanzarTurno(
            @PathVariable String id,
            @RequestParam int opcionElegida
    ) {
        Turno nuevoTurno = aventuraService.avanzarTurno(id, opcionElegida);
        return ResponseEntity.ok(nuevoTurno);
    }

    @GetMapping("/{id}/historia")
    public ResponseEntity<String> getHistoriaCompleta(@PathVariable String id) {
        String historia = aventuraService.getHistoriaCompleta(id);
        return ResponseEntity.ok(historia);
    }


    @PostMapping("/generateImagen")
    public List<String> generate(@RequestParam("prompt") String prompt,
                                 @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        return this.imageGenerationService.generateImages(prompt, images);
    }


    @GetMapping("/ultimaImagen")
    public ResponseEntity<byte[]> getUltimaImagen() {
        List<String> nombresDeImagenes = imageGenerationService.getNombresDeImagenes();
        if (nombresDeImagenes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String ultimaImagenName = nombresDeImagenes.get(nombresDeImagenes.size() - 1);
        byte[] bytes = this.imageStorageService.get(ultimaImagenName);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
    }


}

package gemini.aventura.service;


import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import jakarta.annotation.Nullable;
import org.apache.http.HttpException;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class ImageGenerationService {

    private final String imageGenerationModel = "gemini-2.0-flash-exp-image-generation";

    private final Client genaiClient;
    private final ImageStorageService imageStorageService;
    private List<String> generatedImageNames = new ArrayList<>();

    public ImageGenerationService(Client genaiClient, ImageStorageService imageStorageService) {
        this.genaiClient = genaiClient;
        this.imageStorageService = imageStorageService;
    }


    public List<String> generateImages(String prompt, @Nullable List<MultipartFile> images) {
        List<Part> parts = new ArrayList<>();
        parts.add(Part.fromText(prompt));

        if (images != null) {
            List<Part> imageParts = images.stream()
                    .map(image -> {
                        try {
                            return Part.fromBytes(image.getBytes(), image.getContentType());
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            parts.addAll(imageParts); // Add images
        }

        Content content = Content.builder().parts(parts).build();
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseModalities(List.of("Text", "Image"))
                .build();

        GenerateContentResponse response = this.genaiClient.models.generateContent(imageGenerationModel, content, config);
        List<Image> generatedImages = getImages(response);

        generatedImages.forEach(image -> {
            this.imageStorageService.store(image.imageName(), image.imageBytes());
            this.generatedImageNames.add(image.imageName());
        });

        return generatedImages.stream().map(Image::imageName).toList();

    }


    private List<Image> getImages(GenerateContentResponse response) {
        ImmutableList<Part> responseParts = response.parts();
        if (responseParts == null || responseParts.isEmpty()) {
            return Collections.emptyList();
        }
        return responseParts
                .stream()
                .map(Part::inlineData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(inlineData -> inlineData.data().isPresent())
                .map(inlineData -> {
                    MimeType mimeType = MimeType.valueOf(inlineData.mimeType().get()); // imageMimeType
                    return new Image(
                            "%s.%s".formatted(UUID.randomUUID().toString(), mimeType.getSubtype()),
                            inlineData.data().get(), // imageBytes
                            mimeType.toString());
                })
                .toList();
    }

    public List<String> getNombresDeImagenes() {
        return this.generatedImageNames;
    }

    record Image(String imageName, byte[] imageBytes, String mimeType) {}
}

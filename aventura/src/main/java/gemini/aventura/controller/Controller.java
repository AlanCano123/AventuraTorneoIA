package gemini.aventura.controller;

import gemini.aventura.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/gemini")
@RequiredArgsConstructor
public class Controller {

    private final GeminiService service;

    @GetMapping("/ask")
    public String askGeminiAPI(@RequestBody String prompt) {
        return service.askGemini(prompt);
    }
}

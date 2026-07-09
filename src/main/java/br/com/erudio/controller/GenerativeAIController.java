package br.com.erudio.controller;

import br.com.erudio.service.ChatService;
import br.com.erudio.service.ImageService;
import br.com.erudio.service.RecipeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ai")
public class GenerativeAIController {

    private final ChatService chatService;
    private final RecipeService recipeService;
    private final ImageService imageService;

    public GenerativeAIController(ChatService chatService,
                                  RecipeService recipeService,
                                  ImageService imageService) {
        this.chatService = chatService;
        this.recipeService = recipeService;
        this.imageService = imageService;
    }

    @GetMapping("ask-ai")
    public String getResponse(@RequestParam String prompt) {
        return chatService.getResponse(prompt);
    }

    @GetMapping("ask-ai-options")
    public String getResponseWithOptions(@RequestParam String prompt) {
        return chatService.getResponseWithOptions(prompt);
    }

    @GetMapping("recipe-creator")
    public String recipeCreator(@RequestParam String ingredients,
                                @RequestParam(defaultValue = "any") String cuisine,
                                @RequestParam(defaultValue = "none") String dietaryRestrictions) {

        return recipeService.createRecipe(ingredients, cuisine, dietaryRestrictions);
    }

    @GetMapping("generate-image")
    public List<String> generateImages(@RequestParam String prompt,
                                       @RequestParam(defaultValue = "auto") String quality,
                                       @RequestParam(defaultValue = "1") Integer n,
                                       @RequestParam(defaultValue = "1024") Integer height,
                                       @RequestParam(defaultValue = "1024") Integer width) {

        return imageService.generateImage(prompt, quality, n, height, width);

    }

    @GetMapping("generate-image/view")
    public ResponseEntity<byte[]> generateImageView(@RequestParam String prompt,
                                                    @RequestParam(defaultValue = "auto") String quality,
                                                    @RequestParam(defaultValue = "1") Integer n,
                                                    @RequestParam(defaultValue = "1024") Integer height,
                                                    @RequestParam(defaultValue = "1024") Integer width) {

        byte[] imageBytes = imageService.generateImageBytes(prompt, quality, n, height, width);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(imageBytes.length)
                .body(imageBytes);
    }
}
package br.com.erudio.controller;

import br.com.erudio.service.ChatService;
import br.com.erudio.service.ImageService;
import br.com.erudio.service.RecipeService;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ai")
public class GenerativeAIController {

    private final ChatService chatService;
    private final RecipeService recipeService;
    private final ImageService imageService;

    @Value("${app.image.base-url}")
    private String imageBaseUrl;

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

  //  @GetMapping("/generate-image")
    public ResponseEntity<List<String>> generateImage(
            @RequestParam String prompt,
            @RequestParam(defaultValue = "auto") String quality,
            @RequestParam(defaultValue = "1") Integer n,
            @RequestParam(defaultValue = "1024") Integer height,
            @RequestParam(defaultValue = "1024") Integer width) {

        List<String> imagesList = imageService.generateImageBytes(prompt, quality, n, height, width);

        // Retorna a lista de strings com os prefixos diretamente no corpo do JSON
        return ResponseEntity.ok().body(imagesList);
    }

    @GetMapping("generate-image")
    public ResponseEntity<List<String>> generateSaveImageView(
            @RequestParam String prompt,
            @RequestParam(defaultValue = "auto") String quality,
            @RequestParam(defaultValue = "1") Integer n,
            @RequestParam(defaultValue = "1024") Integer height,
            @RequestParam(defaultValue = "1024") Integer width) {

        List<String> fileNames = imageService.generateAndSaveImages(prompt, quality, n, height, width);

        List<String> imageUrls = fileNames.stream()
                .map(name -> imageBaseUrl + name)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(imageUrls);
    }
    @GetMapping("generate-image/view")
    public ResponseEntity<List<String>> generateImageView(
            @RequestParam String prompt,
            @RequestParam(defaultValue = "auto") String quality,
            @RequestParam(defaultValue = "1") Integer n,
            @RequestParam(defaultValue = "1024") Integer height,
            @RequestParam(defaultValue = "1024") Integer width) {

        List<String> imagesBase64 = imageService.generateImageBytes(prompt, quality, n, height, width);

        // Retorna a lista de imagens dentro de um JSON estruturado
        return ResponseEntity.ok().body(imagesBase64);
    }

    //gero imagens sem salvar no disco, apenas retorno as urls prontas da OpenAI
 //   @GetMapping("/generate-image")
    public ResponseEntity<List<String>> generateImages( @RequestParam String prompt,
                                                           @RequestParam(defaultValue = "auto") String quality,
                                                           @RequestParam(defaultValue = "1") Integer n,
                                                           @RequestParam(defaultValue = "1024") Integer height,
                                                           @RequestParam(defaultValue = "1024") Integer width) {
        // Agora o service já devolve as URLs prontas da OpenAI
        List<String> imageUrls = imageService.generateAndSaveImages(prompt, quality, n, height, width);

        return ResponseEntity.ok().body(imageUrls);
    }

}
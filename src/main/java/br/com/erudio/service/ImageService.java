package br.com.erudio.service;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ImageService {

    private final OpenAiImageModel imageModel;
    @Value("${app.upload.dir}")
    private String uploadDir; // O Spring vai injetar o caminho configurado acima


    public ImageService(OpenAiImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public List<String> generateImageBytes(String prompt, String quality, Integer n, Integer height, Integer width) {
        ImageResponse imageResponse = imageModel.call(new ImagePrompt(prompt, OpenAiImageOptions.builder().quality(quality).N(n).height(height).width(width).build()));

        // Mapeia os resultados e já formata cada String no padrão que o HTML/React entende
        return imageResponse.getResults().stream().map(result -> "data:image/png;base64," + result.getOutput().getB64Json()).collect(Collectors.toList());
    }

    public List<String> generateAndSaveImages(String prompt, String quality, Integer n, Integer height, Integer width) {
        ImageResponse imageResponse = imageModel.call(new ImagePrompt(prompt, OpenAiImageOptions.builder().quality(quality).N(n).height(height).width(width).build()));

        List<String> fileNames = new ArrayList<>();

        // 2. GARANTE QUE O CAMINHO TERMINE COM UMA BARRA ADEQUADA PARA O SISTEMA OPERACIONAL
        String folderPath = uploadDir;
        if (!folderPath.endsWith("/") && !folderPath.endsWith("\\")) {
            folderPath += File.separator;
        }

        // Cria a pasta local se ela não existir
        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Usamos uma variável final para ser acessível dentro do laço lambda do forEach
        final String finalUploadDir = folderPath;

        imageResponse.getResults().forEach(result -> {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(result.getOutput().getB64Json());

                // Gera um nome único para o arquivo para evitar sobreescrita
                String fileName = UUID.randomUUID().toString() + ".png";

                // Salva o binário puro usando o caminho injetado do YAML
                Files.write(Paths.get(finalUploadDir + fileName), imageBytes);

                fileNames.add(fileName);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao salvar imagem no disco", e);
            }
        });

        return fileNames; // Retorna ["uuid1.png", "uuid2.png"]
    }

    public List<String> generateImages(String prompt, String quality, Integer n, Integer height, Integer width) {
        ImageResponse imageResponse = imageModel.call(
                new ImagePrompt(prompt, OpenAiImageOptions.builder()
                        .quality(quality)
                        .N(n)
                        .height(height)
                        .width(width)
                        .build())
        );

        List<String> imageUrls = new ArrayList<>();

        imageResponse.getResults().forEach(result -> {
            // Pega a URL pública e temporária gerada diretamente pela OpenAI
            String openAiUrl = result.getOutput().getUrl();
            imageUrls.add(openAiUrl);
        });

        return imageUrls; // Retorna a lista de URLs da OpenAI: ["https://oaidalleapiprodscus...", ...]
    }


}
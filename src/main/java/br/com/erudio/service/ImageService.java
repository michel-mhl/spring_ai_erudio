package br.com.erudio.service;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ImageService {

        private final OpenAiImageModel imageModel;

        public ImageService(OpenAiImageModel imageModel) {
            this.imageModel = imageModel;
        }

    public List<String> generateImage(String prompt, String quality,
                                      Integer n, Integer height, Integer width) {
        ImageResponse imageResponse = imageModel.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .quality(quality)
                                .N(n)
                                .height(height)
                                .width(width)
                                .build())
        );

        return IntStream.range(0, imageResponse.getResults().size())
                .mapToObj(i -> {
                    var output = imageResponse.getResults().get(i).getOutput();
                    String base64 = output.getB64Json();
                    byte[] bytes = Base64.getDecoder().decode(base64);
                    String fileName = "image-" + System.currentTimeMillis() + "-" + i + ".png";
                    try {
                        Path path = Path.of("src/main/resources/static/images/" + fileName);
                        Files.createDirectories(path.getParent());
                        Files.write(path, bytes);
                        return "/images/" + fileName;
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao salvar imagem", e);
                    }
                })
                .toList();
    }

    public byte[] generateImageBytes(String prompt, String quality,
                                     Integer n, Integer height, Integer width) {
        ImageResponse imageResponse = imageModel.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .quality(quality)
                                .N(n)
                                .height(height)
                                .width(width)
                                .build())
        );
        var output = imageResponse.getResults().get(0).getOutput();
        return Base64.getDecoder().decode(output.getB64Json());
    }

}
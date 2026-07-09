package br.com.erudio.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RecipeService {

    private final ChatClient chatClient;

    public RecipeService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String createRecipe(String ingredients,
                               String cuisine,
                               String dietaryRestrictions) {
        var template = """
                I want to create a recipe using the following ingredients: {ingredients}
                The cuisine type I prefer is {cuisine}.
                Please consider the following dietary restrictions: {dietaryRestrictions}.
                Please provide me with a detailed recipe including title, list of ingredients, and cooking instructions
                """;

        return chatClient.prompt()
                .user(u -> u
                        .text(template)
                        .param("ingredients", ingredients)
                        .param("cuisine", cuisine)
                        .param("dietaryRestrictions", dietaryRestrictions))
                .call()
                .content();
    }

}

// Esse modelo de prompt template preciso criar um map para setar os parametros , então estou usando o chatClient diretamente
//        PromptTemplate promptTemplate = new PromptTemplate(template);
//        Map<String, Object> params = Map.of(
//                "ingredients", ingredients,
//                "cuisine", cuisine,
//                "dietaryRestrictions", dietaryRestrictions
//        );
//
//        Prompt prompt = promptTemplate.create(params);
//  return chatModel.call(prompt).getResult().getOutput().getText();

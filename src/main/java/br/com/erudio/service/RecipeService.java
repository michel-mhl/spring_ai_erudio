package br.com.erudio.service;

import org.springframework.ai.chat.model.ChatModel;

public class RecipeService {
    private final ChatModel chatModel;

    public RecipeService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    //preciso criar o contrutor e o metodo que vai chamar o chatModel.call() para gerar a receita
    //preciso indicar que o prompt vai ser "Generate a recipe for a dish with the following ingredients: " + ingredients


}

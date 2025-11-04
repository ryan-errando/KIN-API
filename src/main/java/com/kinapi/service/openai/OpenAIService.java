package com.kinapi.service.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinapi.common.dto.GeneratedQuestionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OpenAIService {

    private final WebClient openAIWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GPT_4O_MINI = "gpt-4o-mini";

    public OpenAIService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.url}") String apiUrl
    ) {
        this.openAIWebClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Generates a daily question for families to enhance their relationships
     * @return GeneratedQuestionDto containing the question and category
     */
    public Mono<GeneratedQuestionDto> generateFamilyDailyQuestion() {
        String prompt = """
                Generate a thoughtful daily question designed to help family members connect and strengthen their relationships.

                The question should:
                - Encourage meaningful conversation and sharing
                - Be appropriate for all family members (including children and adults)
                - Focus on positive experiences, memories, gratitude, dreams, or understanding each other better
                - Be open-ended to promote discussion
                - Avoid sensitive topics like finances, politics, or controversial subjects

                Categories can be one of: "Gratitude", "Memories", "Dreams & Goals", "Fun & Hobbies", "Values & Beliefs", "Daily Life", "Feelings & Emotions"

                Please respond in the following JSON format only, without any additional text or markdown:
                {
                  "question": "Your generated question here",
                  "category": "Category name here"
                }
                """;

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", GPT_4O_MINI);
        requestBody.put("messages", List.of(userMessage));
        requestBody.put("temperature", 0.8);

        log.info("Generating family daily question using OpenAI model: {}", GPT_4O_MINI);

        return openAIWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::parseQuestionResponse)
                .doOnSuccess(response -> log.info("Successfully generated family question: {}", response.getQuestion()))
                .doOnError(error -> log.error("Error generating family question: {}", error.getMessage()));
    }

    /**
     * Parses the OpenAI response and extracts the generated question
     * @param response The raw OpenAI API response
     * @return GeneratedQuestionDto with question and category
     */
    private GeneratedQuestionDto parseQuestionResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                String content = (String) message.get("content");

                JsonNode jsonNode = objectMapper.readTree(content);

                return GeneratedQuestionDto.builder()
                        .question(jsonNode.get("question").asText())
                        .category(jsonNode.get("category").asText())
                        .build();
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON from OpenAI response: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error extracting question from OpenAI response: {}", e.getMessage());
        }

        return GeneratedQuestionDto.builder()
                .question("What is one thing that made you smile today?")
                .category("Daily Life")
                .build();
    }
}
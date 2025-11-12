package com.kinapi.service.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinapi.common.dto.GeneratedQuestionDto;
import com.kinapi.common.dto.GeneratedQuestionsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
            @Value("${openai.api.token}") String apiKey,
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
     * @param previousQuestions List of recently asked questions to avoid duplicates
     * @return GeneratedQuestionDto containing the question and category
     */
    public Mono<GeneratedQuestionDto> generateFamilyDailyQuestion(List<String> previousQuestions) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("""
                Generate a thoughtful daily question designed to help family members connect and strengthen their relationships.

                The question should:
                - Encourage meaningful conversation and sharing
                - Be appropriate for all family members (including children and adults)
                - Focus on positive experiences, memories, gratitude, dreams, or understanding each other better
                - Be open-ended to promote discussion
                - Avoid sensitive topics like finances, politics, or controversial subjects
                """);

        if (previousQuestions != null && !previousQuestions.isEmpty()) {
            promptBuilder.append("\nIMPORTANT: Avoid generating questions similar to these recently asked questions:\n");
            for (int i = 0; i < previousQuestions.size(); i++) {
                promptBuilder.append(String.format("%d. %s\n", i + 1, previousQuestions.get(i)));
            }
            promptBuilder.append("\nMake sure your generated question is significantly different in topic and approach from the above questions.\n");
        }

        promptBuilder.append("""

                Categories can be one of: "Gratitude", "Memories", "Dreams & Goals", "Fun & Hobbies", "Values & Beliefs", "Daily Life", "Feelings & Emotions"

                Please respond in the following JSON format only, without any additional text or markdown:
                {
                  "question": "Your generated question here",
                  "category": "Category name here"
                }
                """);

        String prompt = promptBuilder.toString();

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

    /**
     * Generates 4 daily questions based on a specific category for family bonding
     * @param category The category to generate questions for
     * @param previousQuestions List of recently asked questions to avoid duplicates
     * @return GeneratedQuestionsDto containing 4 questions and the category
     */
    public Mono<GeneratedQuestionsDto> generateMultipleFamilyQuestions(String category, List<String> previousQuestions) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format("""
                Generate 4 thoughtful daily questions designed to help family members connect and strengthen their relationships.

                All questions must be related to the category: "%s"

                Each question should:
                - Encourage meaningful conversation and sharing among family members
                - Be appropriate for all family members (including children and adults)
                - Focus on positive experiences, memories, gratitude, dreams, or understanding each other better
                - Be open-ended to promote discussion
                - Avoid sensitive topics like finances, politics, or controversial subjects
                - Be unique and different from each other
                """, category));

        if (previousQuestions != null && !previousQuestions.isEmpty()) {
            promptBuilder.append("\nIMPORTANT: Avoid generating questions similar to these recently asked questions:\n");
            for (int i = 0; i < previousQuestions.size(); i++) {
                promptBuilder.append(String.format("%d. %s\n", i + 1, previousQuestions.get(i)));
            }
            promptBuilder.append("\nMake sure your generated questions are significantly different in topic and approach from the above questions.\n");
        }

        promptBuilder.append("""

                Please respond in the following JSON format only, without any additional text or markdown:
                {
                  "questions": [
                    "First question here",
                    "Second question here",
                    "Third question here",
                    "Fourth question here"
                  ]
                }
                """);

        String prompt = promptBuilder.toString();

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", GPT_4O_MINI);
        requestBody.put("messages", List.of(userMessage));
        requestBody.put("temperature", 0.8);

        log.info("Generating 4 family questions for category '{}' using OpenAI model: {}", category, GPT_4O_MINI);

        return openAIWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> parseMultipleQuestionsResponse(response, category))
                .doOnSuccess(response -> log.info("Successfully generated 4 family questions for category: {}", category))
                .doOnError(error -> log.error("Error generating family questions: {}", error.getMessage()));
    }

    /**
     * Generates 1 daily question based on a specific category for family bonding
     * @param category The category to generate question for
     * @param previousQuestions List of recently asked questions to avoid duplicates
     * @return GeneratedQuestionDto containing 1 question and the category
     */
    public Mono<GeneratedQuestionDto> generateSingleFamilyQuestion(String category, List<String> previousQuestions) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format("""
                Generate a thoughtful daily question designed to help family members connect and strengthen their relationships.

                The question must be related to the category: "%s"

                The question should:
                - Encourage meaningful conversation and sharing among family members
                - Be appropriate for all family members (including children and adults)
                - Focus on positive experiences, memories, gratitude, dreams, or understanding each other better
                - Be open-ended to promote discussion
                - Avoid sensitive topics like finances, politics, or controversial subjects
                """, category));

        if (previousQuestions != null && !previousQuestions.isEmpty()) {
            promptBuilder.append("\nIMPORTANT: Avoid generating questions similar to these recently asked questions:\n");
            for (int i = 0; i < previousQuestions.size(); i++) {
                promptBuilder.append(String.format("%d. %s\n", i + 1, previousQuestions.get(i)));
            }
            promptBuilder.append("\nMake sure your generated question is significantly different in topic and approach from the above questions.\n");
        }

        promptBuilder.append("""

                Please respond in the following JSON format only, without any additional text or markdown:
                {
                  "question": "Your generated question here"
                }
                """);

        String prompt = promptBuilder.toString();

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", GPT_4O_MINI);
        requestBody.put("messages", List.of(userMessage));
        requestBody.put("temperature", 0.8);

        log.info("Generating single family question for category '{}' using OpenAI model: {}", category, GPT_4O_MINI);

        return openAIWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> parseSingleQuestionResponse(response, category))
                .doOnSuccess(response -> log.info("Successfully generated family question for category: {}", category))
                .doOnError(error -> log.error("Error generating family question: {}", error.getMessage()));
    }

    /**
     * Parses the OpenAI response for multiple questions
     * @param response The raw OpenAI API response
     * @param category The category of the questions
     * @return GeneratedQuestionsDto with questions and category
     */
    private GeneratedQuestionsDto parseMultipleQuestionsResponse(Map<String, Object> response, String category) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                String content = (String) message.get("content");

                JsonNode jsonNode = objectMapper.readTree(content);
                JsonNode questionsNode = jsonNode.get("questions");

                List<String> questions = new ArrayList<>();
                if (questionsNode.isArray()) {
                    for (JsonNode questionNode : questionsNode) {
                        questions.add(questionNode.asText());
                    }
                }

                return GeneratedQuestionsDto.builder()
                        .questions(questions)
                        .category(category)
                        .build();
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON from OpenAI response: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error extracting questions from OpenAI response: {}", e.getMessage());
        }

        // Fallback questions
        return GeneratedQuestionsDto.builder()
                .questions(List.of(
                        "What is one thing that made you smile today?",
                        "What are you grateful for today?",
                        "What is something new you learned recently?",
                        "What is a happy memory you have with the family?"
                ))
                .category(category)
                .build();
    }

    /**
     * Parses the OpenAI response for a single question
     * @param response The raw OpenAI API response
     * @param category The category of the question
     * @return GeneratedQuestionDto with question and category
     */
    private GeneratedQuestionDto parseSingleQuestionResponse(Map<String, Object> response, String category) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                String content = (String) message.get("content");

                JsonNode jsonNode = objectMapper.readTree(content);

                return GeneratedQuestionDto.builder()
                        .question(jsonNode.get("question").asText())
                        .category(category)
                        .build();
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON from OpenAI response: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error extracting question from OpenAI response: {}", e.getMessage());
        }

        return GeneratedQuestionDto.builder()
                .question("What is one thing that made you smile today?")
                .category(category)
                .build();
    }
}
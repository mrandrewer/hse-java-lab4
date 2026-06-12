package com.hse.lab4.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;

import java.util.List;
import java.util.Optional;

/**
 * Реализация поставщика вопросов через AI (DeepSeek)
 */
public class AIQuestionProvider implements IQuestionProvider {

    private final OpenAIClient client;
    private final String model;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIQuestionProvider(String apiUrl, String apiKey, String modelName) {
        this.model = modelName;
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl != null ? apiUrl : "https://api.deepseek.com/v1")
                .build();
    }

    @Override
    public Question getQuestion(int level) {
        if (level < 0 || level > 15) {
            return null;
        }

        String prompt = buildPrompt(level);

        try {
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addUserMessage(prompt)
                    .model(model)
                    .temperature(0.7)
                    .build();

            var completion = client.chat().completions().create(params);
            Optional<ChatCompletionMessage> firstChoice = completion.choices().stream()
                    .findFirst()
                    .map(choice -> choice.message());

            if (firstChoice.isEmpty()) {
                return null;
            }

            String content = firstChoice.get().content().orElse(null);
            String json = extractJson(content);
            if (json == null) {
                return null;
            }

            // Parse JSON into a Question object
            AiGeneratedQuestionData data = objectMapper.readValue(json, AiGeneratedQuestionData.class);
            return data.toQuestion(level);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Построение промта
     */
    private String buildPrompt(int level) {
        return """
                Ты автор вопросов для игры "Кто хочет стать миллионером?".
                В игре 15 уровней сложности от простого (1) до супер сложного (15)
                Сгенерируй один уникальный вопрос для уровня сложности %d

                ### Для уровня 0–3 (очень лёгкий):
                Вопрос должен быть настолько простым, чтобы на него ответил почти любой взрослый (даже ребёнок 10 лет).
                - Темы: столицы стран, очевидные факты о природе, простые правила русского языка, стандартные единицы измерения, базовые математические понятия.
                - ЗАПРЕЩЕНО: история (даты, войны, правители), сложная наука, узкопрофессиональные термины, логические головоломки с подвохом.
                - Пример хорошего вопроса: «Как называется столица Франции?» (варианты: Париж, Марсель, Лион, Бордо).

                ### Для уровня 4–7 (средний):
                Вопрос требует более глубоких знаний, но остаётся в рамках школьной программы.

                ### Для уровня 8–11 (сложный):
                Узкая тема, возможно профессиональная или из редких областей.

                ### Для уровня 12–15 (очень сложный):
                Экзотический вопрос, на который правильно ответит 1 из 1000

                Четыре варианта ответа должны быть:
                - правдоподобными (не абсурдными);
                - не содержать подсказок вроде «все перечисленные»;
                - разной длины и структуры;
                - правильный ответ — абсолютно точным.

                Верни ТОЛЬКО валидный JSON-объект со следующими полями:
                {
                    "text": "Текст вопроса",
                    "answers": ["ответ A", "ответ B", "ответ C", "ответ D"],
                    "correctAnswer": 0   // индекс правильного ответа (0-3)
                }

                """
                .formatted(level);
    }

    /**
     * Извлечение JSON из ответа
     */
    private String extractJson(String content) {
        if (content == null) {
            return null;
        }
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return content.substring(start, end + 1);
        }
        return null;
    }

    /**
     * Internal class representing the AI response structure.
     */
    private static class AiGeneratedQuestionData {
        public String text;
        public List<String> answers;
        public int correctAnswer;

        public Question toQuestion(int level) {
            if (text == null || answers == null || answers.size() != 4) {
                throw new IllegalArgumentException("Invalid AI response: missing fields or wrong answer count");
            }
            if (correctAnswer < 0 || correctAnswer > 3) {
                throw new IllegalArgumentException("correctAnswer must be 0-3, got " + correctAnswer);
            }
            return new Question(text, answers.toArray(new String[0]), correctAnswer, level);
        }
    }
}
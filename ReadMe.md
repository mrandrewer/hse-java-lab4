# Лабораторная работа 4. Игра «Кто хочет стать миллионером?»

## Задача

Цель работы — реализовать настольное приложение по мотивам телевизионного шоу «Кто хочет стать миллионером?». В программе игрок последовательно отвечает на 15 вопросов возрастающей сложности. Каждый вопрос содержит 4 варианта ответа, из которых только один является правильным. Время ответа не ограничено.

Основные задачи работы:

1. Реализовать игровой процесс и возможности, описанные в методических рекомендациях.
2. Реализовать хранение вопросов игры в базе данных SQLite.
3. Реализовать хранение таблицы рекордов в базе данных и вывод лучших результатов.
4. Реализовать возможность динамической генерации вопросов средствами генеративного ИИ через подключение к API.

## Модель данных

Для отделения игровой логики от конкретного способа получения вопросов введён общий интерфейс `IQuestionProvider`.

```plantuml
@startuml
skinparam classAttributeIconSize 0

package "com.hse.lab4.data" {
    interface IQuestionProvider {
        + Question getQuestion(int level)
    }

    class Question {
        - int id
        - String text
        - String answer1
        - String answer2
        - String answer3
        - String answer4
        - int correctAnswer
        - int level
        + String[] getAnswers()
        + int getCorrectAnswer()
        + int getLevel()
    }

    class TxtQuestionRepository {
        - List<Question>[] questionsByLevel
        - Random random
        + void load(String filePath)
        + Question getQuestion(int level)
        + List<Question> getAllQuestions()
    }

    class DBQuestionRepository {
        - ConnectionSource connectionSource
        - Dao<Question, Integer> questionDao
        + Question getQuestion(int level)
        + void importQuestions(List<Question> questions)
        + void clearQuestions()
    }

    class AIQuestionProvider {
        - OpenAIClient client
        - ObjectMapper objectMapper
        - String model
        + Question getQuestion(int level)
    }

    IQuestionProvider <|.. TxtQuestionRepository
    IQuestionProvider <|.. DBQuestionRepository
    IQuestionProvider <|.. AIQuestionProvider

    TxtQuestionRepository --> Question
    DBQuestionRepository --> Question
    AIQuestionProvider --> Question
}

package "com.hse.lab4.engine" {
    class Game {
        - Question currentQuestion
        - MoneyLevel currentLevel
        - IQuestionProvider questionProvider
        + void newGame()
        + boolean nextLevel()
        + boolean checkAnswer(int answerIndex)
        + Question repalceQuestion()
    }
}

Game --> IQuestionProvider
@enduml
```

Контроллер `PrimaryController` связывает пользовательский интерфейс с игровым движком. `Game` отвечает за текущий уровень, текущий вопрос, проверку ответа и переход к следующему уровню. Репозитории в пакете `data` отвечают за получение и сохранение данных.

## Диаграмма последовательности получения вопроса

```mermaid
sequenceDiagram
    participant UI as PrimaryController
    participant Game as Game
    participant Provider as IQuestionProvider
    participant Source as TXT / SQLite / API

    UI->>Game: newGame()/nextLevel()
    Game->>Provider: getQuestion(level)
    Provider->>Source: загрузить или сгенерировать вопрос
    Source-->>Provider: данные вопроса
    Provider-->>Game: Question
    Game-->>UI: currentQuestion
    UI->>UI: отобразить текст и ответы

    UI->>Game: checkAnswer(answerIndex)
    Game-->>UI: true / false
```

## Хранение вопросов в SQLite

Для таблицы вопросов используется класс `Question`. Репозиторий `DBQuestionRepository` создаёт таблицу при запуске и предоставляет два основных сценария:

1. получение случайного вопроса по уровню сложности;
2. импорт набора вопросов из текстового файла.

Для сохранения результатов используется класс `DBRecordRepository`. При окончании игры, победе или добровольном завершении с выигрышем приложение запрашивает имя игрока и сохраняет результат.

```mermaid
erDiagram
    QUESTIONS {
        int id PK
        string text
        string answer1
        string answer2
        string answer3
        string answer4
        int correctAnswer
        int level
    }

    RECORDS {
        int id PK
        string playerName
        string playedAt
        int amount
    }
```

Таблицы не связаны внешними ключами: вопросы используются в игровом процессе, а записи рекордов фиксируют независимые результаты прохождений.

## Сборка и запуск

Для сборки и запуска проекта используется Maven.
Для режима ИИ перед запуском необходимо установить переменную окружения `MILLIONAIRE_API_KEY`.

```bash
mvn clean javafx:run
```

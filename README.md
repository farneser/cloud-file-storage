# Шестое задание курса [java-backend-learning-course](https://zhukovsd.github.io/java-backend-learning-course/)

[![Java CI with Maven](https://github.com/farneser/cloud-file-storage/actions/workflows/maven.yml/badge.svg)](https://github.com/farneser/cloud-file-storage/actions/workflows/maven.yml)
[![Docker Image CI](https://github.com/farneser/cloud-file-storage/actions/workflows/docker-image.yml/badge.svg)](https://github.com/farneser/cloud-file-storage/actions/workflows/docker-image.yml)
[![Checkstyle validation](https://github.com/farneser/cloud-file-storage/actions/workflows/checkstyle.yml/badge.svg)](https://github.com/farneser/cloud-file-storage/actions/workflows/checkstyle.yml)

## [Задание](https://zhukovsd.github.io/java-backend-learning-course/Projects/CloudFileStorage/)

Многопользовательское файловое облако. Пользователи сервиса могут использовать его для загрузки и хранения файлов.
Источником вдохновения для проекта является Google Drive.

## Запуск

### Полностью локально

```bash
sudo docker-compose up -f docker-compose-local.yml
```

### Бд на сервере

Необходимо в `docker-compose-remote.yml` настроить

```bash
sudo docker-compose up -f docker-compose-remote.yml
```

## Docker

Образ доступен на [DockerHub](https://hub.docker.com/repository/docker/farneser/cloud-file-storage/general)

Пример запуска с подключением на удаленные СУБД

```yaml
version: '3'

services:
  cloud-file-storage:
    image: farneser/cloud-file-storage:latest
    ports:
      - "8080:8080"
    environment:
      POSTGRES_HOST: localhost
      POSTGRES_PORT: 5432
      POSTGRES_DB: cloud-file-storage
      POSTGRES_USERNAME: postgres
      POSTGRES_PASSWORD: postgres
      REDIS_HOST: localhost
      REDIS_PORT: 6379
      MINIO_HOST: MINIO_HOST_PATH # example http://localhost:9000
      MINIO_ACCESS_KEY: YOUR_MINIO_ACCESS_KEY
      MINIO_SECRET_KEY: YOUR_MINIO_SECRET_KEY
      MINIO_FILES_BUCKET: user-files
```

Запуск и больше информации можно узнать из [официальной документации](https://docs.docker.com/compose/reference/)

Базовая команда запуска

```bash
docker-compose up -d
```

## Переменные окружения

| Наименование       | Стандартное значение    | Описание                                                                                                                                                  |
|--------------------|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| POSTGRES_HOST      | `localhost`             | ipv4 адрес постгрес                                                                                                                                       |
| POSTGRES_PORT      | `5432`                  | порт постгрес                                                                                                                                             |
| POSTGRES_DB        | `cloud-file-storage`    | название базы данных постгрес                                                                                                                             |
| POSTGRES_USERNAME  | `postgres`              | наименование пользователя постгрес                                                                                                                        |
| POSTGRES_PASSWORD  | `postgres`              | пароль пользователя постгрес                                                                                                                              |
| REDIS_HOST         | `localhost`             | ipv4 адрес редис                                                                                                                                          |
| REDIS_PORT         | `6379`                  | порт редис                                                                                                                                                |
| MINIO_HOST         | `http://localhost:9000` | url подключения к minio                                                                                                                                   |
| MINIO_ACCESS_KEY   |                         | ключ подключения к minio                                                                                                                                  |
| MINIO_SECRET_KEY   |                         | секретный ключ базы minio                                                                                                                                 |
| MINIO_FILES_BUCKET | `user-files`            | наименование корневого бакета в minio                                                                                                                     |
| LOGGING_LEVEL      | `INFO`                  | [Уровень логирования](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-logging.html) (ERROR, WARN, INFO, DEBUG, TRACE) |

## Что нужно знать

- [Java](https://zhukovsd.github.io/Technologies/Java/) - коллекции, ООП
- [Maven/Gradle](https://zhukovsd.github.io/Technologies/BuildSystems/)
- [Backend](https://zhukovsd.github.io/Technologies/Backend/)
    - Spring Boot, Spring Security, Spring Sessions
    - Thymeleaf
    - Upload файлов, заголовки HTTP запросов, cookies, cессии
- [Базы данных](https://zhukovsd.github.io/Technologies/Databases/)
    - SQL
    - Spring Data JPA
    - Представление о NoSQL хранилищах
- [Frontend](https://zhukovsd.github.io/Technologies/Frontend/) - HTML/CSS, Bootstrap
- [Docker](https://zhukovsd.github.io/Technologies/Microservices/#docker) - контейнеры, образы, volumes, Docker Compose
- [Тесты](https://zhukovsd.github.io/Technologies/Tests/) - интеграционное тестирование, JUnit, Testcontainers
- [Деплой](https://zhukovsd.github.io/Technologies/DevOps/#деплой) - облачный хостинг, командная строка Linux, Tomcat

## Мотивация проекта

- Использование возможностей Spring Boot
- Практика с Docker и Docker Compose
- Первый проект, где студент самостоятельно разрабатывает структуру БД
- Знакомство с NoSQL хранилищами - S3 для файлов, Redis для сессий

## Функционал приложения

Работа с пользователями:

- Регистрация
- Авторизация
- Logout

Работа с файлами и папками:

- Загрузка файлов и папок
- Создание новой пустой папки (аналогично созданию новой папки в проводнике)
- Удаление
- Переименование

## Работа с сессиями, авторизацией, регистрацией

В предыдущем проекте мы управляли сессиями пользователей вручную, в этом проекте воспользуемся возможности экосистемы
Spring Boot.

За авторизацию, управление доступом к страницам отвечает Spring Security.

За работу с сессиями отвечает Spring Sessions. По умолчанию Spring Boot хранит сессии внутри приложения, и они теряются
после каждого перезапуска приложения. Мы воспользуемся Redis для хранения сессий.
Пример - [https://www.baeldung.com/spring-session](https://www.baeldung.com/spring-session). Redis - NoSQL хранилище,
имеющее встроенный TTL (time to live) атрибут для записей, что делает его удобным для хранения сессий - истекшие сессии
автоматически удаляются.

## Тесты

### Интеграционные тесты сервиса по работе с пользователями

Как и в прошлом проекте, покроем тестами связку слоя данных с классами-сервисами, отвечающими за пользователей.

Вместо с H2 предлагаю воспользоваться Testcontainers для запуска тестов в контексте полноценной (а не in-memory) базы
данных. Это позволяет приблизить окружение тестов к рабочему окружению, и тестировать нюансы, специфичные для конкретных
движков БД.

Примеры тест кейсов:

- Вызов метода "создать пользователя" в сервисе, отвечающем за работу с пользователями, приводит к появлению новой
  записи в таблице `users`
- Создание пользователя с неуникальным username приводит к ожидаемому типу исключения

### Интеграционные тесты сервиса по работе с файлами и папками

Опциональное задание повышенной сложности - покрыть тестами взаимодействие с сервисом хранения данных, работающим Minio.

Примеры тест кейсов:

- Загрузка файла приводит к его появлению в bucket'е Minio в корневой папке текущего пользователя
- Переименование, удаление файлов и папок приводит к ожидаемому результату
- Проверка прав доступа - пользователь не должен иметь доступа к чужим файлам
- Поиск - пользователь может находить свои файлы, но не чужие

Что потребуется:

- Интеграция JUnit и Spring Security
- Реализация [GenericContainer](https://www.testcontainers.org/features/creating_container/) для интеграции Minio и
  Testcontainers

## Docker

В данном проекте впервые воспользуемся Docker для удобного запуска необходимых приложений - SQL базы, файлового
хранилища MinIO и хранилища сессий Redis.

Необходимо:

- Найти образы для каждого нужного приложения из списка выше
- Написать Docker Compose файл для запуска стека с приложениями (по контейнеру для каждого)
- Знать Docker Compose команды для работы со стеком

Как будет выглядеть работа с Docker:

- Для работы над проектом запускаем стек из контейнеров
- Уничтожаем или останавливаем контейнеры (с сохранением данных на volumes), когда работа не ведётся
- По необходимости уничтожаем данные на volumes, если хотим очистить то или иное хранилище, запустить

## Деплой

Будем вручную деплоить jar артефакт. Для его запуска не требуется Tomcat, потому что в собранное Spring Boot приложение
уже встроен веб-сервер. Все остальные приложения этого проекта (SQL, Redis, MinIO) запускаем через Docker Compose.

Шаги:

- Локально собрать jar артефакт приложения
- В хостинг-провайдере по выбору арендовать облачный сервер на Linux
- Установить JRE, Docker
- Скопировать на удалённый сервер Docker Compose файл для запуска MySQL, Redis, MinIO
- Скопировать на удалённый сервер локально собранный jar, запустить

Ожидаемый результат - приложение доступно по адресу `http://$server_ip:8080/`.

## План работы над приложением

- Docker Compose - добавить MySQL
- Spring Boot - с помощью Spring Security, Thymeleaf и Spring Data JPA реализовать регистрацию и авторизацию
  пользователей
- Интеграционные тесты для сервиса регистрации
- Docker Compose - добавить MinIO
- Spring Boot - интегрировать AWS Java SDK и научиться совершать операции с файлами в бакете, написать сервис,
  инкапсулирующий необходимые для приложения операции
- Реализовать загрузку файлов и папок через форму (формы) на главной странице
- Реализовать отображение файлов и навигацию по структуре директорий, действия с файлами (удаление, переименование)
- Поиск файлов - сервис, контроллер и Thymeleaf шаблон
- (Опционально) интеграционные тесты для сервиса, отвечающего за работу с файлами и папками
- Docker Compose - добавить Redis
- Spring Sessions - сконфигурировать хранение сессий внутри Redis
- Деплой

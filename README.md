# Шестое задание курса [java-backend-learning-course](https://zhukovsd.github.io/java-backend-learning-course/)

[![Java CI with Maven](https://github.com/farneser/cloud-file-storage/actions/workflows/maven.yml/badge.svg)](https://github.com/farneser/cloud-file-storage/actions/workflows/maven.yml)
[![Docker Image CI](https://github.com/farneser/cloud-file-storage/actions/workflows/docker-image.yml/badge.svg)](https://github.com/farneser/cloud-file-storage/actions/workflows/docker-image.yml)
[![Checkstyle validation](https://github.com/farneser/cloud-file-storage/actions/workflows/checkstyle.yml/badge.svg)](https://github.com/farneser/cloud-file-storage/actions/workflows/checkstyle.yml)

## [Задание](https://zhukovsd.github.io/java-backend-learning-course/Projects/CloudFileStorage/)

Многопользовательское файловое облако. Пользователи сервиса могут использовать его для загрузки и хранения файлов.
Источником вдохновения для проекта является Google Drive.

## Запуск

### Полностью локально

Приложение будет доступно по пути [http://localhost:8080](http://localhost:8080)

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

## Эндпоинты

Эндпоинты описаны в [README.md](src/main/java/com/farneser/cloudfilestorage/controller/README.md)

## Стек технологий

* [spring boot](https://spring.io/projects/spring-boot)
* [spring data jpa](https://spring.io/projects/spring-data-jpa)
* [postgresql](https://www.postgresql.org/)
* [spring security](https://spring.io/projects/spring-security)
* [redis](https://redis.io/)
* [minio](https://min.io/)
* [docker](https://www.docker.com/)

Тесты

* [testcontainers](https://www.testcontainers.org/)
* [h2database](https://www.h2database.com/html/main.html)

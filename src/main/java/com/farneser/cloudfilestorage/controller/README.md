# Endpoints

## /login

Страница входа и страница, на которую будет выполнен переход в случае требования авторизации

Контент:

- Форма входа

POST Параметры

| Параметр | Тип    |
|----------|--------|
| username | string |
| password | string |

## /register

Страница регистрации нового пользователя

Контент:

- Форма регистрации

POST Параметры

| Параметр        | Тип    |
|-----------------|--------|
| username        | string |
| password        | string |
| confirmPassword | string |

## /

Главная страница приложения

Контент:

- Список элементов текущей папки

GET Параметры

| Параметр | Тип    |
|----------|--------|
| path     | string |

## /storage

Эндпоинт загрузки файлов
Файлы загружаются в исходном виде, а пипки в виде архива

GET Параметры

| Параметр | Тип    |
|----------|--------|
| path     | string |

## /storage/rename

Эндпоинт загрузки файлов на сервер

POST Параметры

| Параметр   | Тип    |
|------------|--------|
| path       | string |
| objectPath | string |
| newName    | string |

## /storage/file

Эндпоинт загрузки файлов на сервер

POST Параметры (multipart/form-data)

| Параметр | Тип           |
|----------|---------------|
| path     | string        |
| file     | MultipartFile |

## /storage/folder

Эндпоинт загрузки папки на сервер

POST Параметры (multipart/form-data)

| Параметр | Тип             |
|----------|-----------------|
| path     | string          |
| folder   | MultipartFile[] |

## /storage/folder/create

Эндпоинт создания пустой папки

POST Параметры

| Параметр   | Тип    |
|------------|--------|
| path       | string |
| folderName | string |

## /storage/delete

Эндпоинт удаления объекта

POST Параметры

| Параметр   | Тип    |
|------------|--------|
| path       | string |
| objectName | string |

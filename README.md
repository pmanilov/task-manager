# Task manager

Это проект для выполнения тестового задания.

## Сборка проекта

Для сборки проекта используйте Gradle. Перейдите в корневую директорию проекта и выполните следующую команду в терминале(linux):

```bash
./gradlew build
```

На windows команда:

```bash
gradlew.bat build
```

После успешной сборки, в директории build/libs будет создан исполняемый JAR-файл task-manager-1.0.jar.

## Запуск программы

Запустите docker контейнер с помощью команды:

```bash
docker-compose up
```

После этой команды сервер в контейнере запустится и можно отправлять запросы на порт 8081. 

Документация swagger ui будет по адресу : http://localhost:8081/swagger-ui/index.html#/

Также возможно посмотреть результаты тестов написав команду(linux):

```bash
./gradlew test
```

На windows команда:

```bash
gradlew.bat test
```

Результаты будут лежать в директории build/test-results в виде xml файлов.
# 🧠 homeowners-data-service — Платформа для управления данными домовладельцев

**MVP-система для централизованного сбора, хранения и обработки информации о домовладельцах**

## 📝 Описание

Этот репозиторий содержит набор микросервисов на **Spring Boot**, реализующих backend-платформу для подачи и управления сведениями о домовладельцах. Система обеспечивает безопасное хранение данных, валидацию вводимой информации и подготовку отчетов для управляющих организаций.

---

## 🔧 Технологический стек

- **Язык и фреймворки**: Java 21, Spring Boot 3.5.6, Spring Cloud 2025.0.0
- **Безопасность**: JWT (EC-256), Spring Security
- **Хранилища**:
  - PostgreSQL (Spring Data JPA + Liquibase)
  - Redis — кэш, сессии, токены
- **Месседжинг**: Apache Kafka (Spring Kafka)
- **Инструменты**: Lombok, MapStruct, Caffeine, Thymeleaf
- **Документация**: OpenAPI 3 (Swagger UI), Javadoc
- **Контейнеризация**: Docker, Docker Compose

---

## 🚀 Начало работы

### Требования

- Docker & Docker Compose
### Переменные окружения

- ACTIVE_PROFILE= Активный профиль Spring: dev, prod

- POSTGRES_USER= Имя пользователя базы данных PostgreSQL
- POSTGRES_PASSWORD= Пароль пользователя PostgreSQL
- POSTGRES_DB= Название базы данных PostgreSQL

- PGADMIN_DEFAULT_EMAIL= Email для входа в pgAdmin
- PGADMIN_DEFAULT_PASSWORD= Пароль для входа в pgAdmin

- FRONT_URL= URL фронтенда, используется в email-ссылках и редиректах

- ACCESS_TOKEN_DURATION= Время жизни Access Token (например: 900000) ms
- REFRESH_TOKEN_DURATION= Время жизни Refresh Token (например: 2592000000) ms

- SPRING_SECURITY_USER_NAME= Логин для доступа к UI Kafka (если включена базовая аутентификация)
- SPRING_SECURITY_USER_PASSWORD= Пароль для Kafka UI

- MAIL_USERNAME= Email пользователя для отправки писем (SMTP)
- MAIL_PASSWORD= Пароль или app-password для SMTP
- COMPANY_NAME= Название компании, используется в email и уведомлениях

- SMS_API_ID= API ID для доступа к SMS.RU API


### Запуск

```bash
# Из корня репозитория
docker compose up -d --build
```

После запуска:

- API доступно на `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- База данных и сервисы инициализируются автоматически (включая демо-задачи)
- В логах authentication-service при первом запуске будет пароль для админа

---

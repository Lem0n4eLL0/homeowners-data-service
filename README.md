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
- Переменные окружения:

```bash
export ADMIN_EMAIL=<email администратора по умолчанию>
export MAIL_USERNAME=<SMTP username>
export MAIL_PASSWORD=<SMTP password>
export ACCESS_TOKEN_DURATION=<Время жизни access токена в милисекундах> # рекомендую 900000
export REFRESH_TOKEN_DURATION=<Время жизни refresh токена в милисекундах> # рекомендую 2592000000
export FRONT_URL=<http://localhost:5173> # или укажите свой
```

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

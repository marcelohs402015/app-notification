# App Notification - Android Email Event Extractor

Aplicativo Android desenvolvido seguindo **Clean Architecture**, **MVVM** e **SOLID Principles** para ler emails do Gmail, extrair informações de eventos usando IA (Gemini) e agendar no Google Calendar.

## Arquitetura

O projeto segue **Clean Architecture** com separação estrita em camadas:

- **Domain**: Models, UseCases, Repository Interfaces (camada de negócio)
- **Data**: Repositories, DataSources, Room Database (camada de dados)
- **Presentation**: ViewModels, Compose UI, Navigation (camada de apresentação)

## Stack Tecnológica

- **Kotlin** (versão mais recente)
- **Jetpack Compose** (Material 3)
- **Coroutines & Flow** para assincronismo
- **Room Database** para persistência local (Offline-first)
- **Hilt** para Dependency Injection
- **Gmail API** para leitura de emails
- **Gemini AI** (google.generativeai) para extração de eventos
- **Google Calendar API** para agendamento
- **WorkManager** para sincronização em background

## Configuração

### 1. API Keys

Adicione sua chave da API Gemini no arquivo `app/src/main/java/com/appnotification/di/NetworkModule.kt`:

```kotlin
@Provides
fun provideGeminiApiKey(): String {
    return "YOUR_GEMINI_API_KEY"
}
```

### 2. Google OAuth

Configure o OAuth no Google Cloud Console:
1. Crie um projeto no [Google Cloud Console](https://console.cloud.google.com/)
2. Ative as APIs: Gmail API e Google Calendar API
3. Configure OAuth 2.0 credentials
4. Adicione o SHA-1 do seu keystore

### 3. Permissões

As permissões necessárias já estão configuradas no `AndroidManifest.xml`:
- `INTERNET`
- `ACCESS_NETWORK_STATE`

## Estrutura do Projeto

```
app/src/main/java/com/appnotification/
├── domain/
│   ├── model/          # Domain models (Email, Event, Resource)
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Use cases (business logic)
├── data/
│   ├── local/          # Room database (entities, DAOs)
│   ├── remote/         # DataSources (Gmail, Gemini, Calendar)
│   ├── mapper/         # Mappers (Entity <-> Domain)
│   └── repository/     # Repository implementations
├── presentation/
│   ├── viewmodel/      # ViewModels (MVVM)
│   ├── ui/
│   │   ├── screen/     # Compose screens
│   │   └── theme/       # Material 3 theme
│   └── navigation/     # Navigation graph
├── di/                 # Hilt modules
└── worker/             # WorkManager workers
```

## Padrões Aplicados

- **Repository Pattern**: Abstração da fonte de dados
- **Dependency Injection**: Hilt para injeção de dependências
- **Observer Pattern**: Flow para reatividade
- **Strategy Pattern**: Diferentes estratégias de extração (via Gemini)
- **Single Responsibility**: Cada classe tem uma responsabilidade única
- **Dependency Inversion**: Interfaces no Domain, implementações no Data

## Funcionalidades

1. **Sincronização de Emails**: Busca emails do Gmail e armazena localmente
2. **Extração de Eventos**: Usa Gemini AI para identificar eventos nos emails
3. **Agendamento**: Permite agendar eventos extraídos no Google Calendar
4. **Offline-first**: Cache local com Room Database
5. **Background Sync**: WorkManager sincroniza emails periodicamente

## Desenvolvimento

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew installDebug
```

## Licença

Este projeto é um exemplo de arquitetura Android seguindo as melhores práticas de engenharia de software.

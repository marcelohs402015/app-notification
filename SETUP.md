# Guia de Configuração - App Notification

## Pré-requisitos

1. **Android Studio** (versão mais recente)
2. **JDK 17** ou superior
3. **Conta Google Cloud** com APIs habilitadas
4. **Chave da API Gemini**

## Passo a Passo

### 1. Configurar Google Cloud Console

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto ou selecione um existente
3. Ative as seguintes APIs:
   - **Gmail API**
   - **Google Calendar API**
4. Configure OAuth 2.0:
   - Vá em "APIs & Services" > "Credentials"
   - Clique em "Create Credentials" > "OAuth client ID"
   - Selecione "Android" como tipo de aplicativo
   - Adicione o package name: `com.appnotification`
   - Adicione o SHA-1 do seu keystore (veja abaixo como obter)

#### Obter SHA-1 do Keystore

Para debug:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Para release, use o keystore de produção.

### 2. Configurar API Key do Gemini

1. Acesse [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Crie uma nova API key
3. No arquivo `app/src/main/java/com/appnotification/di/NetworkModule.kt`, substitua:

```kotlin
@Provides
fun provideGeminiApiKey(): String {
    return "YOUR_GEMINI_API_KEY" // Substitua pela sua chave
}
```

**Alternativa (Recomendada)**: Use `BuildConfig` ou `local.properties`:

1. Adicione em `local.properties`:
```properties
gemini.api.key=YOUR_GEMINI_API_KEY
```

2. Configure no `build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("gemini.api.key")}\"")
    }
}
```

3. Use no módulo:
```kotlin
@Provides
fun provideGeminiApiKey(): String {
    return BuildConfig.GEMINI_API_KEY
}
```

### 3. Configurar Autenticação Google

O app usa `GoogleAccountCredential` para autenticação. Você precisará:

1. Implementar a tela de autenticação (veja `AuthScreen.kt`)
2. Solicitar as permissões necessárias:
   - `GmailScopes.GMAIL_READONLY`
   - `CalendarScopes.CALENDAR`

Exemplo de implementação:

```kotlin
val credential = GoogleAccountCredential.usingOAuth2(
    context,
    listOf(GmailScopes.GMAIL_READONLY, CalendarScopes.CALENDAR)
)

// Solicitar conta
val accountName = credential.selectedAccountName
if (accountName == null) {
    // Mostrar seletor de conta
    startActivityForResult(
        credential.newChooseAccountIntent(),
        REQUEST_ACCOUNT_PICKER
    )
}
```

### 4. Build e Run

1. Sincronize o projeto no Android Studio
2. Execute `./gradlew build` ou use o Android Studio
3. Instale no dispositivo/emulador

## Estrutura de Dados

### Room Database

O app usa Room para cache local:
- **Tabela `emails`**: Armazena emails do Gmail
- **Tabela `events`**: Armazena eventos extraídos

### Sincronização

O `WorkManager` sincroniza emails a cada 15 minutos (quando há conexão).

Para agendar manualmente:

```kotlin
@Inject
lateinit var workManagerScheduler: WorkManagerScheduler

// No seu código
workManagerScheduler.scheduleEmailSync()
```

## Troubleshooting

### Erro: "API key not found"
- Verifique se a chave do Gemini está configurada corretamente
- Certifique-se de que `local.properties` está no `.gitignore`

### Erro: "Authentication required"
- Verifique se as APIs estão habilitadas no Google Cloud Console
- Confirme que o SHA-1 está correto
- Verifique se o package name está correto

### Erro: "Permission denied"
- Verifique se o usuário concedeu as permissões necessárias
- Confirme que o escopo OAuth está correto

## Próximos Passos

1. Implementar autenticação completa com Google Sign-In
2. Adicionar tratamento de erros mais robusto
3. Implementar testes unitários e de integração
4. Adicionar notificações para novos eventos
5. Melhorar a UI/UX

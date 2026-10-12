# Шаблон «Футбольный дайджест» — полный план итерации 0

Назначение: каркас выдаётся студентам **уже реализованным**, кроме одного файла —
`MatchDetailsDialog.kt` (маленькое задание итерации 0). Проект компилируется и запускается:
список матчей работает, при тапе студент дописывает диалог.

Стек: Kotlin, Compose, Retrofit + kotlinx-serialization, Coroutines, Koin, ViewModel, Coil (логотипы),
Navigation 3, Chucker (инспектор трафика, только debug).
minSdk 26 (java.time доступен без десугаринга). Версии зависимостей — актуальные стабильные.

## Готовые части (не меняются)

| Файл | Содержимое |
|---|---|
| `build.gradle.kts` | зависимости стека + Coil + Navigation 3 + Chucker (debug); плагин kotlinx-serialization |
| `AndroidManifest.xml` | `INTERNET`, регистрация Application |
| `FootballDigestApp.kt` | `startKoin { modules(networkModule, repositoryModule, viewModelModule) }` |
| `MainActivity.kt` | `setContent { AppTheme { MatchesNavRoot() } }` |
| `ui/theme/` | Material-тема |

## Дерево пакетов (`java/ru/urfu/footballdigest/`)

```
di/
  NetworkModule.kt         [готово]
  RepositoryModule.kt      [готово]
  ViewModelModule.kt       [готово]
data/
  api/
    SStatsApi.kt           [готово] Retrofit-контракт
    dto/Envelope.kt        [готово] два конверта (см. ловушку ниже)
    dto/GameDto.kt         [готово] DTO матча и команд
  mapper/GameMapper.kt     [готово] DTO -> домен (+правила маппинга)
  repository/
    MatchRepositoryImpl.kt [готово]
domain/
  model/Match.kt, Team.kt, MatchStatus.kt   [готово]
  repository/MatchRepository.kt             [готово] интерфейс
  usecase/GetMatchesUseCase.kt, GetMatchUseCase.kt [готово]
presentation/
  navigation/
    Navigation.kt           [готово] маршруты, Navigator, NavDisplay; диалог деталей — узел навигации
  matches/
    MatchesScreen.kt        [готово]
    MatchesViewModel.kt     [готово]
    MatchListItem.kt        [готово] элемент списка (лого, команды, счёт/время)
  details/
    MatchDetailsDialog.kt   [ЗАДАНИЕ СТУДЕНТАМ] TODO-заглушка контента диалога
  ui/common/ErrorState.kt, LoadingState.kt   [готово]
```

## План реализации по классам

### domain/model

```kotlin
data class Team(val id: Long, val name: String, val logoUrl: String?)

enum class MatchStatus { SCHEDULED, FINISHED }

data class Match(
    val id: Long,
    val home: Team, val away: Team,
    val homeScore: Int?, val awayScore: Int?,   // null — матч не начат
    val status: MatchStatus,
    val kickoff: LocalDateTime,
    val league: String,
    val round: String,                          // roundName, для диалога деталей
)
```

### domain/repository + usecase

```kotlin
interface MatchRepository {
    suspend fun matchesOn(date: LocalDate): List<Match>
    suspend fun match(id: Long): Match
}

class GetMatchesUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(date: LocalDate): List<Match> = repository.matchesOn(date)
}
class GetMatchUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(id: Long): Match = repository.match(id)
}
```

### data/api/dto — конверты и DTO

**Ловушка API:** `/Games/list` возвращает конверт с `data: List<T>`, а `/Games/{id}` — конверт
с одиночным объектом `data: T`. Поэтому два конверта, иначе kotlinx-serialization уронит парсинг деталей.

```kotlin
@Serializable data class ListEnvelope<T>(val status: String, val data: List<T> = emptyList())
@Serializable data class SingleEnvelope<T>(val status: String, val data: T? = null)
```

`GameDto` (@Serializable, все поля camelCase — совпадают с JSON, `Json { ignoreUnknownKeys = true }`):
`id: Long`, `dateUtc: Long` (unix-секунды), `statusName: String`, `homeResult/awayResult: Int?`,
`homeTeam/awayTeam: TeamDto(id: Long, name: String)`, `season: SeasonDto(league: LeagueDto(name: String))`,
`roundName: String`.

### data/api — контракт

```kotlin
interface SStatsApi {
    @GET("Games/list?offset=0&limit=10")
    suspend fun gamesOn(@Query("Date") date: String): ListEnvelope<GameDto>   // "yyyy-MM-dd"; offset/limit захардкожены
    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>
}
```

### data/mapper — правила маппинга

- `Team(id, name, logoUrl = "https://sstats.net/assets/logos/$id.png")` — паттерн проверен (200, image/png).
- `status = if (statusName == "Finished") FINISHED else SCHEDULED` (промежуточные строки вроде "Second Half" считаем несыгранным; счёт live-матча при этом показываем).
- `kickoff = Instant.ofEpochSecond(dateUtc).atZone(ZoneId.systemDefault()).toLocalDateTime()`.
- `homeScore/awayScore = homeResult/awayResult` (null сохраняется).

### data/repository

`MatchRepositoryImpl(private val api: SStatsApi) : MatchRepository`:
- `matchesOn(date)` = `api.gamesOn(date.toString()).data.map { it.toMatch() }`;
- `match(id)` = `api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id")`.

### presentation/MatchesViewModel

```kotlin
sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data class Content(val date: LocalDate, val matches: List<Match>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}
```

- приватные поля: `_state: MutableStateFlow<MatchesUiState>`;
- публичные: `state: StateFlow<MatchesUiState>`;
- `init` загружает `LocalDate.today()`;
- `nextDay()` / `previousDay()` сдвигают дату и перезагружают (переводят в Loading);
- загрузка: `viewModelScope.launch { runCatching { getMatches(date) } … }`.

### presentation/MatchesScreen

- заголовок: `IconButton(←)`, текст даты, `IconButton(→)`;
- состояние: Loading — кружок, Error — `ErrorState`, Content — `LazyColumn`;
- элемент — `MatchListItem(match, onClick = onMatchClick)`:
  `AsyncImage(logoUrl, 48.dp)` + названия команд + счёт (только у сыгранных; время и лига
  в списке НЕ показываются — они будут в деталях); логотипы обоих команд и плейсхолдер
  при ошибке загрузки;
- тап по элементу: `onMatchClick(match)` — колбэк приходит от навигации, навигатор
  открывает узел `MatchDetails` (диалог деталей не живёт в состоянии экрана).

### presentation/details — ЗАДАНИЕ СТУДЕНТАМ (единственный TODO шаблона)

Диалог деталей — отдельный узел навигации, а не состояние ViewModel: в
`presentation/navigation/Navigation.kt` он зарегистрирован как
`entry<MatchDetails>(metadata = DialogSceneStrategy.dialog())`; androidx.navigation3 сам
показывает содержимое в диалоговом окне и закрывает по тапу вне (это `goBack` навигатора).

```kotlin
@Composable
fun MatchDetailsDialog(match: Match, onDismiss: () -> Unit) {
    TODO("Итерация 0: контент диалога деталей матча")
}
```

Ожидаемая реализация — карточка (`Card`/`Surface`) с данными списка (логотипы, команды,
счёт у сыгранных) плюс минимум три детали, которых в списке нет: время старта (`kickoff`,
формат `"HH:mm"`), лига, тур (`match.round`). Оборачивать в
`androidx.compose.ui.window.Dialog` не нужно — диалоговое окно даёт навигация.
Кнопка закрытия вызывает `onDismiss`.

### di

- `NetworkModule`: `Json { ignoreUnknownKeys = true }`, OkHttp + `HttpLoggingInterceptor(Level.BODY)`
  (обязателен — им демонстрируется итерация 1) + `ChuckerInterceptor` (в debug; в release
  подставляется no-op артефакт), Retrofit (`https://api.sstats.net/`, конвертер kotlinx-serialization), `SStatsApi`.
- `RepositoryModule`: `single { MatchRepositoryImpl(get()) } bind MatchRepository::class`.
- `ViewModelModule`: use cases (`single { GetMatchesUseCase(get()) }`, `single { GetMatchUseCase(get()) }`),
  затем `viewModelOf(::MatchesViewModel)`.

## Конвенции

1. Зависимости — только через конструктор, собирает Koin.
2. DTO живут в `data/`, домен о них не знает; маппинг — в `data/mapper/`.
3. Новые файлы сверх дерева — разрешены, обосновываются на сдаче.
4. Навигация — androidx.navigation3 (`NavDisplay` + `entryProvider`); диалоговые узлы —
   через `DialogSceneStrategy.dialog()`, а не через состояние ViewModel.

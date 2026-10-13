package io.github.dekabrsky.androidMidSemester.presentation.teams

// ЗАДАНИЕ 4 (закомментировано): файл добавлен в задании 4 и сейчас отключён.
// Чтобы включить, раскомментируй код ниже.

// import androidx.lifecycle.ViewModel
// import androidx.lifecycle.viewModelScope
// import io.github.dekabrsky.androidMidSemester.domain.model.Team
// import io.github.dekabrsky.androidMidSemester.domain.usecase.ClearTeamSelectionUseCase
// import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
// import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
// import io.github.dekabrsky.androidMidSemester.domain.usecase.ToggleTeamSelectionUseCase
// import kotlinx.coroutines.CancellationException
// import kotlinx.coroutines.flow.MutableStateFlow
// import kotlinx.coroutines.flow.SharingStarted
// import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.combine
// import kotlinx.coroutines.flow.stateIn
// import kotlinx.coroutines.launch
//
// sealed interface TeamsUiState {
//     data object Loading : TeamsUiState
//
//     /**
//      * @param teams команды, подходящие под строку поиска
//      * @param selectedIds выбранные команды (по всему списку, а не только по найденным)
//      */
//     data class Content(
//         val teams: List<Team>,
//         val selectedIds: Set<Long>,
//         val query: String,
//     ) : TeamsUiState
//
//     data class Error(val message: String) : TeamsUiState
// }
//
// /** Результат загрузки списка команд, отдельно от выбора и поиска. */
// private sealed interface TeamsLoad {
//     data object Loading : TeamsLoad
//     data class Loaded(val teams: List<Team>) : TeamsLoad
//     data class Failed(val message: String) : TeamsLoad
// }
//
// class TeamsViewModel(
//     private val getTeams: GetTeamsUseCase,
//     observeSelectedTeams: ObserveSelectedTeamsUseCase,
//     private val toggleTeamSelection: ToggleTeamSelectionUseCase,
//     private val clearTeamSelection: ClearTeamSelectionUseCase,
// ) : ViewModel() {
//
//     private val load = MutableStateFlow<TeamsLoad>(TeamsLoad.Loading)
//     private val query = MutableStateFlow("")
//
//     val state: StateFlow<TeamsUiState> =
//         combine(load, observeSelectedTeams(), query) { load, selectedIds, query ->
//             when (load) {
//                 TeamsLoad.Loading -> TeamsUiState.Loading
//                 is TeamsLoad.Failed -> TeamsUiState.Error(load.message)
//                 is TeamsLoad.Loaded -> TeamsUiState.Content(
//                     teams = load.teams.filter { it.name.contains(query.trim(), ignoreCase = true) },
//                     selectedIds = selectedIds,
//                     query = query,
//                 )
//             }
//         }.stateIn(
//             scope = viewModelScope,
//             started = SharingStarted.WhileSubscribed(5_000),
//             initialValue = TeamsUiState.Loading,
//         )
//
//     init {
//         loadTeams()
//     }
//
//     fun onQueryChange(value: String) {
//         query.value = value
//     }
//
//     fun onTeamToggle(teamId: Long) {
//         toggleTeamSelection(teamId)
//     }
//
//     fun onClearSelection() {
//         clearTeamSelection()
//     }
//
//     fun retry() {
//         loadTeams()
//     }
//
//     private fun loadTeams() {
//         load.value = TeamsLoad.Loading
//         viewModelScope.launch {
//             load.value = try {
//                 TeamsLoad.Loaded(getTeams())
//             } catch (e: CancellationException) {
//                 throw e
//             } catch (e: Exception) {
//                 TeamsLoad.Failed(e.message ?: "Не удалось загрузить команды")
//             }
//         }
//     }
// }

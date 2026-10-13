package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data class Content(val date: LocalDate, val matches: List<Match>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    init {
        load(LocalDate.now())
    }

    fun previousDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.minusDays(1))
    }

    fun nextDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.plusDays(1))
    }

    private fun load(date: LocalDate) {
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches -> _state.value = MatchesUiState.Content(date, matches) }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }
}

// ===== ЗАДАНИЕ 4 (закомментировано) =====
// Выше рабочая версия после задания 3. Ниже версия этого файла из задания 4.
// Чтобы включить задание 4, замени код выше на раскомментированный код ниже.
//
// import androidx.lifecycle.ViewModel
// import androidx.lifecycle.viewModelScope
// import io.github.dekabrsky.androidMidSemester.domain.model.Match
// import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
// import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
// import kotlinx.coroutines.CancellationException
// import kotlinx.coroutines.flow.MutableStateFlow
// import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.asStateFlow
// import kotlinx.coroutines.flow.collectLatest
// import kotlinx.coroutines.flow.combine
// import kotlinx.coroutines.flow.update
// import kotlinx.coroutines.launch
// import java.time.LocalDate
//
// sealed interface MatchesUiState {
//     data object Loading : MatchesUiState
//
//     /** @param isFiltered true, если показаны матчи только выбранных команд */
//     data class Content(
//         val date: LocalDate,
//         val matches: List<Match>,
//         val isFiltered: Boolean,
//     ) : MatchesUiState
//
//     data class Error(val message: String) : MatchesUiState
// }
//
// class MatchesViewModel(
//     private val getMatches: GetMatchesUseCase,
//     private val observeSelectedTeams: ObserveSelectedTeamsUseCase,
// ) : ViewModel() {
//
//     private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
//     val state: StateFlow<MatchesUiState> = _state.asStateFlow()
//
//     private val date = MutableStateFlow(LocalDate.now())
//
//     init {
//         // Перезагружаем список при смене даты И при смене выбранных команд.
//         // collectLatest отменяет устаревшую загрузку, если пользователь быстро листает дни.
//         viewModelScope.launch {
//             combine(date, observeSelectedTeams()) { date, teamIds -> date to teamIds }
//                 .collectLatest { (date, teamIds) -> load(date, teamIds) }
//         }
//     }
//
//     fun previousDay() {
//         date.update { it.minusDays(1) }
//     }
//
//     fun nextDay() {
//         date.update { it.plusDays(1) }
//     }
//
//     private suspend fun load(date: LocalDate, teamIds: Set<Long>) {
//         _state.value = MatchesUiState.Loading
//         runCatching { getMatches(date, teamIds) }
//             .onSuccess { matches ->
//                 _state.value = MatchesUiState.Content(
//                     date = date,
//                     matches = matches,
//                     isFiltered = teamIds.isNotEmpty(),
//                 )
//             }
//             .onFailure { e ->
//                 if (e is CancellationException) throw e
//                 _state.value = MatchesUiState.Error(e.message ?: "Не удалось загрузить матчи")
//             }
//     }
// }

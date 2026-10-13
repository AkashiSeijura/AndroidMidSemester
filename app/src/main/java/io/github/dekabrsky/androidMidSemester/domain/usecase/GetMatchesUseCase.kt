package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class GetMatchesUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(date: LocalDate): List<Match> = repository.matchesOn(date)
}

// ===== ЗАДАНИЕ 4 (закомментировано) =====
// Выше рабочая версия после задания 3. Ниже версия этого файла из задания 4.
// Чтобы включить задание 4, замени код выше на раскомментированный код ниже.
//
// import io.github.dekabrsky.androidMidSemester.domain.model.Match
// import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
// import java.time.LocalDate
//
// class GetMatchesUseCase(private val repository: MatchRepository) {
//     suspend operator fun invoke(date: LocalDate, teamIds: Set<Long> = emptySet()): List<Match> =
//         repository.matchesOn(date, teamIds)
// }

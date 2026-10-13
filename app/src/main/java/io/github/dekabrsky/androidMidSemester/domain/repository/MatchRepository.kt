package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

interface MatchRepository {
    suspend fun matchesOn(date: LocalDate): List<Match>
    suspend fun match(id: Long): Match
}

// ===== ЗАДАНИЕ 4 (закомментировано) =====
// Выше рабочая версия после задания 3. Ниже версия этого файла из задания 4.
// Чтобы включить задание 4, замени код выше на раскомментированный код ниже.
//
// import io.github.dekabrsky.androidMidSemester.domain.model.Match
// import java.time.LocalDate
//
// interface MatchRepository {
//     /** @param teamIds матчи только этих команд; пустое множество означает "все матчи дня". */
//     suspend fun matchesOn(date: LocalDate, teamIds: Set<Long> = emptySet()): List<Match>
//     suspend fun match(id: Long): Match
// }

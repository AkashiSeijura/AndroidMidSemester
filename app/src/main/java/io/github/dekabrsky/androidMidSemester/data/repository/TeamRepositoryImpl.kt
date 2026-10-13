package io.github.dekabrsky.androidMidSemester.data.repository

// ЗАДАНИЕ 4 (закомментировано): файл добавлен в задании 4 и сейчас отключён.
// Чтобы включить, раскомментируй код ниже.

// import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
// import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
// import io.github.dekabrsky.androidMidSemester.domain.model.Team
// import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
//
// class TeamRepositoryImpl(
//     private val api: SStatsApi,
// ) : TeamRepository {
//
//     /** Список команд страны почти не меняется, поэтому держим его в памяти до перезапуска. */
//     private val cache = mutableMapOf<String, List<Team>>()
//
//     override suspend fun teamsOf(country: String): List<Team> =
//         cache[country] ?: api.teams(country).data.map { it.toTeam() }
//             .also { cache[country] = it }
// }

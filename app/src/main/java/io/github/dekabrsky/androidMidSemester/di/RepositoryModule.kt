package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchCache() }
    single<MatchRepository> { EspnMatchRepositoryImpl(get(), get()) }
}

// ===== ЗАДАНИЕ 4 (закомментировано) =====
// Выше рабочая версия после задания 3. Ниже версия этого файла из задания 4.
// Чтобы включить задание 4, замени код выше на раскомментированный код ниже.
//
// import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
// import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
// import io.github.dekabrsky.androidMidSemester.data.repository.SelectedTeamsRepositoryImpl
// import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
// import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
// import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository
// import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
// import org.koin.android.ext.koin.androidContext
// import org.koin.dsl.module
//
// val repositoryModule = module {
//     single { MatchCache() }
//     // Бизнес вернулся на sstats: только там есть список команд страны и фильтр матчей по командам.
//     // EspnMatchRepositoryImpl остаётся в проекте, но в DI не подключён.
//     single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
//     single<TeamRepository> { TeamRepositoryImpl(get()) }
//     single<SelectedTeamsRepository> { SelectedTeamsRepositoryImpl(androidContext()) }
// }

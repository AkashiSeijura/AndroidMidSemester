package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository

class GetMatchUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(id: Long): Match = repository.match(id)
}

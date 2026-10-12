package io.github.dekabrsky.androidMidSemester.domain.model

import java.time.LocalDateTime

data class Match(
    val id: Long,
    val home: Team,
    val away: Team,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: MatchStatus,
    val kickoff: LocalDateTime,
    val league: String,
    val round: String,
)

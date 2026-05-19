package com.doomguard.ui.wireframe

enum class CoachFilterChip {
    All,
    Focus,
    Sleep,
    Habits,
}

data class CoachRecommendation(
    val id: String,
    val title: String,
    val body: String,
    val tags: Set<CoachFilterChip>,
)

object CoachLibrary {
    val all: List<CoachRecommendation> = listOf(
        CoachRecommendation(
            "1",
            "Take a digital break",
            "Set a 10-minute timer and leave your phone face-down. Short resets rebuild attention.",
            setOf(CoachFilterChip.All, CoachFilterChip.Focus, CoachFilterChip.Habits),
        ),
        CoachRecommendation(
            "2",
            "Focus booster",
            "Try one Pomodoro before opening social apps — 25 minutes of deep work first.",
            setOf(CoachFilterChip.All, CoachFilterChip.Focus),
        ),
        CoachRecommendation(
            "3",
            "Sleep-friendly scrolling",
            "Dim the screen and set a hard stop 45 minutes before bed.",
            setOf(CoachFilterChip.All, CoachFilterChip.Sleep),
        ),
        CoachRecommendation(
            "4",
            "Habit swap",
            "Replace one evening scroll with a short walk or chapter of reading.",
            setOf(CoachFilterChip.All, CoachFilterChip.Habits, CoachFilterChip.Sleep),
        ),
        CoachRecommendation(
            "5",
            "Notification hygiene",
            "Mute non-human alerts for social apps during work blocks.",
            setOf(CoachFilterChip.All, CoachFilterChip.Focus, CoachFilterChip.Habits),
        ),
    )
}

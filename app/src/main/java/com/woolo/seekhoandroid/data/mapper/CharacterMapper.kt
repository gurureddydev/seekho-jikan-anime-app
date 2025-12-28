package com.woolo.seekhoandroid.data.mapper

import com.woolo.seekhoandroid.data.remote.dto.CharacterDataDto
import com.woolo.seekhoandroid.data.remote.dto.CharactersResponse
import com.woolo.seekhoandroid.domain.model.Character
import com.woolo.seekhoandroid.domain.model.VoiceActor

fun CharactersResponse.toCharacters(): List<Character> {
    return data.map { it.toCharacter() }
}

fun CharacterDataDto.toCharacter(): Character {
    return Character(
        malId = character.malId,
        name = character.name ?: "Unknown",
        imageUrl = character.images?.jpg?.imageUrl ?: character.images?.webp?.imageUrl,
        role = role,
        voiceActors = voiceActors?.mapNotNull { it.toVoiceActor() } ?: emptyList()
    )
}

fun com.woolo.seekhoandroid.data.remote.dto.VoiceActorDto.toVoiceActor(): VoiceActor? {
    val person = person ?: return null
    return VoiceActor(
        malId = person.malId,
        name = person.name ?: "Unknown",
        imageUrl = person.images?.jpg?.imageUrl,
        language = language
    )
}


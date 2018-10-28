package com.dev.cameronc.movies.model.actor

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ActorDetails(@Id var id: Long,
                        val tmdbId: Long,
                        val biography: String,
                        val birthday: String?,
                        val deathDay: String?,
                        val homepage: String?,
                        val knownFor: String,
                        val name: String,
                        val placeOfBirth: String?,
                        val profilePhotoPath: String?)

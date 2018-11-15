package com.dev.cameronc.movies.model.actor

import com.dev.cameronc.moviedb.data.actor.Role

data class ActorScreenModel(val name: String,
                            val birthday: String?,
                            val deathDay: String?,
                            val age: String?,
                            val placeOfBirth: String?,
                            val biography: String,
                            val profileImagePath: String?,
                            val actorRoles: List<Role>)
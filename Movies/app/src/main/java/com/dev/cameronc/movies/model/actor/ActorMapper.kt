package com.dev.cameronc.movies.model.actor

import com.dev.cameronc.moviedb.data.actor.ActorDetails
import javax.inject.Inject

class ActorMapper @Inject constructor() {

    fun mapToAppActor(actorDetails: ActorDetails): com.dev.cameronc.movies.model.actor.ActorDetails {
        return com.dev.cameronc.movies.model.actor.ActorDetails(0, actorDetails.id,
                actorDetails.biography, actorDetails.birthday, actorDetails.deathday,
                actorDetails.homepage, actorDetails.knownForDepartment, actorDetails.name,
                actorDetails.placeOfBirth, actorDetails.profilePath)
    }
}
package com.dev.cameronc.moviedb.data.movie.detail.video

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

class SiteTransformer : JsonSerializer<Site>, JsonDeserializer<Site> {
    companion object {
        private val SiteEnums = EnumSet.allOf(Site::class.java)
    }

    override fun serialize(src: Site?, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement =
            JsonPrimitive(src?.siteName)

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Site =
            transformSiteNameToSite(json?.asString.orEmpty()) ?: Site.YouTube

    private fun transformSiteNameToSite(siteName: String): Site? = SiteEnums.firstOrNull { it.siteName == siteName }

}
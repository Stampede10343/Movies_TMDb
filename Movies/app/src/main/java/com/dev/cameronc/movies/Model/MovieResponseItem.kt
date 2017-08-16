package com.dev.cameronc.movies.Model

import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


/*@Entity*/ class MovieResponseItem(@SerializedName("poster_path") var posterPath: String, @SerializedName("adult") var adult: Boolean,
                                    @SerializedName("overview") var overview: String, @SerializedName("release_date") var releaseDate: String,
                                    @PrimaryKey @SerializedName("id") var id: Int, @SerializedName("original_title") var originalTitle: String,
                                    @SerializedName("original_language") var originalLanguage: String, @SerializedName("title") var title: String,
                                    @SerializedName("backdrop_path") var backdropPath: String, @SerializedName("popularity") var popularity: Float,
                                    @SerializedName("vote_count") var voteCount: Int, @SerializedName("video") var video: Boolean,
                                    @SerializedName("vote_average") var voteAverage: Float,
                                    @SerializedName("genre_ids") var genreIds: ArrayList<Int>, var genreTags: ArrayList<String>? = null) : Parcelable
{
    constructor(parcel: Parcel) : this(posterPath = parcel.readString(), adult = parcel.readInt() == 1, overview = parcel.readString(),
            releaseDate = parcel.readString(), id = parcel.readInt(), originalTitle = parcel.readString(), originalLanguage = parcel.readString(),
            title = parcel.readString(), backdropPath = parcel.readString(), popularity = parcel.readFloat(), voteCount = parcel.readInt(),
            video = parcel.readInt() == 1, voteAverage = parcel.readFloat(), genreIds = parcel.readArrayList(Int::class.java.classLoader) as ArrayList<Int>,
            genreTags = parcel.readArrayList(ClassLoader.getSystemClassLoader()) as ArrayList<String>?)

    override fun writeToParcel(parcel: Parcel, flag: Int)
    {
        parcel.writeString(posterPath)
        parcel.writeInt(if (adult) 1 else 0)
        parcel.writeString(overview)
        parcel.writeString(releaseDate)
        parcel.writeInt(id)
        parcel.writeString(originalTitle)
        parcel.writeString(originalLanguage)
        parcel.writeString(title)
        parcel.writeString(backdropPath)
        parcel.writeFloat(popularity)
        parcel.writeInt(voteCount)
        parcel.writeInt(if (video) 1 else 0)
        parcel.writeFloat(voteAverage)
        parcel.writeList(genreIds)
        parcel.writeList(genreTags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MovieResponseItem>
    {
        override fun createFromParcel(parcel: Parcel): MovieResponseItem
        {
            return MovieResponseItem(parcel)
        }

        override fun newArray(size: Int): Array<MovieResponseItem?>
        {
            return arrayOfNulls(size)
        }
    }

    fun addGenre(genre: String)
    {
        if (genreTags == null)
        {
            genreTags = ArrayList()
        }

        genreTags?.add(genre)
    }
}

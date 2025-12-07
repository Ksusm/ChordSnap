package cz.mendelu.pef.chordsnap.communication

import cz.mendelu.pef.chordsnap.models.Chord
import cz.mendelu.pef.chordsnap.models.ChordType
import cz.mendelu.pef.chordsnap.models.Note
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ChordsAPI {

    @Headers("Content-Type: application/json")
    @GET("{owner}/chords_api/refs/heads/main/src/databases/json/chords.json")
    suspend fun getAllChords(
        @Path("owner") owner: String
    ): Response<List<Chord>>

    @Headers("Content-Type: application/json")
    @GET("{owner}/chords_api/refs/heads/main/src/databases/json/chord_types.json")
    suspend fun getChordTypes(
        @Path("owner") owner: String
    ): Response<List<ChordType>>

    @Headers("Content-Type: application/json")
    @GET("{owner}/chords_api/refs/heads/main/src/databases/json/notes.json")
    suspend fun getAllNotes(
        @Path("owner") owner: String
    ): Response<List<Note>>
}
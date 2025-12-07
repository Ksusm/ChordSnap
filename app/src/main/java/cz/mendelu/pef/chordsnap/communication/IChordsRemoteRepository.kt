package cz.mendelu.pef.chordsnap.communication

import cz.mendelu.pef.chordsnap.models.Chord
import cz.mendelu.pef.chordsnap.models.ChordType
import cz.mendelu.pef.chordsnap.models.Note

interface IChordsRemoteRepository : IBaseRemoteRepository {

    suspend fun getAllChords(): CommunicationResult<List<Chord>>
    suspend fun getChordTypes(): CommunicationResult<List<ChordType>>
    suspend fun getAllNotes(): CommunicationResult<List<Note>>
}
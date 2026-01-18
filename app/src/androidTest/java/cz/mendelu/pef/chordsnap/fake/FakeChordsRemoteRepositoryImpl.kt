package cz.mendelu.pef.chordsnap.fake

import cz.mendelu.pef.chordsnap.communication.CommunicationResult
import cz.mendelu.pef.chordsnap.communication.IChordsRemoteRepository
import cz.mendelu.pef.chordsnap.models.Chord
import cz.mendelu.pef.chordsnap.models.ChordType
import cz.mendelu.pef.chordsnap.models.Note
import javax.inject.Inject

class FakeChordsRemoteRepositoryImpl @Inject constructor() : IChordsRemoteRepository {

    var chords: List<Chord> = emptyList()
    var chordTypes: List<ChordType> = emptyList()
    var notes: List<Note> = emptyList()

    override suspend fun getAllChords(): CommunicationResult<List<Chord>> {
        return CommunicationResult.Success(chords)
    }

    override suspend fun getChordTypes(): CommunicationResult<List<ChordType>> {
        return CommunicationResult.Success(chordTypes)
    }

    override suspend fun getAllNotes(): CommunicationResult<List<Note>> {
        return CommunicationResult.Success(notes)
    }
}
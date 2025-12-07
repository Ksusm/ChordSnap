package cz.mendelu.pef.chordsnap.communication

import cz.mendelu.pef.chordsnap.models.Chord
import cz.mendelu.pef.chordsnap.models.ChordType
import cz.mendelu.pef.chordsnap.models.Note
import cz.mendelu.pef.chordsnap.utils.Constants
import javax.inject.Inject

class ChordsRemoteRepositoryImpl @Inject constructor(
    private val api: ChordsAPI
) : IChordsRemoteRepository {

    override suspend fun getAllChords(): CommunicationResult<List<Chord>> {
        return processResponse {
            api.getAllChords(Constants.GITHUB_OWNER)
        }
    }

    override suspend fun getChordTypes(): CommunicationResult<List<ChordType>> {
        return processResponse {
            api.getChordTypes(Constants.GITHUB_OWNER)
        }
    }

    override suspend fun getAllNotes(): CommunicationResult<List<Note>> {
        return processResponse {
            api.getAllNotes(Constants.GITHUB_OWNER)
        }
    }
}
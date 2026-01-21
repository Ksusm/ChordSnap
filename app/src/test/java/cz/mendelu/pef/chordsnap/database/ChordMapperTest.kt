package cz.mendelu.pef.chordsnap.database

import cz.mendelu.pef.chordsnap.models.Chord
import cz.mendelu.pef.chordsnap.models.ChordImages
import cz.mendelu.pef.chordsnap.models.ChordName
import org.junit.Test
import kotlin.test.assertEquals

class ChordMapperTest {

    @Test
    fun `toEntity converts Chord to ChordEntity correctly`() {
        val chord = Chord(
            id = "1",
            noteId = "C",
            typeId = "major",
            notes = listOf("C", "E", "G"),
            name = ChordName("C major", "Do mayor"),
            images = ChordImages("url1")
        )

        val entity = chord.toEntity()

        assertEquals("1", entity.id)
        assertEquals("C major", entity.nameEng)
        assertEquals("Do mayor", entity.nameSpa)
        assertEquals("C", entity.noteId)
        assertEquals("major", entity.typeId)
        assertEquals("url1", entity.imageUrl)
    }

    @Test
    fun `toEntity handles sharp notation correctly`() {
        val chord = Chord(
            id = "2",
            noteId = "C#",
            typeId = "minor",
            notes = listOf("C#", "E", "G#"),
            name = ChordName("C# minor", "Do# menor"),
            images = ChordImages("url2")
        )

        val entity = chord.toEntity()

        assertEquals("2", entity.id)
        assertEquals("C#", entity.noteId)
        assertEquals("C# minor", entity.nameEng)
    }

    @Test
    fun `toEntity handles flat notation correctly`() {
        val chord = Chord(
            id = "3",
            noteId = "Db",
            typeId = "major",
            notes = listOf("Db", "F", "Ab"),
            name = ChordName("Db major", "Reb mayor"),
            images = ChordImages("url3")
        )

        val entity = chord.toEntity()

        assertEquals("3", entity.id)
        assertEquals("Db", entity.noteId)
        assertEquals("Db major", entity.nameEng)
    }

    @Test
    fun `toEntityList converts empty list correctly`() {
        val chords = emptyList<Chord>()

        val entities = chords.toEntityList()

        assertEquals(0, entities.size)
    }

    @Test
    fun `toEntity handles diminished chord correctly`() {
        val chord = Chord(
            id = "4",
            noteId = "B",
            typeId = "diminished",
            notes = listOf("B", "D", "F"),
            name = ChordName("B diminished", "Si disminuido"),
            images = ChordImages("url4")
        )

        val entity = chord.toEntity()

        assertEquals("diminished", entity.typeId)
        assertEquals("B diminished", entity.nameEng)
    }

    @Test
    fun `toEntity handles seventh chord correctly`() {
        val chord = Chord(
            id = "5",
            noteId = "G",
            typeId = "7",
            notes = listOf("G", "B", "D", "F"),
            name = ChordName("G7", "Sol7"),
            images = ChordImages("url5")
        )

        val entity = chord.toEntity()

        assertEquals("7", entity.typeId)
        assertEquals("G7", entity.nameEng)
        assertEquals("Sol7", entity.nameSpa)
    }
}
package cz.mendelu.pef.chordsnap.database

import cz.mendelu.pef.chordsnap.models.Chord

fun Chord.toEntity(): ChordEntity {
    return ChordEntity(
        id = this.id,
        nameEng = this.name.eng,
        nameSpa = this.name.spa,
        noteId = this.noteId,
        typeId = this.typeId,
        imageUrl = this.images.pos1
    )
}

fun List<Chord>.toEntityList(): List<ChordEntity> {
    return this.map { it.toEntity() }
}
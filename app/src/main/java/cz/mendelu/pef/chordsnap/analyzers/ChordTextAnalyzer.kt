package cz.mendelu.pef.chordsnap.analyzers

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ChordTextAnalyzer(
    private val onChordDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val validNotes = setOf("A", "B", "C", "D", "E", "F", "G")

    @Volatile
    private var shouldScan = false

    fun triggerScan() {
        shouldScan = true
    }

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        if (!shouldScan) {
            imageProxy.close()
            return
        }

        shouldScan = false

        imageProxy.image?.let { mediaImage ->
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val allText = visionText.text

                    val foundChord = findChordInText(allText)
                    if (foundChord != null) {
                        onChordDetected(foundChord)
                    }

                    imageProxy.close()
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
        } ?: imageProxy.close()
    }

    private fun findChordInText(text: String): String? {
        val words = text.split(Regex("\\s+"))

        // Check for two-word chords first (e.g., "G minor", "C major")
        for (i in 0 until words.size - 1) {
            val firstWord = words[i]
            val secondWord = words[i + 1]

            if (firstWord.length in 1..2 &&
                validNotes.contains(firstWord.first().uppercaseChar().toString())) {

                val combinedChord = tryTwoWordChord(firstWord, secondWord)
                if (combinedChord != null) {
                    return combinedChord
                }
            }
        }

        // Check single-word chords
        for (word in words) {
            val chord = extractChordFromWord(word)
            if (chord != null) {
                return chord
            }
        }

        return null
    }

    private fun tryTwoWordChord(firstWord: String, secondWord: String): String? {
        val firstChar = firstWord.first().uppercaseChar().toString()
        if (!validNotes.contains(firstChar)) return null

        val lowerSecond = secondWord.lowercase()

        if (lowerSecond == "major" || lowerSecond == "minor" || lowerSecond == "diminished") {
            return "$firstWord $lowerSecond"
        }

        return null
    }

    private fun extractChordFromWord(word: String): String? {
        if (word.length < 1 || word.length > 10) return null

        val cleanWord = word.trim()
        if (cleanWord.isEmpty()) return null

        val firstChar = cleanWord.first().uppercaseChar().toString()
        if (!validNotes.contains(firstChar)) return null

        // Pattern 1: Single note (C, D, E, F, G, A, B)
        if (cleanWord.length == 1) {
            return cleanWord.uppercase()
        }

        // Pattern 2: Two characters
        if (cleanWord.length == 2) {
            val secondChar = cleanWord[1]

            // C#, Db, F#
            if (secondChar == 'b' || secondChar == '#') {
                return firstChar + secondChar
            }

            // Cm, Am, Gm
            if (secondChar.lowercaseChar() == 'm') {
                return firstChar + " minor"
            }

            // CM, AM, GM
            if (secondChar.uppercaseChar() == 'M') {
                return firstChar + " major"
            }

            if (secondChar.isDigit() && secondChar == '7') {
                return cleanWord.uppercase()
            }
        }

        // Pattern 3: Three characters
        if (cleanWord.length == 3) {
            val secondChar = cleanWord[1]
            val thirdChar = cleanWord[2]

            // C#m, Dbm, F#M
            if ((secondChar == 'b' || secondChar == '#')) {
                if (thirdChar.lowercaseChar() == 'm') {
                    return firstChar + secondChar + " minor"
                }
                if (thirdChar.uppercaseChar() == 'M') {
                    return firstChar + secondChar + " major"
                }
                if (thirdChar.isDigit() && thirdChar == '7') {
                    return firstChar + secondChar + thirdChar
                }
            }
        }

        val lowerWord = cleanWord.lowercase()
        if (lowerWord.endsWith("major") && cleanWord.length <= 7) {
            val notesPart = cleanWord.substring(0, cleanWord.length - 5).trim()
            if (notesPart.length <= 2 && validNotes.contains(notesPart.first().uppercaseChar().toString())) {
                return notesPart + " major"
            }
        }
        if (lowerWord.endsWith("minor") && cleanWord.length <= 7) {
            val notesPart = cleanWord.substring(0, cleanWord.length - 5).trim()
            if (notesPart.length <= 2 && validNotes.contains(notesPart.first().uppercaseChar().toString())) {
                return notesPart + " minor"
            }
        }
        if (lowerWord.endsWith("diminished") && cleanWord.length <= 13) {
            val notesPart = cleanWord.substring(0, cleanWord.length - 10).trim()
            if (notesPart.length <= 2 && validNotes.contains(notesPart.first().uppercaseChar().toString())) {
                return notesPart + " diminished"
            }
        }

        return null
    }
}
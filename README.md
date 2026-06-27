# ChordSnap

Android app for guitarists. Point your camera at a chord written on paper, 
and ChordSnap recognizes it, pulls the fingering diagram, and saves it to 
your library.

## What it does
- Scans chords from paper or books via camera (ML Kit OCR)
- Fetches fingering diagrams from Chords API
- Stores your chord history locally (Room database)
- Create practices — group chords into a sequence so you can run through 
  them in one place without jumping between individual chord details
- Shows nearby music shops and schools on a map

## Tech stack
Kotlin, Jetpack Compose, ML Kit, Room, DataStore, Chords API

## Setup
Create a `local.properties` file in the project root and add:
server=https://raw.githubusercontent.com/
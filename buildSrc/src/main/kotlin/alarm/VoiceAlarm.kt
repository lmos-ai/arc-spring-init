// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package arc.alarm

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

fun staticAlarm() {
    val mp3File = Thread.currentThread().contextClassLoader.getResourceAsStream("media/timer_completed.wav")
    val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(mp3File)
    val clip: Clip = AudioSystem.getClip()
    clip.open(audioInputStream)
    clip.start()
    audioInputStream.close()
}


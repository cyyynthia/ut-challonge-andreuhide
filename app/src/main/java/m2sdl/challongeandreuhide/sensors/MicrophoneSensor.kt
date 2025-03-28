package m2sdl.challongeandreuhide.sensors

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.abs

class MicrophoneSensor {
	private var sampleRate: Int = 44100
	private var channelConfig: Int = AudioFormat.CHANNEL_IN_MONO
	private var audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT

	private var minBufferSize: Int = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

	@SuppressLint("MissingPermission") // Must be granted
	private var microphone: AudioRecord =
		AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufferSize * 10)

	private var buffer: ShortArray = ShortArray(minBufferSize / 2)

	private var lastAmplitude = 0.0

	fun startRecording() {
		microphone.startRecording()
	}

	fun stopRecording() {
		microphone.stop()
		microphone.release()
	}

	fun capture() {
		val readSize = microphone.read(buffer, 0, buffer.size)

		var sum = 0.0
		for (i in 0 until readSize) {
			sum += abs(buffer[i].toFloat())
		}

		lastAmplitude = sum / readSize
	}

	fun getAmplitude(): Double {
		return lastAmplitude
	}

	/*
	fun FOUTU_POUR_FOUTU() {
		val bufferSize = AudioTrack.getMinBufferSize(
			SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
			AudioFormat.ENCODING_PCM_16BIT
		)
		if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
			bufferSize = SAMPLE_RATE * 2
		}

		val audioTrack = AudioTrack(
			AudioManager.STREAM_MUSIC,
			SAMPLE_RATE,
			AudioFormat.CHANNEL_OUT_MONO,
			AudioFormat.ENCODING_PCM_16BIT,
			bufferSize,
			AudioTrack.MODE_STREAM
		)

		audioTrack.play()

		Log.v(LOG_TAG, "Audio streaming started")

		val buffer = ShortArray(bufferSize)
		mSamples.rewind()
		val limit: Int = mNumSamples
		val totalWritten = 0
		while (mSamples.position() < limit && mShouldContinue) {
			val numSamplesLeft: Int = limit - mSamples.position()
			val samplesToWrite: Int
			if (numSamplesLeft >= buffer.size) {
				mSamples.get(buffer)
				samplesToWrite = buffer.size
			} else {
				for (i in numSamplesLeft..<buffer.size) {
					buffer[i] = 0
				}
				mSamples.get(buffer, 0, numSamplesLeft)
				samplesToWrite = numSamplesLeft
			}
			totalWritten += samplesToWrite
			audioTrack.write(buffer, 0, samplesToWrite)
		}

		if (!mShouldContinue) {
			audioTrack.release()
		}
	} */
}

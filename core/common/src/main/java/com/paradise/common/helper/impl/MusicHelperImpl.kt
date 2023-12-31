package com.paradise.common.helper.impl

import android.content.Context
import android.media.MediaPlayer
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.core.model.musicItem
import com.paradise.common.R
import com.paradise.common.helper.MusicHelper
import com.paradise.common.network.DEFAULT_MUSIC_DURATION
import com.paradise.common.network.defaultDispatcher
import com.paradise.common.network.getUriFromFilePath
import com.paradise.common.utils.getRandomElement
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicHelperImpl @Inject constructor(
    private val fragment: Fragment,
) : MusicHelper {

    private var mediaPlayer: MediaPlayer? = null
    private var job: Job? = null
    private val _isPrepared: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private lateinit var contextRef: Context
    private lateinit var lifecycleOwner: LifecycleOwner
    override fun initMusicHelper() {
        contextRef = fragment.requireContext()
        lifecycleOwner = fragment.viewLifecycleOwner
    }

    override val isPrepared: StateFlow<Boolean> get() = _isPrepared

    /**
     * Release media player
     *
     * 내부 변수 mediaPlayer를 초기화 해주거나, 중간에 음악을 멈추거나 할 때 사용하는 함수이다.
     */
    override fun releaseMediaPlayer() {
        this.mediaPlayer?.let {
            if (!(_isPrepared.value)) { // mediaPlayer가 null은 아닌데 prepared가 되어있지 않다는 것은 아직 prepareAsync()호출 후 비동기적으로 prepare이 동작 중이라는 것이다.
                it.release() // 비동기로 prepareAsync()가 동작 중일 땐 isPlaying이나 stop을 호출하면 에러가 발생한다. 그래서 release()만 호출
            } else {
                _isPrepared.value = false // 초기화
                if (it.isPlaying) { // prepare가 된 상태라는 건 음악이 실행되고 있다는 것이다.
                    it.stop()
                    it.release()
                }
            }
            it.setOnCompletionListener(null) // 리스너 해제
            it.setOnPreparedListener(null)
        }
        job?.cancel() // 음악이 duration 전에 끝날 경우 job을 cancle한다.
        job = null
    }

    /**
     * Start res music
     *
     * Res에 저장된 음악 리스트에서 음악을 랜덤으로 뽑아 MusicHelperImpl.Builder()에 저장한다.
     */
    override fun setStandardMusic() {
        startMusic(R.raw.settingsound)
    }

    /**
     * Start res music
     *
     * Res에 저장된 음악 리스트에서 음악을 랜덤으로 뽑아 MusicHelperImpl.Builder()에 저장한다.
     */
    override fun setResMusic() {
        val randomMusic = listOf<Int>(
            (R.raw.alert1), (R.raw.alert2), (R.raw.alert3)
        ).getRandomElement()
        if (randomMusic != null) startMusic(randomMusic)
    }

    override fun setMyMusic(musicList: List<musicItem>) {
        val randomMusic = musicList.getRandomElement()
        if (randomMusic != null) startMusic(randomMusic)
    }

    /**
     * Start music
     *
     * 음악을 실행하는 메서드
     * @param resId, raw package에 있는 음악 파일을 재생
     */
    override fun startMusic(resId: Int) {
        contextRef.let { context ->
            val rawDescriptor = context.resources.openRawResourceFd(resId)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    rawDescriptor.fileDescriptor, rawDescriptor.startOffset, rawDescriptor.length
                )
                setMusic(duration = DEFAULT_MUSIC_DURATION)
            }
            rawDescriptor.close()
        }
    }

    /**
     * Start music
     *
     * @param music, Room에 저장된 외부 저장소의 음악 파일 경로로 음악 재생
     */
    override fun startMusic(music: musicItem) {
        contextRef.let { context ->
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, getUriFromFilePath(context, music.newPath!!)!!)
                setMusic(music.startTime.toInt(), music.durationTime)
            }
        }
    }

    /**
     * Set music
     *
     * 주어진 음악 파일 정보로부터 시작 시간과 지속 시간을 설정한다.
     * @param startTime, 시작 시간(raw 파일 음악 : 기본 0 사용, Room 음악 : 커스텀 가능)
     * @param duration, (나중에 커스텀화)
     */
    private fun MediaPlayer.setMusic(
        startTime: Int = 0,
        duration: Long,
    ) = this.apply {
        // mediaPlayer가 null이 아닐 때만 job이 생김
        setListener(startTime)
        job = setDuration(duration) // 음악이 재생하는 도중에 정지할 경우 직접 cancle 해줘야하므로 객체를 받아둔다.
        if (job == null) releaseMediaPlayer()
    }

    /**
     * Set listener
     *
     * mediaPlayer에 리스너를 등록한다.
     * @param startTime
     */
    private fun MediaPlayer.setListener(startTime: Int) = this.apply {
        setOnPreparedListener {// 준비 완료 여부 리스너
            _isPrepared.value = true // 준비가 완료됨!!
            if (startTime > 0) seekTo(startTime) // 시작 시간이 있다면 그 시간으로 시작 시간을 초기화 한다.
            start()
        }
        setOnCompletionListener {// 음악 완료 여부 리스너
            releaseMediaPlayer()
        }
        prepareAsync() // 리스너 등록이 끝나면 prepareAsync()를 호출해 비동기로 음악을 준비한다.
    }

    /**
     * Set duration
     *
     * 음악의 종료 시점을 정한다.
     * @param duration
     */
    private fun setDuration(duration: Long) = lifecycleOwner.let {
        it.lifecycleScope.launch(defaultDispatcher) {
            delay(duration)
            releaseMediaPlayer()
        }
    }
}
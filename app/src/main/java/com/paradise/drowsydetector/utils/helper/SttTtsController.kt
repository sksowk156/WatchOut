package com.paradise.drowsydetector.utils.helper

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.paradise.drowsydetector.utils.CHECKUSESTTSERVICE
import com.paradise.drowsydetector.utils.SELECTGUIDESETTING
import com.paradise.drowsydetector.utils.SELECTMUSICSETTING
import com.paradise.drowsydetector.utils.SELECTSERVICE
import com.paradise.drowsydetector.utils.TTS_FINISHING
import com.paradise.drowsydetector.utils.TTS_SPEAKING
import java.lang.ref.WeakReference

class SttTtsController(
    private var contextRef: WeakReference<Context>,
    private var lifecycleOwner: WeakReference<LifecycleOwner>,
    private var sstServiceImpl: WeakReference<sstService>,
) {
    companion object {
        @Volatile
        private var instance: SttTtsController? = null
        fun getInstance(context: Context, lifecycleOwner: LifecycleOwner, sstService: sstService) =
            instance ?: synchronized(this) {
                // LocationSercie 객체를 생성할 때 같이 한번만 객체를 생성한다.
                instance ?: SttTtsController(
                    WeakReference(context), WeakReference(lifecycleOwner), WeakReference(sstService)
                ).also {
                    instance = it
                }
            }
    }

    interface sstService {
        fun baseMusic()
        fun userMusic()
        fun guideOff()
        fun guideOn()
        fun relaxData()
        fun recentRelaxData()
        fun cancleAnalyze()
    }

    private var ttsHelper: TtsHelper? = null
    private var sttHelper: SttHelper? = null
    fun releaseSttTtsController() {
        ttsHelper?.releaseTtsHelper()
        sttHelper?.releaseSttHelper()
    }

    fun clearContext() {
        releaseSttTtsController()
        ttsHelper?.clearContext()
        sttHelper?.clearContext()
        contextRef.clear()
        instance = null
    }

    fun initSttTtsController() {
        contextRef.get()?.let { context ->
            ttsHelper = TtsHelper.getInstance(context)
            ttsHelper?.initTTS()
            sttHelper = SttHelper.getInstance(context)
        }
        subscribeSttResult()
    }

    var result = MutableLiveData<String>()

    var ttsStateObserver: Observer<Int>? = null

    fun speakOutTtsHelper(word: String) {
        if (checkTtsSttHelperReady()) ttsHelper?.speakOut(word)
    }

    fun checkTtsSttHelperReady() =
        (ttsHelper?.isSpeaking?.value != TTS_SPEAKING && !(sttHelper?.sttState?.value)!!)

    fun resetSttHelper(lifecycleOwner: LifecycleOwner) {
        sttHelper?.sttResult?.removeObservers(lifecycleOwner) // observer 제거, 결과 받고 나서 제거
        sttHelper?.releaseSttHelper() // stt 종료
    }

    fun subscribeSttResult() {
        lifecycleOwner.get()?.let { lifecycleOwner ->
            result.observe(lifecycleOwner) {
                if (checkTtsSttHelperReady()) {
                    if (it.isNotEmpty()) {
                        resetSttHelper(lifecycleOwner)
                        Log.d("whatisthis", it)
                        when (it) {
                            CHECKUSESTTSERVICE -> {
                                checkUseSttService(
                                    lifecycleOwner,
                                    initObserver,
                                    "음성 인식 서비스를 사용하시겠습니까?? \n 1번 예, 2번 아니오"
                                )
                            }

                            SELECTSERVICE -> {
                                checkUseSttService(
                                    lifecycleOwner,
                                    initObserver1,
                                    "1번 음악 설정,\n 2번 휴식 공간 안내 설정,\n 3번 휴식 공간 정보 요청,\n 4번 휴식 공간 최근 정보 조회,\n 5번 앱 종료"
                                )
                            }

                            SELECTMUSICSETTING -> {
                                checkUseSttService(
                                    lifecycleOwner, initObserver2, "1번 기본 음악으로 설정,\n 2번 사용자 설정 음악"
                                )
                            }

                            SELECTGUIDESETTING -> {
                                checkUseSttService(
                                    lifecycleOwner, initObserver3, "1번 안내 받지 않음,\n 2번 안내 받음"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkUseSttService(
        lifecycleOwner: LifecycleOwner,
        initObserver: Observer<String>,
        speakOutWord: String,
    ) {
        sttHelper?.sttResult?.observe(lifecycleOwner, initObserver)

        ttsStateObserver = Observer<Int> {
            if ((ttsHelper?.isSpeaking?.value == TTS_FINISHING && !(sttHelper?.sttState?.value)!!)) { // TTS가 끝났을 때, STT가 시작하지 않았을 때
                sttHelper?.startSTT() // stt 시작
                ttsHelper?.stopTtsHelper() // tts 멈춤
                ttsHelper?.isSpeaking?.removeObserver(ttsStateObserver!!) // observer 제거, 바로 제거 해줘야 함-> 안 그러면 무한 루프
            }
        }
        ttsHelper?.isSpeaking?.observe(lifecycleOwner, ttsStateObserver!!)
        ttsHelper?.speakOut(speakOutWord)
    }

    val initObserver = Observer<String> {
        if (it.isNotEmpty()) { // STT 결과가 나왔을 때
            Log.d("whatisthis", "음성 인식 결과1 : ${it}")
            when (it) {
                "onError" -> {
                } // 에러 (말을 안한 것도 포함)
                "1번" -> {
                    result.postValue(SELECTSERVICE)
                } // 예
                "2번" -> {}// 아니오
                else -> {
                }
            }
        }
    }

    val initObserver1 = Observer<String> {
        if (it.isNotEmpty()) { // STT 결과가 나왔을 때
            Log.d("whatisthis", "음성 인식 결과2 : ${it}")
            when (it) {
                "onError" -> {
                }

                "1번" -> {
                    result.postValue(SELECTMUSICSETTING)
                } // 음악 설정
                "2번" -> {
                    result.postValue(SELECTGUIDESETTING)
                } // 휴식 공간 안내 설정
                "3번" -> {
                    requestRelaxData()
                } // 휴식 공간 정보 요청
                "4번" -> {
                    requestRecentRelaxData()
                } // 휴식 공간 최근 정보 조회
                "5번" -> {
                    requestCancleAnalyze()
                } // 앱 종료
                else -> {
                }
            }
        }
    }

    val initObserver2 = Observer<String> {
        if (it.isNotEmpty()) { // STT 결과가 나왔을 때
            Log.d("whatisthis", "음성 인식 결과3 : ${it}")
            when (it) {
                "onError" -> { // 에러 (말을 안한 것도 포함)
                }

                "1번" -> { // 기본 음악으로 설정
                    setBaseMusic()
                }

                "2번" -> { // 사용자 음악으로 설정
                    setUserMusic()
                }

                else -> {
                }
            }
        }
    }

    val initObserver3 = Observer<String> {
        if (it.isNotEmpty()) { // STT 결과가 나왔을 때
            Log.d("whatisthis", "음성 인식 결과 : ${it}")
            when (it) {
                "onError" -> { // 에러 (말을 안한 것도 포함)
                }

                "1번" -> { // 안내 받지 않음
                    setGuideOff()
                }

                "2번" -> { // 안내 받음
                    setGuideOn()
                }

                else -> {
                }
            }
        }
    }

    fun setBaseMusic() {
        sstServiceImpl.get()?.run {
            this.baseMusic()
        }
    }

    fun setUserMusic() {
        sstServiceImpl.get()?.run {
            this.userMusic()
        }
    }

    fun setGuideOff() {
        sstServiceImpl.get()?.run {
            this.guideOff()
        }
    }

    fun setGuideOn() {
        sstServiceImpl.get()?.run {
            this.guideOn()
        }
    }

    fun requestRelaxData() { // 휴식 공간 정보 요청
        sstServiceImpl.get()?.run {
            this.relaxData()
        }
    }

    fun requestRecentRelaxData() { // 휴식 공간 최근 정보 조회
        sstServiceImpl.get()?.run {
            this.recentRelaxData()
        }
    }

    fun requestCancleAnalyze() {  // 앱 종료
        sstServiceImpl.get()?.run {
            this.cancleAnalyze()
        }
    }
}
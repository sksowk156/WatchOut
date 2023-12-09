package com.paradise.common.helper.impl

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMeshPoint
import com.paradise.common.helper.CameraHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class CameraHelperImpl @Inject constructor(
    private val fragment: Fragment,
) : CameraHelper {

    private var contextRef: Context? = null
    private var lifecycleOwner: LifecycleOwner? = null
    override fun initCameraHelper() {
        contextRef = fragment.requireContext()
        lifecycleOwner = fragment.viewLifecycleOwner
    }


    private lateinit var cameraExecutor: ExecutorService

    private val faceDetectorOption by lazy {
        FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()
    }

    private val faceMeshOption by lazy {
        FaceMeshDetectorOptions.Builder().setUseCase(FaceMeshDetectorOptions.FACE_MESH).build()
    }

    private val faceDetector by lazy {
        FaceDetection.getClient(faceDetectorOption)
    }

    private val faceMesh by lazy {
        FaceMeshDetection.getClient(faceMeshOption)
    }


    override fun stopCameraHelper() {
        cameraExecutor.shutdown()
    }

    override fun releaseCameraHelper() {
        stopCameraHelper()
        faceDetector.close()
        faceMesh.close()
    }


    override fun startAnalyze(
        previewView: PreviewView,
        analyzedData: (Face, List<FaceMeshPoint>) -> Unit,
    ) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraController = LifecycleCameraController(contextRef!!)
        // 전면 카메라
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        cameraController.bindToLifecycle(lifecycleOwner!!)
        previewView.controller = cameraController.apply {
            setImageAnalysisAnalyzer(cameraExecutor, MlKitAnalyzer(
                listOf(faceDetector, faceMesh),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(contextRef!!)
            ) { result: MlKitAnalyzer.Result? ->
                // 화면 회전 시 쓰레드가 종료되는 도중에 binding이 null이 되기 때문에 binding이 먼저 null 되면 쓰레드 내부가 동작하지 못하게 막는다.(nullpointexception 방지)
                if (result != null) {
                    // 분석 결과(랜드마크)
                    val analyzeResult1 = result.getValue(faceDetector)
                    val analyzeResult2 = result.getValue(faceMesh)
                    // 아무것도 인식하지 못할 땐 return
                    if (analyzeResult1 == null || analyzeResult2 == null || analyzeResult1.size == 0 || analyzeResult2.size == 0) {
                        previewView.setOnTouchListener { _, _ -> false } //no-op
                        return@MlKitAnalyzer
                    }
                    val resultDetector = analyzeResult1[0]
                    val resultMesh = analyzeResult2[0].allPoints
                    analyzedData(resultDetector, resultMesh)
                }
            })
        }

    }

    private var cameraProvider: ProcessCameraProvider? = null

    @SuppressLint("UnsafeOptInUsageError")
    override fun startCamera2(
        previewView: PreviewView,
        analyzedData: (Face, List<FaceMeshPoint>) -> Unit,
    ) {
        contextRef?.let { context ->
            // 카메라 및 ML Kit 설정
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            // 카메라 미리보기 설정
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // 이미지 분석 설정
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

            // 이미지 분석기 설정
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image =
                        InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                    faceDetector.process(image).addOnSuccessListener { analyzeResult1 ->
                        // 얼굴이 감지되면 FaceMesh를 적용
                        if (analyzeResult1 == null || analyzeResult1.size == 0) return@addOnSuccessListener
                        faceMesh.process(image).addOnSuccessListener { analyzeResult2 ->
                            if (analyzeResult2 == null || analyzeResult2.size == 0) return@addOnSuccessListener
                            val resultDetector = analyzeResult1[0]
                            val resultMesh = analyzeResult2[0].allPoints
                            analyzedData(resultDetector, resultMesh)
                        }.addOnFailureListener {

                        }.addOnCompleteListener {
                            imageProxy.close()
                        }
                    }.addOnFailureListener { e ->
                        // 에러 처리
                    }.addOnCompleteListener {
                        // 이미지 프록시를 닫아야 다음 이미지를 분석할 수 있습니다.
                        imageProxy.close()
                    }
                }
            }

            lifecycleOwner.let { lifecycleOwner ->
                // 카메라 바인딩
                cameraProviderFuture.addListener({
                    cameraProvider = cameraProviderFuture.get()

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner!!, cameraSelector, preview, imageAnalysis
                    )
                }, ContextCompat.getMainExecutor(context))
            }
        }


    }
}
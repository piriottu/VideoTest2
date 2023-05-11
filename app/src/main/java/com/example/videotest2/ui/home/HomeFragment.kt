package com.example.videotest2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.videotest2.databinding.FragmentDetailBinding
import com.example.videotest2.databinding.FragmentHomeBinding
import com.example.videotest2.recordbutton.IRecorderActions
import com.example.videotest2.ui.HomeViewModel
import com.example.videotest2.recordbutton.RecorderStateManager
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.AudioCodec
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Grid
import com.otaliastudios.cameraview.controls.Hdr
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.controls.VideoCodec
import com.otaliastudios.cameraview.controls.WhiteBalance
import com.otaliastudios.cameraview.filter.Filters
import java.io.File
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    /**
     * Binding
     */
    private lateinit var binding: FragmentHomeBinding


    /**
     * ViewModel
     * */
    private val viewModelShared: HomeViewModel by activityViewModels()

    private val timeVideo: Long = 12000L
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.layoutPostVideoBtn.setTotalVideoDuration(timeVideo)
        binding.layoutPostVideoBtn.setListener(recorderActionListener)
        //Cam
        binding.camera.setLifecycleOwner(viewLifecycleOwner)

        binding.camera.apply {
            mode = Mode.VIDEO
            facing = Facing.FRONT
            videoCodec = VideoCodec.DEVICE_DEFAULT
            audioCodec = AudioCodec.DEVICE_DEFAULT
            whiteBalance = WhiteBalance.AUTO
            hdr = Hdr.OFF
            audio = Audio.ON
            audioBitRate = 0
            // Disable
            videoMaxSize = 0
            videoMaxDuration = 0
            videoBitRate = 0
            previewFrameRate = 0F

            grid = Grid.DRAW_4X4
            filter = Filters.BLACK_AND_WHITE.newInstance()
        }

        binding.camera.addCameraListener(Listener())

        setupObserver()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.camera.open()
    }

    override fun onPause() {
        super.onPause()
        binding.camera.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.camera.destroy()
    }

    private fun setupObserver() {
        viewModelShared.updateTimeLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { time ->
                binding.fragmentPostVideoTimeTv.text = time
            }
        }
    }

    //Animation button recorder
    private val recorderActionListener = object : IRecorderActions {
        override fun onStartRecord() {
            viewModelShared.updateRecordStatus(RecorderStateManager.RecorderState.RECORDING)
            //PLAY VIDEO
            captureVideo()
            //setupVisibilityBtn(false)
            isVisibleMinuteHeader(true)
        }

        override fun onPauseRecord() {
            //STOP VIDEO
            captureVideo()
            //setupVisibilityBtn(true)
            isVisibleMinuteHeader(true)
            viewModelShared.updateRecordStatus(RecorderStateManager.RecorderState.PAUSED)
        }

        override fun onResumeRecord() {
            //STOP VIDEO
            captureVideo()
            viewModelShared.updateRecordStatus(RecorderStateManager.RecorderState.RESUMED)
        }

        override fun onEndRecord() {
            // STOP VIDEO
            captureVideo()
            //setupVisibilityBtn(true)
            isVisibleMinuteHeader(false)
            viewModelShared.updateRecordStatus(RecorderStateManager.RecorderState.INIT)
        }

        override fun onCancelled() {
            // BACK
            captureVideo()
            //setupVisibilityBtn(true)
            isVisibleMinuteHeader(false)
            viewModelShared.updateRecordStatus(RecorderStateManager.RecorderState.INIT)
            //TODO findNavController().navigateUp()
        }

        override fun getCurrentRecorderState(): RecorderStateManager.RecorderState =
            viewModelShared.getRecorderState()

        override fun getTime(time: Long) {
            viewModelShared.getTime(
                seconds = getSecondsToMilliseconds(time),
                minutes = TimeUnit.MILLISECONDS.toMinutes(time)
            )
        }

        override fun onDurationTooShortError() {}
        override fun onSingleTap() {}
    }

    private fun captureVideo() {
        //Take video
        if (binding.camera.isTakingVideo) {
            binding.camera.stopVideo()
            Toast.makeText(
                requireContext(),
                "sei dentro captureVideo stopVideo ",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val file = File(requireActivity().filesDir, "video.mp4")
            binding.camera.takeVideoSnapshot(file)
        }

    }

    private fun isVisibleMinuteHeader(isVisible: Boolean) {
        binding.fragmentPostVideoTimeContainer.isVisible = isVisible
    }


    private inner class Listener : CameraListener() {

        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            // A Video was taken!
            viewModelShared.setFile(result.file)
            binding.camera.stopVideo()
            findNavController().navigate(HomeFragmentDirections.toDetailFragment())
            Toast.makeText(requireContext(), "sei dentro onVideoTaken", Toast.LENGTH_LONG).show()
        }

        override fun onCameraClosed() {
            super.onCameraClosed()
            Toast.makeText(requireContext(), "sei dentro onCameraClosed", Toast.LENGTH_LONG).show()
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            Toast.makeText(requireContext(), "sei dentro onVideoRecordingEnd", Toast.LENGTH_LONG)
                .show()
        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            Toast.makeText(requireContext(), "sei dentro onVideoRecordingStart", Toast.LENGTH_LONG)
                .show()
        }
    }

    //Convert milliseconds to 0:00 and 0:59
    private fun getSecondsToMilliseconds(milliseconds: Long): String {
        val seconds =
            when (val toSecond = TimeUnit.MILLISECONDS.toSeconds(milliseconds)) {
                in 60..119 -> {
                    addZero(toSecond - 60)
                }

                in 119..179 -> {
                    addZero(toSecond - 120)
                }

                in 179..239 -> {
                    addZero(toSecond - 180)
                }

                in 239..299 -> {
                    addZero(toSecond - 240)
                }

                else -> {
                    addZero(toSecond)
                }
            }
        return seconds
    }

    private fun addZero(milliseconds: Long): String {
        return if (milliseconds <= 9) {
            "0$milliseconds"
        } else {
            "$milliseconds"
        }
    }
}
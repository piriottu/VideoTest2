package com.example.videotest2.ui.home

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.videotest2.R
import com.example.videotest2.databinding.FragmentDetailBinding
import com.example.videotest2.ui.HomeViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Util


class DetailFragment : Fragment() {

    /**
     * Binding
     */
    private lateinit var binding: FragmentDetailBinding

    /**
     * ViewModel
     * */
    private val viewModelShared: HomeViewModel by activityViewModels()

    /**
     * ExoPlayer
     * */
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        initializePlayer(viewModelShared.getFile().absolutePath.toUri())
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer(uriFile: Uri) {

        val trackSelector = DefaultTrackSelector(requireContext()).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                //Load video
                val mediaItem = MediaItem.fromUri(uriFile)
                exoPlayer.setMediaItem(mediaItem)

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }
}
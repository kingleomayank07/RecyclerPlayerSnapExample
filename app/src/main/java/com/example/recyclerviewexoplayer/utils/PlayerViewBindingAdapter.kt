package com.example.recyclerviewexoplayer.utils

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

//region context function
fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
//endregion

//region const values for buffer strategy
private const val MIN_BUFFER = 65536
private const val MAX_BUFFER = 131072
private const val BUFFER_PLAYBACK = 8064
private const val BUFFER_PLAYBACK_RE_BUFFER = 8064
private const val BACK_BUFFER_PLAYBACK = 80064
val hide = MutableLiveData<Boolean>()
//endregion

class PlayerViewAdapter {

    companion object {

        //region companion object variables
        private var playersMap: MutableMap<Int, SimpleExoPlayer> = mutableMapOf()
        private var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null


        fun releaseAllPlayers() {
            playersMap.map {
                it.value.release()
            }
        }

        fun pauseAllPlayers() {
            playersMap.map {
                it.value.pause()
            }
        }

        fun playAllPlayers() {
            playersMap.map {
                it.value.play()
            }
        }
        //endregion

        //region release_recycle_exoPlayers
        fun releaseRecycledPlayers(index: Int) {
            playersMap[index]?.stop()
            playersMap[index]?.release()
//            playersMap.remove(index)
        }
        //endregion

        //region pauseCurrentPlayingVideo
        private fun pauseCurrentPlayingVideo() {
            if (currentPlayingVideo != null) {
                currentPlayingVideo?.second?.playWhenReady = false
            }
        }
        //endregion

        //region playIndexThenPausePreviousPlayer
        fun playIndexThenPausePreviousPlayer(index: Int) {
            if (playersMap[index]?.playWhenReady == false) {
                pauseCurrentPlayingVideo()
                playersMap[index]?.playWhenReady = true
                currentPlayingVideo = Pair(index, playersMap[index]!!)

            }
        }
        //endregion

        @JvmStatic
        @BindingAdapter(
            value = ["video_url", "on_state_change", "progressbar", "thumbnail", "item_index"],
            requireAll = false
        )
        fun PlayerView.loadVideo(
            url: String, callback: PlayerStateCallback,
            progressbar: ProgressBar, thumbnail: ImageView,
            item_index: Int? = null
        ) {

            //region trackSelector & loadControl & Exoplayer
            val trackSelector: TrackSelector = DefaultTrackSelector(context)

            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    MIN_BUFFER, MAX_BUFFER,
                    BUFFER_PLAYBACK, BUFFER_PLAYBACK_RE_BUFFER
                ).setBackBuffer(BACK_BUFFER_PLAYBACK, true)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()

            val player = SimpleExoPlayer.Builder(context)
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .setBandwidthMeter(DefaultBandwidthMeter.Builder(context).build())
                .setHandleAudioBecomingNoisy(true)
                .build()

            player.playWhenReady = false
            player.repeatMode = Player.REPEAT_MODE_ALL
            setKeepContentOnPlayerReset(true)
            this.useController = true

            val mediaSource = ProgressiveMediaSource
                .Factory(DefaultHttpDataSourceFactory("RecyclerViewExoPlayer"))
                .createMediaSource(
                    MediaItem.Builder().setUri(Uri.parse(url)).build()
                )

            player.setMediaSource(mediaSource)
            player.prepare()
            this.player = player
            //endregion

            //region playersMap
            if (playersMap.containsKey(item_index))
                playersMap.remove(item_index)
            if (item_index != null)
                playersMap[item_index] = player
            //endregion

            //region player Listener
            this.player!!.addListener(object : Player.EventListener {

                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    this@loadVideo.context.toast("Oops! Error occurred while playing media.")
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)

                    if (playbackState == Player.STATE_BUFFERING) {
                        hide.postValue(false)
                        callback.onVideoBuffering(player)
                        thumbnail.visibility = View.VISIBLE
                        progressbar.visibility = View.VISIBLE
                    }

                    if (playbackState == Player.STATE_READY) {
                        progressbar.visibility = View.GONE
                        thumbnail.visibility = View.GONE
                        hide.postValue(true)
                        callback.onVideoDurationRetrieved(this@loadVideo.player!!.duration, player)
                    }

                    if (playbackState == Player.STATE_READY && player.playWhenReady) {
                        hide.postValue(true)
                        callback.onStartedPlaying(player)
                    }
                }
            })
            //endregion

        }
    }
}
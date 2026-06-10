package tv.iptv.player

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tv.iptv.player.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide system UI for full-screen video
        hideSystemUI()

        initializePlayer()
        observeViewModel()

        // Handle intent extras
        val index = intent.getIntExtra("EXTRA_INDEX", 0)
        val channelList = intent.getParcelableArrayListExtra<Channel>("EXTRA_CHANNEL_LIST") ?: arrayListOf()
        viewModel.setPlaylist(channelList, index)
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> binding.progressBar.visibility = View.VISIBLE
                            Player.STATE_READY -> binding.progressBar.visibility = View.GONE
                            else -> binding.progressBar.visibility = View.GONE
                        }
                    }
                })
            }
        binding.playerView.player = player
        binding.playerView.useController = false // Custom UI handled separately
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentChannel.collectLatest { channel ->
                channel?.let {
                    playChannel(it)
                    updateOverlay(it)
                }
            }
        }
    }

    private fun playChannel(channel: Channel) {
        val mediaItem = MediaItem.fromUri(channel.url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    private fun updateOverlay(channel: Channel) {
        binding.channelName.text = channel.name
        // Show overlay temporarily
        binding.overlayLayout.visibility = View.VISIBLE
        binding.overlayLayout.postDelayed({
            binding.overlayLayout.visibility = View.GONE
        }, 5000)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
                viewModel.nextChannel()
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                viewModel.previousChannel()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                toggleSideChannelList()
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                showChannelInfo()
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                if (binding.sideChannelList.visibility == View.VISIBLE || binding.overlayLayout.visibility == View.VISIBLE) {
                    binding.sideChannelList.visibility = View.GONE
                    binding.overlayLayout.visibility = View.GONE
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun toggleSideChannelList() {
        if (binding.sideChannelList.visibility == View.VISIBLE) {
            binding.sideChannelList.visibility = View.GONE
        } else {
            binding.sideChannelList.visibility = View.VISIBLE
            // Populate and focus list
        }
    }

    private fun showChannelInfo() {
        viewModel.currentChannel.value?.let { updateOverlay(it) }
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}

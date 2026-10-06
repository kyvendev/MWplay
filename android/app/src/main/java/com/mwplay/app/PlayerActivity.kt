package com.mwplay.app

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@UnstableApi
class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = intent.getStringExtra(EXTRA_TITLE) ?: "MW Play"

        val url = intent.getStringExtra(EXTRA_URL)
        if (url.isNullOrBlank()) {
            finish()
            return
        }

        playerView = PlayerView(this).apply {
            useController = true
            keepScreenOn = true
            setShowSubtitleButton(true)
            setShowNextButton(false)
            setShowPreviousButton(false)
        }
        setContentView(playerView)

        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    val detail = buildString {
                        append("Não foi possível reproduzir esta mídia.\n\n")
                        append("Código: ${error.errorCodeName}")
                        error.cause?.message?.takeIf { it.isNotBlank() }?.let {
                            append("\nDetalhe: $it")
                        }
                    }
                    AlertDialog.Builder(this@PlayerActivity)
                        .setTitle("Erro de reprodução")
                        .setMessage(detail)
                        .setPositiveButton("OK", null)
                        .show()
                }

                override fun onTracksChanged(tracks: Tracks) {
                    invalidateOptionsMenu()
                }
            })
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        // Long press the settings icon area is not required: Media3's controller
        // exposes subtitles directly. Tapping the Activity title area is avoided;
        // audio selection is available through the Android options menu below.
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menu.add("Áudio").setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS)
        menu.add("Legendas").setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.title?.toString()) {
            "Áudio" -> {
                showTrackDialog(C.TRACK_TYPE_AUDIO, "Idioma do áudio")
                true
            }
            "Legendas" -> {
                showTrackDialog(C.TRACK_TYPE_TEXT, "Legendas", includeOff = true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showTrackDialog(trackType: Int, dialogTitle: String, includeOff: Boolean = false) {
        val exoPlayer = player ?: return
        val choices = mutableListOf<TrackChoice>()
        if (includeOff) choices += TrackChoice("Desativadas", null, -1)

        exoPlayer.currentTracks.groups.forEach { group ->
            if (group.type != trackType) return@forEach
            for (index in 0 until group.length) {
                if (!group.isTrackSupported(index)) continue
                val format = group.getTrackFormat(index)
                val language = format.label
                    ?: format.language?.let { java.util.Locale.forLanguageTag(it).displayLanguage }
                    ?: if (trackType == C.TRACK_TYPE_AUDIO) "Faixa de áudio ${choices.size + 1}" else "Legenda ${choices.size + 1}"
                choices += TrackChoice(language, group.mediaTrackGroup, index)
            }
        }

        if (choices.size <= if (includeOff) 1 else 0) {
            Toast.makeText(this, "Nenhuma faixa disponível", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setItems(choices.map { it.label }.toTypedArray()) { _, which ->
                val choice = choices[which]
                val builder = exoPlayer.trackSelectionParameters.buildUpon()
                if (trackType == C.TRACK_TYPE_TEXT && choice.group == null) {
                    builder.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                } else if (choice.group != null) {
                    builder.setTrackTypeDisabled(trackType, false)
                    builder.clearOverridesOfType(trackType)
                    builder.addOverride(TrackSelectionOverride(choice.group, listOf(choice.trackIndex)))
                }
                exoPlayer.trackSelectionParameters = builder.build()
            }
            .show()
    }

    override fun onStop() {
        player?.release()
        player = null
        super.onStop()
    }

    private data class TrackChoice(
        val label: String,
        val group: androidx.media3.common.TrackGroup?,
        val trackIndex: Int
    )

    companion object {
        const val EXTRA_URL = "media_url"
        const val EXTRA_TITLE = "media_title"
    }
}

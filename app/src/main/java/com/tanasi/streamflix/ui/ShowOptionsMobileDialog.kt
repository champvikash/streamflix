package com.tanasi.streamflix.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tanasi.streamflix.R
import com.tanasi.streamflix.adapters.AppAdapter
import com.tanasi.streamflix.database.AppDatabase
import com.tanasi.streamflix.databinding.DialogShowOptionsMobileBinding
import com.tanasi.streamflix.fragments.home.HomeMobileFragment
import com.tanasi.streamflix.fragments.home.HomeMobileFragmentDirections
import com.tanasi.streamflix.fragments.season.SeasonMobileFragment
import com.tanasi.streamflix.models.Episode
import com.tanasi.streamflix.models.Movie
import com.tanasi.streamflix.models.TvShow
import com.tanasi.streamflix.utils.format
import com.tanasi.streamflix.utils.getCurrentFragment
import com.tanasi.streamflix.utils.toActivity
import java.util.Calendar

class ShowOptionsMobileDialog(
    context: Context,
    show: AppAdapter.Item,
) : BottomSheetDialog(context) {

    private val binding = DialogShowOptionsMobileBinding.inflate(LayoutInflater.from(context))

    private val database = AppDatabase.getInstance(context)

    init {
        setContentView(binding.root)

        findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundColor(Color.TRANSPARENT)

        when (show) {
            is Episode -> displayEpisode(show)
            is Movie -> displayMovie(show)
            is TvShow -> displayTvShow(show)
        }

        binding.btnOptionCancel.setOnClickListener {
            hide()
        }
    }


    private fun displayEpisode(episode: Episode) {
        database.episodeDao().getById(episode.id)?.let { episodeDb ->
            episode.merge(episodeDb)
        }

        Glide.with(context)
            .load(episode.poster ?: episode.tvShow?.poster)
            .fitCenter()
            .into(binding.ivOptionsShowPoster)

        binding.tvOptionsShowTitle.text = episode.title

        binding.tvShowSubtitle.text = episode.season?.number?.let { seasonNumber ->
            context.getString(
                R.string.episode_item_info,
                seasonNumber,
                episode.number,
                episode.title,
            )
        } ?: context.getString(
            R.string.episode_item_info_episode_only,
            episode.number,
            episode.title,
        )


        binding.btnOptionEpisodeOpenTvShow.apply {
            setOnClickListener {
                when (val fragment = context.toActivity()?.getCurrentFragment()) {
                    is HomeMobileFragment -> episode.tvShow?.let { tvShow ->
                        NavHostFragment.findNavController(fragment).navigate(
                            HomeMobileFragmentDirections.actionHomeToTvShow(
                                id = tvShow.id
                            )
                        )
                    }
                }
                hide()
            }

            visibility = when (context.toActivity()?.getCurrentFragment()) {
                is HomeMobileFragment -> View.VISIBLE
                else -> View.GONE
            }
        }

        binding.btnOptionShowFavorite.visibility = View.GONE

        binding.btnOptionShowWatched.apply {
            setOnClickListener {
                episode.isWatched = !episode.isWatched
                if (episode.isWatched) {
                    episode.watchedDate = Calendar.getInstance()
                    episode.watchHistory = null
                } else {
                    episode.watchedDate = null
                }
                database.episodeDao().save(episode)

                when (val fragment = context.toActivity()?.getCurrentFragment()) {
                    is HomeMobileFragment -> fragment.refresh()
                    is SeasonMobileFragment -> fragment.refresh(episode)
                }
                hide()
            }

            text = when {
                episode.isWatched -> context.getString(R.string.option_show_unwatched)
                else -> context.getString(R.string.option_show_watched)
            }
            visibility = View.VISIBLE
        }

        binding.btnOptionProgramClear.apply {
            setOnClickListener {
                if (episode.watchHistory == null) return@setOnClickListener

                episode.watchHistory = null
                database.episodeDao().save(episode)

                when (val fragment = context.toActivity()?.getCurrentFragment()) {
                    is HomeMobileFragment -> fragment.refresh()
                    is SeasonMobileFragment -> fragment.refresh(episode)
                }
                hide()
            }

            visibility = when {
                episode.watchHistory != null -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    private fun displayMovie(movie: Movie) {
        database.movieDao().getById(movie.id)?.let { movieDb ->
            movie.merge(movieDb)
        }

        Glide.with(context)
            .load(movie.poster)
            .fitCenter()
            .into(binding.ivOptionsShowPoster)

        binding.tvOptionsShowTitle.text = movie.title

        binding.tvShowSubtitle.text = movie.released?.format("yyyy")


        binding.btnOptionEpisodeOpenTvShow.visibility = View.GONE

        binding.btnOptionShowFavorite.apply {
            setOnClickListener {
                movie.isFavorite = !movie.isFavorite
                database.movieDao().save(movie)

                when (val fragment = context.toActivity()?.getCurrentFragment()) {
                    is HomeMobileFragment -> fragment.refresh()
                }
                hide()
            }

            text = when {
                movie.isFavorite -> context.getString(R.string.option_show_unfavorite)
                else -> context.getString(R.string.option_show_favorite)
            }
            visibility = View.VISIBLE
        }

        binding.btnOptionShowWatched.apply {
            setOnClickListener {
                movie.isWatched = !movie.isWatched
                if (movie.isWatched) {
                    movie.watchedDate = Calendar.getInstance()
                    movie.watchHistory = null
                } else {
                    movie.watchedDate = null
                }
                database.movieDao().save(movie)

                when (val fragment = context.toActivity()?.getCurrentFragment()) {
                    is HomeMobileFragment -> fragment.refresh()
                }
                hide()
            }

            text = when {
                movie.isWatched -> context.getString(R.string.option_show_unwatched)
                else -> context.getString(R.string.option_show_watched)
            }
            visibility = View.VISIBLE
        }

        binding.btnOptionProgramClear.apply {
            setOnClickListener {
                if (movie.watchHistory == null) return@setOnClickListener

                movie.watchHistory = null
                database.movieDao().save(movie)

                when (val fragment = context.toActivity()?.getCurrentFragment()) {
                    is HomeMobileFragment -> fragment.refresh()
                }
                hide()
            }

            visibility = when {
                movie.watchHistory != null -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    private fun displayTvShow(tvShow: TvShow) {
        database.tvShowDao().getById(tvShow.id)?.let { tvShowDb ->
            tvShow.merge(tvShowDb)
        }

        Glide.with(context)
            .load(tvShow.poster)
            .fitCenter()
            .into(binding.ivOptionsShowPoster)

        binding.tvOptionsShowTitle.text = tvShow.title

        binding.tvShowSubtitle.text = tvShow.released?.format("yyyy")


        binding.btnOptionEpisodeOpenTvShow.visibility = View.GONE

        binding.btnOptionShowFavorite.apply {
            setOnClickListener {
                tvShow.isFavorite = !tvShow.isFavorite
                database.tvShowDao().save(tvShow)

                when (val fragment = context.toActivity()?.getCurrentFragment()) {
                    is HomeMobileFragment -> fragment.refresh()
                }
                hide()
            }

            text = when {
                tvShow.isFavorite -> context.getString(R.string.option_show_unfavorite)
                else -> context.getString(R.string.option_show_favorite)
            }
            visibility = View.VISIBLE
        }

        binding.btnOptionShowWatched.visibility = View.GONE

        binding.btnOptionProgramClear.visibility = View.GONE
    }
}
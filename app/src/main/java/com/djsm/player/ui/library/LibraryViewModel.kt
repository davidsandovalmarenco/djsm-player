package com.djsm.player.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djsm.player.domain.model.SortOption
import com.djsm.player.domain.model.SortOrder
import com.djsm.player.domain.usecase.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.ASCENDING)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<LibraryUiState> = _retryTrigger.flatMapLatest {
        combine(
            getSongsUseCase.observeSongs(),
            _searchQuery.debounce(300).distinctUntilChanged(),
            _sortOption,
            _sortOrder
        ) { songs, query, option, order ->
            
            if (songs.isEmpty()) {
                return@combine LibraryUiState.Empty
            }

            val baseAlbums = songs.groupBy { it.albumId }.map { (albumId, albumSongs) ->
                val firstSong = albumSongs.first()
                com.djsm.player.domain.model.Album(
                    id = albumId,
                    name = firstSong.album,
                    artist = firstSong.artist,
                    songCount = albumSongs.size,
                    durationMs = albumSongs.sumOf { it.durationMs },
                    songs = albumSongs.sortedBy { it.trackNumber ?: 0 }
                )
            }

            val baseArtists = songs.groupBy { it.artistId }.map { (artistId, artistSongs) ->
                val firstSong = artistSongs.first()
                com.djsm.player.domain.model.Artist(
                    id = artistId,
                    name = firstSong.artist,
                    songCount = artistSongs.size,
                    albumCount = artistSongs.distinctBy { it.albumId }.size,
                    songs = artistSongs.sortedBy { it.title.lowercase() }
                )
            }

            val baseFolders = songs.groupBy { it.folderId }.map { (folderId, folderSongs) ->
                val firstSong = folderSongs.first()
                com.djsm.player.domain.model.Folder(
                    id = folderId,
                    name = firstSong.folderName,
                    songCount = folderSongs.size,
                    songs = folderSongs.sortedBy { it.title.lowercase() }
                )
            }

            val baseGenres = songs.groupBy { it.genreId }.map { (genreId, genreSongs) ->
                val firstSong = genreSongs.first()
                com.djsm.player.domain.model.Genre(
                    id = genreId,
                    name = firstSong.genre,
                    songCount = genreSongs.size,
                    songs = genreSongs.sortedBy { it.title.lowercase() }
                )
            }

            val filteredSongs = if (query.isBlank()) {
                songs
            } else {
                songs.filter { song ->
                    song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true) ||
                    song.album.contains(query, ignoreCase = true)
                }
            }
            
            val filteredAlbums = if (query.isBlank()) baseAlbums else baseAlbums.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true)
            }
            
            val filteredArtists = if (query.isBlank()) baseArtists else baseArtists.filter {
                it.name.contains(query, ignoreCase = true)
            }

            val filteredFolders = if (query.isBlank()) baseFolders else baseFolders.filter {
                it.name.contains(query, ignoreCase = true)
            }

            val filteredGenres = if (query.isBlank()) baseGenres else baseGenres.filter {
                it.name.contains(query, ignoreCase = true)
            }

            if (filteredSongs.isEmpty() && filteredAlbums.isEmpty() && filteredArtists.isEmpty() && filteredFolders.isEmpty() && filteredGenres.isEmpty() && query.isNotBlank()) {
                return@combine LibraryUiState.NoSearchResults(query)
            }

            val sortedSongs = when (option) {
                SortOption.TITLE -> filteredSongs.sortedBy { it.title.lowercase() }
                SortOption.ARTIST -> filteredSongs.sortedBy { it.artist.lowercase() }
                SortOption.ALBUM -> filteredSongs.sortedBy { it.album.lowercase() }
                SortOption.DURATION -> filteredSongs.sortedBy { it.durationMs }
                SortOption.DATE_ADDED -> filteredSongs.sortedBy { it.dateAddedSeconds }
            }.let {
                if (order == SortOrder.DESCENDING) it.reversed() else it
            }
            
            val sortedAlbums = when (option) {
                SortOption.TITLE, SortOption.ALBUM -> filteredAlbums.sortedBy { it.name.lowercase() }
                SortOption.ARTIST -> filteredAlbums.sortedBy { it.artist.lowercase() }
                SortOption.DURATION -> filteredAlbums.sortedBy { it.durationMs }
                SortOption.DATE_ADDED -> filteredAlbums.sortedBy { it.name.lowercase() }
            }.let {
                if (order == SortOrder.DESCENDING) it.reversed() else it
            }

            val sortedArtists = when (option) {
                SortOption.TITLE, SortOption.ARTIST, SortOption.ALBUM -> filteredArtists.sortedBy { it.name.lowercase() }
                SortOption.DURATION, SortOption.DATE_ADDED -> filteredArtists.sortedBy { it.name.lowercase() }
            }.let {
                if (order == SortOrder.DESCENDING) it.reversed() else it
            }

            val sortedFolders = when (option) {
                SortOption.TITLE, SortOption.ARTIST, SortOption.ALBUM -> filteredFolders.sortedBy { it.name.lowercase() }
                SortOption.DURATION, SortOption.DATE_ADDED -> filteredFolders.sortedBy { it.name.lowercase() }
            }.let {
                if (order == SortOrder.DESCENDING) it.reversed() else it
            }

            val sortedGenres = when (option) {
                SortOption.TITLE, SortOption.ARTIST, SortOption.ALBUM -> filteredGenres.sortedBy { it.name.lowercase() }
                SortOption.DURATION, SortOption.DATE_ADDED -> filteredGenres.sortedBy { it.name.lowercase() }
            }.let {
                if (order == SortOrder.DESCENDING) it.reversed() else it
            }

            LibraryUiState.Success(
                songs = sortedSongs,
                albums = sortedAlbums,
                artists = sortedArtists,
                folders = sortedFolders,
                genres = sortedGenres
            )
        }
        .flowOn(Dispatchers.Default)
        .catch { exception ->
            emit(LibraryUiState.Error(exception.message ?: "No se pudo cargar la biblioteca"))
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState.Loading
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleSortOrder() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.ASCENDING) {
            SortOrder.DESCENDING
        } else {
            SortOrder.ASCENDING
        }
    }

    fun loadSongs() {
        _retryTrigger.value += 1
    }
}
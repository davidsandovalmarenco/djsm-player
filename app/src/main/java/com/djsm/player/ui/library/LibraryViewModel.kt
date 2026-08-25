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

            val filteredSongs = if (query.isBlank()) {
                songs
            } else {
                songs.filter { song ->
                    song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true) ||
                    song.album.contains(query, ignoreCase = true)
                }
            }

            if (filteredSongs.isEmpty() && query.isNotBlank()) {
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

            LibraryUiState.Success(sortedSongs)
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
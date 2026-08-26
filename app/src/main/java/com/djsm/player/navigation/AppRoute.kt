package com.djsm.player.navigation

sealed interface AppRoute

data object LibraryRoute : AppRoute

data class AlbumDetailRoute(val albumId: Long) : AppRoute

data class ArtistDetailRoute(val artistId: Long) : AppRoute

data class FolderDetailRoute(val folderId: String) : AppRoute

data class GenreDetailRoute(val genreId: Long) : AppRoute

data class PlaylistDetailRoute(val playlistId: String) : AppRoute

data object NowPlayingRoute : AppRoute

data object SettingsRoute : AppRoute
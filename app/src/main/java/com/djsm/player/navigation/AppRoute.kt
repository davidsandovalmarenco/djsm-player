package com.djsm.player.navigation

sealed interface AppRoute

data object LibraryRoute : AppRoute

data class AlbumDetailRoute(val albumId: Long) : AppRoute

data class ArtistDetailRoute(val artistId: Long) : AppRoute

data object NowPlayingRoute : AppRoute
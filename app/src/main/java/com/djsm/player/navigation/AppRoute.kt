package com.djsm.player.navigation

sealed interface AppRoute

data object LibraryRoute : AppRoute

data object NowPlayingRoute : AppRoute
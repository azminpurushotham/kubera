package com.collection.kubera.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.PagerState
import com.collection.kubera.data.Shop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Overlay screens shown on top of the tab pager (Shop Details, Update Shop, Collection History).
 */
sealed class DetailOverlay {
    data class ShopDetails(val shop: Shop? = null, val shopId: String? = null) : DetailOverlay()
    data class ShopCollectionHistory(val shop: Shop) : DetailOverlay()
    data class UpdateShop(val shop: Shop) : DetailOverlay()
}

/**
 * Handles tab switching (pager) and detail overlay navigation.
 */
class MainTabNavigator(
    val pagerState: PagerState,
    private val setOverlay: (DetailOverlay?) -> Unit,
    private val coroutineScope: CoroutineScope,
    private val onProgrammaticScrollTarget: ((Pair<Int, Int>?) -> Unit)? = null,
) {
    fun switchToTab(index: Int) {
        coroutineScope.launch {
            val from = pagerState.currentPage
            onProgrammaticScrollTarget?.invoke(from to index)
            try {
                pagerState.animateScrollToPage(
                    index,
                    animationSpec = tween(durationMillis = 280)
                )
            } finally {
                onProgrammaticScrollTarget?.invoke(null)
            }
        }
    }

    fun navigateToShopDetails(shop: Shop? = null, shopId: String? = null) {
        setOverlay(DetailOverlay.ShopDetails(shop, shopId))
    }

    fun navigateToShopCollectionHistory(shop: Shop) {
        setOverlay(DetailOverlay.ShopCollectionHistory(shop))
    }

    fun navigateToUpdateShop(shop: Shop) {
        setOverlay(DetailOverlay.UpdateShop(shop))
    }

    fun onBack() {
        setOverlay(null)
    }

    fun openDrawer(openDrawer: suspend () -> Unit) {
        coroutineScope.launch { openDrawer() }
    }
}

package com.example.recyclerviewexoplayer.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class PreloadLinearLayoutManager : LinearLayoutManager {
    private var mOrientationHelper: OrientationHelper? = null


    private var mAdditionalAdjacentPrefetchItemCount = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        mOrientationHelper = OrientationHelper.createOrientationHelper(this, orientation)
    }

    fun setPreloadItemCount(preloadItemCount: Int) {
        require(preloadItemCount >= 1) { "adjacentPrefetchItemCount must not smaller than 1!" }
        mAdditionalAdjacentPrefetchItemCount = preloadItemCount - 1
    }

    override fun collectAdjacentPrefetchPositions(
        dx: Int, dy: Int, state: RecyclerView.State,
        layoutPrefetchRegistry: LayoutPrefetchRegistry
    ) {
        super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry)

        val delta = if (orientation == HORIZONTAL) dx else dy
        if (childCount == 0 || delta == 0) {
            // can't support this scroll, so don't bother prefetching
            return
        }
        val layoutDirection = if (delta > 0) 1 else -1
        val child = getChildClosest(layoutDirection)
        val currentPosition = getPosition(child!!) + layoutDirection
        val scrollingOffset: Int

        if (layoutDirection == 1) {
            scrollingOffset = (mOrientationHelper!!.getDecoratedEnd(child)
                    - mOrientationHelper!!.endAfterPadding)
            for (i in currentPosition + 1 until currentPosition + mAdditionalAdjacentPrefetchItemCount + 1) {
                if (i >= 0 && i < state.itemCount) {
                    layoutPrefetchRegistry.addPosition(i, max(0, scrollingOffset))
                }
            }
        }
    }

    private fun getChildClosest(layoutDirection: Int): View? {
        return getChildAt(if (layoutDirection == -1) 0 else childCount - 1)
    }
}

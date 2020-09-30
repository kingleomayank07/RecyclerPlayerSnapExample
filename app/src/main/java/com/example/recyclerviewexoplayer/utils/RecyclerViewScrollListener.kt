package com.example.recyclerviewexoplayer.utils

import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewScrollListener : RecyclerView.OnScrollListener() {

    //region variables
    @Volatile
    private var mEnabled = true
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var mPreLoadCount = 5
    //endregion

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (mEnabled) {
            val manager = recyclerView.layoutManager
            require(manager is PreloadLinearLayoutManager) { "Expected recyclerview to have linear layout manager" }
            visibleItemCount = manager.childCount
            firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition()
            onItemIsFirstVisibleItem(firstVisibleItem)
        }
    }

    abstract fun onItemIsFirstVisibleItem(index: Int)

    fun disableScrollListener() {
        mEnabled = false
    }

    fun enableScrollListener() {
        mEnabled = true
    }

    fun setPreLoadCount(mPreLoadCount: Int) {
        this.mPreLoadCount = mPreLoadCount
    }
}
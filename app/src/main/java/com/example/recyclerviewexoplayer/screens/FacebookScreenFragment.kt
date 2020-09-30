package com.example.recyclerviewexoplayer.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.recyclerviewexoplayer.R
import com.example.recyclerviewexoplayer.models.MediaObject
import com.example.recyclerviewexoplayer.utils.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import com.example.recyclerviewexoplayer.utils.PlayerViewAdapter.Companion.releaseAllPlayers
import com.example.recyclerviewexoplayer.utils.PreloadLinearLayoutManager
import com.example.recyclerviewexoplayer.utils.RecyclerViewScrollListener
import com.example.recyclerviewexoplayer.utils.hide
import com.example.recyclerviewexoplayer.viewModels.MediaViewModel
import kotlinx.android.synthetic.main.fragment_facebook_player.*

class FacebookScreenFragment : Fragment(R.layout.fragment_facebook_player) {
    private var mAdapter: FacebookRecyclerAdapter? = null
    private val modelList = ArrayList<MediaObject>()

    private lateinit var scrollListener: RecyclerViewScrollListener


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        val model: MediaViewModel by viewModels()
        model.getMedia().observe(requireActivity(), {
            mAdapter?.updateList(arrayListOf(*it.toTypedArray()))
        })
    }

    private fun setAdapter() {
        mAdapter = FacebookRecyclerAdapter(requireActivity(), modelList)
        recycler_view!!.setHasFixedSize(true)

        val layoutManager = PreloadLinearLayoutManager(activity)
        layoutManager.setPreloadItemCount(5)
        recycler_view!!.layoutManager = layoutManager
        recycler_view!!.adapter = mAdapter
        scrollListener = object : RecyclerViewScrollListener() {
            override fun onItemIsFirstVisibleItem(index: Int) {
                Log.d("visible item index", index.toString())
                if (index != -1)
                    playIndexThenPausePreviousPlayer(index)
            }

        }
        hide.observe(viewLifecycleOwner, {
            if (it) {
                recycler_view!!.visibility = VISIBLE
                pg1.visibility = GONE
            } else {
                recycler_view!!.visibility = GONE
                pg1.visibility = VISIBLE
            }
        })
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)
        recycler_view!!.addOnScrollListener(scrollListener)
        scrollListener.setPreLoadCount(5)
        mAdapter!!.setOnItemClickListener(object : FacebookRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int, model: MediaObject?) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        releaseAllPlayers()
    }
}
package com.example.recyclerviewexoplayer.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.recyclerviewexoplayer.R
import com.example.recyclerviewexoplayer.models.MediaObject
import com.example.recyclerviewexoplayer.utils.PlayerViewAdapter.Companion.pauseAllPlayers
import com.example.recyclerviewexoplayer.utils.PlayerViewAdapter.Companion.playAllPlayers
import com.example.recyclerviewexoplayer.utils.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import com.example.recyclerviewexoplayer.utils.PlayerViewAdapter.Companion.releaseAllPlayers
import com.example.recyclerviewexoplayer.utils.PreloadLinearLayoutManager
import com.example.recyclerviewexoplayer.utils.RecyclerViewScrollListener
import com.example.recyclerviewexoplayer.viewModels.MediaViewModel
import kotlinx.android.synthetic.main.fragment_facebook_player.*

class FacebookScreenFragment : Fragment(R.layout.fragment_facebook_player) {

    //region variables
    private var mAdapter: FacebookRecyclerAdapter? = null
    private val modelList = ArrayList<MediaObject>()
    private lateinit var scrollListener: RecyclerViewScrollListener
    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()

        //region ViewModel
        val model: MediaViewModel by viewModels()
        model.getMedia().observe(requireActivity(), {
            mAdapter?.updateList(arrayListOf(*it.toTypedArray()))
        })
        //endregion

    }

    private fun setAdapter() {
        //region adapter
        mAdapter = FacebookRecyclerAdapter(requireActivity(), modelList)
        recycler_view!!.setHasFixedSize(true)
        recycler_view!!.maxFlingVelocity
        recycler_view!!.setItemViewCacheSize(5)
        //endregion

        //region layoutManager
        val layoutManager =
            PreloadLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
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
        //endregion

        /*
          //region hide recyclerview until first video is ready to be played.
            hide.observe(viewLifecycleOwner, {
                if (it) {
                    recycler_view!!.visibility = VISIBLE
                    pg1.visibility = GONE
                } else {
                    recycler_view!!.visibility = GONE
                    pg1.visibility = VISIBLE
                }
            })
            //endregion
            */

        //region SnapHelper
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)
        recycler_view!!.addOnScrollListener(scrollListener)
        scrollListener.setPreLoadCount(5)
        mAdapter!!.setOnItemClickListener(object : FacebookRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int, model: MediaObject?) {}
        })
        //endregion

    }

    override fun onPause() {
        super.onPause()
        //region pause current player
        pauseAllPlayers()
        //endregion
    }

    override fun onResume() {
        super.onResume()
        //region play current player
        playAllPlayers()
        //endregion
    }

    override fun onDestroy() {
        super.onDestroy()
        //region release players
        releaseAllPlayers()
        //endregion
    }

}


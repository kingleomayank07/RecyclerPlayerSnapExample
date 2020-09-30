package com.example.recyclerviewexoplayer.screens

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewexoplayer.R
import com.example.recyclerviewexoplayer.databinding.FacebookTimelineItemRecyclerListBinding
import com.example.recyclerviewexoplayer.models.MediaObject
import com.example.recyclerviewexoplayer.utils.PlayerStateCallback
import com.example.recyclerviewexoplayer.utils.PlayerViewAdapter.Companion.releaseRecycledPlayers
import com.google.android.exoplayer2.Player
import java.util.*

class FacebookRecyclerAdapter(
    private val mContext: Context,
    private var modelList: ArrayList<MediaObject>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    PlayerStateCallback {

    private var mItemClickListener: OnItemClickListener? =
        null

    fun updateList(modelList: ArrayList<MediaObject>) {
        this.modelList = modelList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {
        val binding: FacebookTimelineItemRecyclerListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.facebook_timeline_item_recycler_list,
            viewGroup,
            false
        )
        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        if (holder is VideoPlayerViewHolder) {
            val model = getItem(position)

            holder.onBind(model)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getItem(position: Int): MediaObject {
        return modelList[position]
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        this.mItemClickListener = mItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            position: Int,
            model: MediaObject?
        )
    }

    inner class VideoPlayerViewHolder(private val binding: FacebookTimelineItemRecyclerListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: MediaObject) {
            // handel on item click
            binding.root.setOnClickListener {
                mItemClickListener!!.onItemClick(
                    it,
                    adapterPosition,
                    model
                )
            }

            binding.apply {
                dataModel = model
                callback = this@FacebookRecyclerAdapter
                index = adapterPosition
                executePendingBindings()
            }
        }
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {}

    override fun onVideoBuffering(player: Player) {}

    override fun onStartedPlaying(player: Player) {
        Log.d("playVideo", "start" + player.contentDuration)

    }
    override fun onFinishedPlaying(player: Player) {}
}
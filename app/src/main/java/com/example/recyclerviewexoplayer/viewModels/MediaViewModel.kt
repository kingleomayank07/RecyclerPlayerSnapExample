package com.example.recyclerviewexoplayer.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recyclerviewexoplayer.models.MediaObject
import com.example.recyclerviewexoplayer.models.reposatories.MediaRepo

class MediaViewModel : ViewModel() {
    private val mediaData: MutableLiveData<MutableList<MediaObject>> = MediaRepo().getMediaData()
    fun getMedia(): MutableLiveData<MutableList<MediaObject>> {
        return mediaData
    }
}
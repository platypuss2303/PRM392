package com.example.instagram.model

class Story {
    private var imageurl: String = ""
    private var timestart: Long = 0
    private var userid: String = ""
    private var storyid: String = ""
    private var timeend: Long = 0

    constructor()

    constructor(imageurl: String, timestart: Long, userid: String, storyid: String, timeend: Long) {
        this.imageurl = imageurl
        this.timestart = timestart
        this.userid = userid
        this.storyid = storyid
        this.timeend = timeend
    }

    fun getStoryid(): String {
        return storyid
    }

    fun setStoryid(storyid: String) {
        this.storyid = storyid
    }

    fun getTimeend(): Long {
        return timeend
    }

    fun setTimeend(timeend: Long) {
        this.timeend = timeend
    }

    fun getImageurl(): String {
        return imageurl
    }

    fun setImageurl(imageurl: String) {
        this.imageurl = imageurl
    }
    fun getTimestart(): Long {
        return timestart
    }

    fun setTimestart(timestamp: Long) {
        this.timestart = timestamp
    }

    fun getUserid(): String {
        return userid
    }

    fun setUserid(userid: String) {
        this.userid = userid
    }
}


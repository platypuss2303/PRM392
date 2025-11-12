package com.example.instagram.model

class Notification {
    private var text: String = ""
    private var userid: String = ""
    private var postid: String = ""
    private var ispost = false
    constructor()

    constructor(text: String, userid: String, postid: String, ispost: Boolean){
        this.text = text
        this.userid = userid
        this.postid = postid
        this.ispost = ispost
    }

    fun getUserId():String{
        return userid

    }

    fun getText():String{
        return text

    }

    fun getPostid():String{
        return postid

    }

    fun isIspost():Boolean{
        return ispost

    }

    fun setUserId(userid: String) {
        this.userid = userid
    }

    fun setText(text: String) {
        this.text = text
    }

    fun setPostid(postid: String) {
        this.postid = postid
    }

    fun setIspost(ispost: Boolean) {
        this.ispost = ispost
    }





}
package com.example.instagram.model.socialModel

class Comment {
    private var comment: String = ""
    private var publisher: String = ""

    constructor()

    constructor(comment: String, publisher: String) {
        this.comment = comment
        this.publisher = publisher
    }

    // Getters and setters for comment and publisher
    fun getComment(): String {
        return comment
    }

    fun setComment(comment: String) {
        this.comment = comment
    }


    fun getPublisher(): String {
        return publisher
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher

    }
}
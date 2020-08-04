package com.noor.newease

class  Ticket{
    var tweetID:String?=null
    var tweetText:String?=null
    var tweetImageURL:String?=null
    var tweetPersonUID:String?=null
    var personImage:String?=null
    var postDate:Long?=null

    constructor(
        tweetID: String?,
        tweetText: String?,
        tweetImageURL: String?,
        tweetPersonUID: String?,
        personImage: String?,
        postDate: Long?
    ) {
        this.tweetID = tweetID
        this.tweetText = tweetText
        this.tweetImageURL = tweetImageURL
        this.tweetPersonUID = tweetPersonUID
        this.personImage = personImage
        this.postDate = postDate
    }
}

package com.noor.newease

class  PostInfo{
    var UserUID:String?=null
    var text:String?=null
    var postImage:String?=null
    var userImage:String?=null
    var postDate:Long ?=null

    constructor(
        UserUID: String?,
        text: String?,
        postImage: String?,
        userImage: String?,
        postDate: Long?
    ) {
        this.UserUID = UserUID
        this.text = text
        this.postImage = postImage
        this.userImage = userImage
        this.postDate = postDate
    }
}

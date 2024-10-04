package com.example.callscreenapp.database

import io.realm.kotlin.types.RealmObject

class MyTheme : RealmObject {
    var backgroundUrl: String = ""
    var avatarUrl: String =""
    var answerUrl: String = ""
    var rejectUrl: String = ""
}
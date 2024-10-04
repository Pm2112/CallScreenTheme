package com.example.callscreenapp.database

import io.realm.kotlin.types.RealmObject

class BackgroundTheme: RealmObject {
    var avatarUrl = ""
    var backgroundTheme = ""
    var iconAnswer = ""
    var iconReject = ""
}
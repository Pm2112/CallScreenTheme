package com.example.callscreenapp.database

import io.realm.kotlin.types.RealmObject

class Avatar : RealmObject {
    var avatarUrl: String = ""
    var avatarUri: String = ""
}
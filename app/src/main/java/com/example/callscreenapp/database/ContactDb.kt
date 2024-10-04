package com.example.callscreenapp.database

import io.realm.kotlin.types.RealmObject

class ContactDb :RealmObject {
    var contactName = ""
    var contactPhone = ""
    var backgroundTheme = ""
    var btnAnswer =""
    var btnReject = ""
    var avatarTheme = ""
}
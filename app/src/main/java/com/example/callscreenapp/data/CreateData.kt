package com.example.callscreenapp.data

import com.example.callscreenapp.model.ListAvatar
import com.example.callscreenapp.model.ListCallButton
import com.example.callscreenapp.model.PhoneCallListImage
import kotlin.random.Random


public var CategoryAnimeSize: Int = 10
public var CategoryLoveSize: Int = 10
public var CategoryAnimalSize: Int = 10
public var CategoryNatureSize: Int = 10
public var CategoryGameSize: Int = 10
public var CategoryCastleSize: Int = 10
public var CategoryFantasySize: Int = 10
public var CategoryTechSize: Int = 10
public var CategorySeaSize: Int = 10
public var CategoryGifSize: Int = 10
public var CategoryAllSize: Int = 10

public var ButtonCallSize: Int = 10
public var ButtonAllSize: Int = 10

public var AvatarSize: Int = 10

public var HttpUrlBackground: String =
    "https://callthemetest.s3.amazonaws.com/Data_background/"
public var AnimeUrl: String = "Anime/anime"
public var LoveUrl: String = "Love/love"
public var AnimalUrl: String = "Animal/animal"
public var NatureUrl: String = "Nature/nature"
public var GameUrl: String = "Game/game"
public var CastleUrl: String = "Castle/castle"
public var FantasyUrl: String = "Fantasy/fantasy"
public var TechUrl: String = "Tech/tech"
public var SeaUrl: String = "Sea/sea"
public var GifUrl: String = "Gif/gif"
public var AllUrl: String = "All/all"

public var HttpUrlButtonCall: String =
    "https://callthemetest.s3.amazonaws.com/Data_Button/"

public var HttpUrlButtonAll: String =
    "https://callthemetest.s3.amazonaws.com/button_all/"

public var HttpUrlAvatar: String =
    "https://callthemetest.s3.amazonaws.com/Data_avatar/"

var ListCategoryAll = mutableListOf<PhoneCallListImage>()
var ListCategoryGif = mutableListOf<PhoneCallListImage>()
var ListCategoryAnime = mutableListOf<PhoneCallListImage>()
var ListCategoryLove = mutableListOf<PhoneCallListImage>()
var ListCategoryAnimal = mutableListOf<PhoneCallListImage>()
var ListCategoryNature = mutableListOf<PhoneCallListImage>()
var ListCategoryGame = mutableListOf<PhoneCallListImage>()
var ListCategoryCastle = mutableListOf<PhoneCallListImage>()
var ListCategoryFantasy = mutableListOf<PhoneCallListImage>()
var ListCategoryTech = mutableListOf<PhoneCallListImage>()
var ListCategorySea = mutableListOf<PhoneCallListImage>()
var ListButtonCall = mutableListOf<ListCallButton>()
var ListButtonAll = mutableListOf<ListCallButton>()
var ListAvatar = mutableListOf<ListAvatar>()

fun CreateListCategoryAll() {
    ListCategoryAll = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryAllSize) {
            if (i <= ButtonAllSize) {
                val randomAvatar = Random.nextInt(1, AvatarSize)
                add(
                    PhoneCallListImage(
                        "${HttpUrlBackground}${AllUrl}_$i.webp",
                        "${HttpUrlAvatar}${randomAvatar}.webp",
                        "${HttpUrlButtonAll}${i}B.webp",
                        "${HttpUrlButtonAll}${i}A.webp"
                    )
                )
            } else {
                val randomButton = Random.nextInt(1, ButtonCallSize)
                val randomAvatar = Random.nextInt(1, AvatarSize)
                add(
                    PhoneCallListImage(
                        "${HttpUrlBackground}${AllUrl}_$i.webp",
                        "${HttpUrlAvatar}${randomAvatar}.webp",
                        "${HttpUrlButtonCall}${randomButton}B.webp",
                        "${HttpUrlButtonCall}${randomButton}A.webp"
                    )
                )
            }
        }
    }
}

fun CreateListCategoryGif() {
    ListCategoryGif = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryGifSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${GifUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryAnime() {
    ListCategoryAnime = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryAnimeSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${AnimeUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryLove() {
    ListCategoryLove = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryLoveSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${LoveUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryAnimal() {
    ListCategoryAnimal = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryAnimalSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${AnimalUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryNature() {
    ListCategoryNature = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryNatureSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${NatureUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryGame() {
    ListCategoryGame = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryGameSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${GameUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryCastle() {
    ListCategoryCastle = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryCastleSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${CastleUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryFantasy() {
    ListCategoryFantasy = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryFantasySize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${FantasyUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategoryTech() {
    ListCategoryTech = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategoryTechSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${TechUrl}_$i.webp",
                    "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListCategorySea() {
    ListCategorySea = mutableListOf<PhoneCallListImage>().apply {
        for (i in 1..CategorySeaSize) {
            val randomButton = Random.nextInt(1, ButtonCallSize)
            val randomAvatar = Random.nextInt(1, AvatarSize)
            add(
                PhoneCallListImage(
                    "${HttpUrlBackground}${SeaUrl}_$i.webp", "${HttpUrlAvatar}${randomAvatar}.webp",
                    "${HttpUrlButtonCall}${randomButton}B.webp",
                    "${HttpUrlButtonCall}${randomButton}A.webp"
                )
            )
        }
    }
}

fun CreateListButtonCall() {
    ListButtonCall = mutableListOf<ListCallButton>().apply {
        for (i in 1..ButtonCallSize) {
            add(
                ListCallButton(
                    "${HttpUrlButtonCall}${i}B.webp",
                    "${HttpUrlButtonCall}${i}A.webp"
                )
            )
        }
    }
}

fun CreateListButtonAll() {
    ListButtonAll = mutableListOf<ListCallButton>().apply {
        for (i in 1..ButtonAllSize) {
            add(
                ListCallButton(
                    "${HttpUrlButtonAll}${i}B.webp",
                    "${HttpUrlButtonAll}${i}A.webp"
                )
            )
        }
    }
}

fun CreateListAvatar() {
    ListAvatar = mutableListOf<ListAvatar>().apply {
        for (i in 1..AvatarSize) add(ListAvatar("${HttpUrlAvatar}${i}.webp"))
    }
}
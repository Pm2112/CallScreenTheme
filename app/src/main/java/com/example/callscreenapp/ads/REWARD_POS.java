package com.example.callscreenapp.ads;

public enum REWARD_POS{
    Chat_PopupUpgradeYourRelationship_Ignore(-1),
    Home_ShopCoins_ID1(0),
    Chat_ShopCoins_ID2(1),
    Chat_PopupCongrats_ID3(2),
    AISETUP_ShopCoins_ID4(3);

    public int toID = 0;
    REWARD_POS(int id) {
        toID = id;
    }
}
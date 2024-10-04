package com.example.callscreenapp.ads;

public enum InterstitialAd_Key_Count {
    Home_Count_Ads("Home_Count_Ads"),
    Create_Invoice_Count_Ads("Create_Invoice_Count_Ads"),
    Preview_Invoice_Count_Ads("Preview_Invoice_Count_Ads"),
    Bottom_Navigation_Count_Ads("Bottom_Navigation_Count_Ads");

    public String toKey = "";
    InterstitialAd_Key_Count(String id) {
        toKey = id;
    }
}

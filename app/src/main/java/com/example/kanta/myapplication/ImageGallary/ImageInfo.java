package com.example.kanta.myapplication.ImageGallary;

public class ImageInfo {
    private String standard;       // 標準解像度の画像URL
    private String thumbnail;      // サムネイル画像のURL

    public String getThumbnail(){ return this.thumbnail; }
    public String getStandard(){ return this.standard; }
    public void setStandard(String standard){ this.standard = standard; }
    public void setThumbnail(String thumbnail){ this.thumbnail = thumbnail; }
}

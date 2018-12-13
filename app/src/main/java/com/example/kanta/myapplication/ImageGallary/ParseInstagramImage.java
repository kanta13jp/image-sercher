package com.example.kanta.myapplication.ImageGallary;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.kanta.myapplication.util.ParseJson;

import java.util.Iterator;

public class ParseInstagramImage extends ParseJson {

    private ImageInfoList image_list = null;

    public ParseInstagramImage(ImageInfoList image_list) {
        super();
        this.image_list = image_list;
    }

    // レスポンスの解析
    @Override
    public void loadJson(String str) {
        Log.e(this.getClass().getSimpleName(),"loadJson()");
        JsonNode root = getJsonNode(str);
        if (root != null){
            Log.e(this.getClass().getSimpleName(),"loadJson()　stat = " + root.path("stat").asText());
            if(root.path("stat").asText().equals("ok"))
            {
               // 次のURLを取得
                this.image_list.setNext_url(root.path("info").path("photo").asText());
                Iterator<JsonNode> ite = root.path("info").path("photo").elements();
                while (ite.hasNext()) {
                    JsonNode j = ite.next();
                    // 画像情報（URL）をリストに追加
                    this.image_list.add(j.path("thumbnail_image_url").asText(),
                            j.path("original_image_url").asText());
                    Log.e(this.getClass().getSimpleName(), "loadJson()　thumbnail_image_url = " + j.path("thumbnail_image_url").asText());
                    Log.e(this.getClass().getSimpleName(), "loadJson()　original_image_url = " + j.path("original_image_url").asText());
                }
            }
        }
    }
}

package com.example.kanta.myapplication.ImageGallary;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<ImageInfo> urls;// 画像情報のリスト
    private Context context;

    public GridViewAdapter(Context context, List<ImageInfo> urls) {
        this.urls = urls;
        this.context = context;
    }

    @Override
    public int getCount() {
        // グリッドビューには、intの最大を設定する
        return 30;
    }

    @Override
    public Object getItem(int position) {
        // サムネイル画像のURLを返す
        return (urls.size() == 0) ? null : urls.get(position % urls.size())
                .getThumbnail();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SquaredImageView view = (SquaredImageView) convertView;
        // ビューの作成が初めてなら
        if (view == null) {
            view = new SquaredImageView(context);
        }
        // サムネイル画像のURLを取得する
        String url = (String) getItem(position);

        // ビューに表示する画像のURLを設定する
        Picasso.get().load(url).into(view);
        return view;
    }

    // 標準サイズの画像を設定する
    public void setStandardImage(int position, ImageView view) {
        if (0 < urls.size()) {
            String url = urls.get(position % urls.size()).getStandard();
            Picasso.get().load(url).into(view);
        }

    }
}

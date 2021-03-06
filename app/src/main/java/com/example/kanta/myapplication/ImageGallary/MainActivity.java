package com.example.kanta.myapplication.ImageGallary;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.kanta.myapplication.R;
import com.example.kanta.myapplication.util.HttpAsyncLoader;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.kanta.myapplication.MESSAGE";
    private InstagramFragment instagramFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
        }
    }

    public static class InstagramFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, SwipeRefreshLayout.OnRefreshListener {

        private SwipeRefreshLayout swipeRefreshLayout = null;
        private EditText editText = null;
        private String keyword = null;
        // Instagram URL保持クラス
        private ImageInfoList image_list = null;
        // Instagram API解析クラス
        private ParseInstagramImage parse = null;

        private GridViewAdapter grid_view_adapter = null;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setRetainInstance(true);
            this.editText = getActivity().findViewById(R.id.editText);
            this.keyword = this.editText.getText().toString();
            this.image_list = new ImageInfoList("https://api.photozou.jp/rest/search_public.json?limit=30&keyword="+keyword);
            this.parse = new ParseInstagramImage(this.image_list);
            this.swipeRefreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.SwipeRefreshLayout);
            this.swipeRefreshLayout.setOnRefreshListener(InstagramFragment.this);

            // プログレスアニメーションの色指定
            this.swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            GridView gv = (GridView)getView().findViewById(R.id.gridView);

            // カラム数を設定する（縦横向きに応じて値を変える）
            gv.setNumColumns(getResources().getInteger(R.integer.num));

            final ImageView im =  (ImageView)getView().findViewById(R.id.imageView);

            gv.setOnItemClickListener(
                    // グリッドビューのクリックリスナー
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,	View view, int position, long id) {
                            grid_view_adapter.setStandardImage(position,im);
                            im.setVisibility(View.VISIBLE);	// 拡大画面を表示する
                        }
                    });

            ((ImageView)getView().findViewById(R.id.imageView)).setOnClickListener(
                    // 拡大表示画面のクリックリスナー
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            im.setVisibility(View.INVISIBLE); // 拡大画面を非表示にする
                        }
                    });

            // 初回の起動時
            if ( this.grid_view_adapter == null ) {
                this.swipeRefreshLayout.setRefreshing(true); // アニメーション開始
                onRefresh(); // 更新処理
            }
            // 画面が回転されたとき
            else {
                setAdapter(gv);
            }
        }

        // loaderの開始
        private void startLoader(int id) {
            getLoaderManager().restartLoader(id, null, InstagramFragment.this);
        }

        // グリッドビューアダプターを作成して、グリッドビューに関連づける
        private void setAdapter(View view) {
            this.grid_view_adapter = new GridViewAdapter(getActivity(),this.image_list.getImageinfo());
            ((GridView)view).setAdapter(this.grid_view_adapter);
        }

        // 取得データの更新
        @Override
        public void onRefresh() {
            Log.e(this.getClass().getSimpleName(),"onRefresh() start");
            this.image_list.clear();	// 画像リストをクリアstartLoader(0);
            startLoader(0);        	// ローダーの起動
        }

        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            Log.e(this.getClass().getSimpleName(),"onCreateLoader() start");
            this.keyword = this.editText.getText().toString();
            this.image_list = new ImageInfoList("https://api.photozou.jp/rest/search_public.json?limit=30&keyword="+keyword);
            this.parse = new ParseInstagramImage(this.image_list);
            HttpAsyncLoader loader = new HttpAsyncLoader(getActivity(), this.image_list.getNext_url());
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            Log.e(this.getClass().getSimpleName(), "onLoadFinished() start");
            if (data == null){
                data = "{\"stat\":\"fail\",\"err\":[{\"code\":\"ERROR_UNKNOWN\",\"msg\":\"Keyword is too short\"}]}";
            }
            this.parse.loadJson(data); // APIのレスポンスを解析する

            // アダプタをビューに関連づける
            setAdapter(getView().findViewById(R.id.gridView));

            this.grid_view_adapter.notifyDataSetChanged(); // 表示の更新
            this.swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {
        }

    }

    /** Called when the user taps the Search button **/
    public void sendMessage(View view){
        // Do something in response to button
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        if(message.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("please input keyword!")
                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // ボタンをクリックしたときの動作
                        }
                    });
            builder.show();
        } else {
            if (instagramFragment == null) {
                instagramFragment = new InstagramFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.container, instagramFragment).commit();
            } else {
                getFragmentManager().beginTransaction()
                        .remove(instagramFragment);
                instagramFragment = new InstagramFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.container, instagramFragment).commit();
            }
        }
    }
}


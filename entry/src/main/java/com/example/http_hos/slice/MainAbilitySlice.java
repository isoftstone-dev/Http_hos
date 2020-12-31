package com.example.http_hos.slice;

import com.example.http_hos.ResourceTable;
import com.example.httplibrary.utils.AsyncHttpClient;
import com.example.httplibrary.utils.JsonHttpResponseHandler;
import com.example.httplibrary.utils.RequestHandle;
import com.example.httplibrary.utils.RequestParams;
import com.google.gson.JsonObject;
import cz.msebera.android.httpclient.Header;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbilitySlice extends AbilitySlice {
    private HiLogLabel label=new HiLogLabel(HiLog.ERROR,00*00101,"test-async-http");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        Text tvResult=(Text) findComponentById(ResourceTable.Id_text);
        String url="http://apis.juhe.cn/simpleWeather/query";
        String key="32becf485f7f174d4385957b62f28f61";
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("city","西安");
        params.put("key",key);
        RequestHandle requestHandle = client.get(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                HiLog.error(label,"zel-onSuccess:"+responseString,responseString);
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        //更新UI
                        tvResult.setText(responseString);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
                super.onFailure(statusCode, headers, errorResponse, throwable);
                HiLog.error(label,"zel-onFailure:"+errorResponse,errorResponse);

            }
        });
    };

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}

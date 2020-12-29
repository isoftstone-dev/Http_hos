package com.example.http_hos.slice;

import com.example.http_hos.ResourceTable;
import com.example.httplibrary.utils.AsyncHttpClient;
import com.example.httplibrary.utils.JsonHttpResponseHandler;
import com.example.httplibrary.utils.RequestHandle;
import com.google.gson.JsonObject;
import cz.msebera.android.httpclient.Header;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        AsyncHttpClient client=new AsyncHttpClient();
        RequestHandle requestHandle = client.get("", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JsonObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JsonObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

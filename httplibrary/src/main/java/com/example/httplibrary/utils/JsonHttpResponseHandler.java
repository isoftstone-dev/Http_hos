/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://github.com/android-async-http/android-async-http

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.example.httplibrary.utils;



import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}, with
 * automatic parsing into a {@link JsonObject} or {@link JsonArray}. <p>&nbsp;</p> This class is
 * designed to be passed to get, post, put and delete requests with the  or  methods anonymously overridden. <p>&nbsp;</p>
 * Additionally, you can override the other event methods from the parent class.
 */
public class JsonHttpResponseHandler extends TextHttpResponseHandler {

    private static final String LOG_TAG = "JsonHttpRH";


    private boolean useRFC5179CompatibilityMode = true;

    /**
     * Creates new JsonHttpResponseHandler, with JSON String encoding UTF-8
     */
    public JsonHttpResponseHandler() {
        super(DEFAULT_CHARSET);
    }

    /**
     * Creates new JsonHttpResponseHandler with given JSON String encoding
     *
     * @param encoding String encoding to be used when parsing JSON
     */
    public JsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    /**
     * Creates new JsonHttpResponseHandler with JSON String encoding UTF-8 and given RFC5179CompatibilityMode
     *
     * @param useRFC5179CompatibilityMode Boolean mode to use RFC5179 or latest
     */
    public JsonHttpResponseHandler(boolean useRFC5179CompatibilityMode) {
        super(DEFAULT_CHARSET);
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }

    /**
     * Creates new JsonHttpResponseHandler with given JSON String encoding and RFC5179CompatibilityMode
     *
     * @param encoding                    String encoding to be used when parsing JSON
     * @param useRFC5179CompatibilityMode Boolean mode to use RFC5179 or latest
     */
    public JsonHttpResponseHandler(String encoding, boolean useRFC5179CompatibilityMode) {
        super(encoding);
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }

    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, JsonObject response) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], JSONObject) was not overriden, but callback was received");
    }

    /**
     * Returns when request succeeds
     *
     * @param statusCode http response status line
     * @param headers    response headers if any
     * @param response   parsed response if any
     */
    public void onSuccess(int statusCode, Header[] headers, JsonArray response) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
    }

    /**
     * Returns when request failed
     *
     * @param statusCode    http response status line
     * @param headers       response headers if any
     * @param throwable     throwable describing the way request failed
     * @param errorResponse parsed response if any
     */
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JsonObject errorResponse) {
        AsyncHttpClient.log.w(LOG_TAG, "onFailure(int, Header[], Throwable, JSONObject) was not overriden, but callback was received", throwable);
    }

    /**
     * Returns when request failed
     *
     * @param statusCode    http response status line
     * @param headers       response headers if any
     * @param throwable     throwable describing the way request failed
     * @param errorResponse parsed response if any
     */
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JsonArray errorResponse) {
        AsyncHttpClient.log.w(LOG_TAG, "onFailure(int, Header[], Throwable, JSONArray) was not overriden, but callback was received", throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        AsyncHttpClient.log.w(LOG_TAG, "onFailure(int, Header[], String, Throwable) was not overriden, but callback was received", throwable);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], String) was not overriden, but callback was received");
    }

    @Override
    public final void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes) {
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            Runnable parser = new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBytes);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                // In RFC5179 a null value is not a valid JSON
                                if (!useRFC5179CompatibilityMode && jsonResponse == null) {
                                    onSuccess(statusCode, headers, (String) null);
                                } else if (jsonResponse instanceof JsonObject) {
                                    onSuccess(statusCode, headers, (JsonObject) jsonResponse);
                                } else if (jsonResponse instanceof JsonArray) {
                                    onSuccess(statusCode, headers, (JsonArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    // In RFC5179 a simple string value is not a valid JSON
                                    if (useRFC5179CompatibilityMode) {
                                        AsyncHttpClient.log.e(LOG_TAG, "失败1");
//                                        onFailure(statusCode, headers, (String) jsonResponse, new JsonParseException("Response cannot be parsed as JSON data"));
                                        onSuccess(statusCode, headers, (String) jsonResponse);
                                    } else {
                                        onSuccess(statusCode, headers, (String) jsonResponse);
                                    }
                                } else {
                                    AsyncHttpClient.log.e(LOG_TAG, "失败2");
                                    onFailure(statusCode, headers, new JsonParseException("Unexpected response type " + jsonResponse.getClass().getName()), (JsonObject) null);
//                                       onSuccess(statusCode,headers,(String) jsonResponse);
                                }
                            }
                        });
                    } catch (final Exception ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, ex, (JsonObject) null);
                            }
                        });
                    }
                }
            };
            if (!getUseSynchronousMode() && !getUsePoolThread()) {
                new Thread(parser).start();
            } else {
                // In synchronous mode everything should be run on one thread
                parser.run();
            }
        } else {
            onSuccess(statusCode, headers, new JsonObject());
        }
    }

    @Override
    public final void onFailure(final int statusCode, final Header[] headers, final byte[] responseBytes, final Throwable throwable) {
        if (responseBytes != null) {
            Runnable parser = new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object jsonResponse = parseResponse(responseBytes);
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                // In RFC5179 a null value is not a valid JSON
                                if (!useRFC5179CompatibilityMode && jsonResponse == null) {
                                    onFailure(statusCode, headers, (String) null, throwable);
                                } else if (jsonResponse instanceof JsonObject) {
                                    onFailure(statusCode, headers, throwable, (JsonObject) jsonResponse);
                                } else if (jsonResponse instanceof JsonArray) {
                                    onFailure(statusCode, headers, throwable, (JsonArray) jsonResponse);
                                } else if (jsonResponse instanceof String) {
                                    onFailure(statusCode, headers, (String) jsonResponse, throwable);
                                } else {
                                    onFailure(statusCode, headers, new JsonParseException("Unexpected response type " + jsonResponse.getClass().getName()), (JsonObject) null);
                                }
                            }
                        });

                    } catch (final Exception ex) {
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                onFailure(statusCode, headers, ex, (JsonObject) null);
                            }
                        });

                    }
                }
            };
            if (!getUseSynchronousMode() && !getUsePoolThread()) {
                new Thread(parser).start();
            } else {
                // In synchronous mode everything should be run on one thread
                parser.run();
            }
        } else {
            AsyncHttpClient.log.v(LOG_TAG, "response body is null, calling onFailure(Throwable, JSONObject)");
            onFailure(statusCode, headers, throwable, (JsonObject) null);
        }
    }


    protected Object parseResponse(byte[] responseBody) throws JsonParseException {
        if (null == responseBody)
            return null;
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If JSON is not valid this will return null
        String jsonString = getResponseString(responseBody, getCharset());
        result=jsonString;
        AsyncHttpClient.log.v(LOG_TAG, "zel-ResponseString:"+jsonString);

//        if (jsonString != null) {
//            jsonString = jsonString.trim();
//            if (useRFC5179CompatibilityMode) {
//                if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
//                    result = new Gson().toJson(jsonString);
//                }
//            } else {
//                // Check if the string is an JSONObject style {} or JSONArray style []
//                // If not we consider this as a string
//                if ((jsonString.startsWith("{") && jsonString.endsWith("}"))
//                        || jsonString.startsWith("[") && jsonString.endsWith("]")) {
//                    result = new JSONTokener(jsonString).nextValue();
//                    result = new Gson().toJson(jsonString);
//                }
//                // Check if this is a String "my String value" and remove quote
//                // Other value type (numerical, boolean) should be without quote
//                else if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
//                    result = jsonString.substring(1, jsonString.length() - 1);
//                }
//            }
//        }
        if (result == null) {
            result = jsonString;
        }
        return result;
    }

    public boolean isUseRFC5179CompatibilityMode() {
        return useRFC5179CompatibilityMode;
    }

    public void setUseRFC5179CompatibilityMode(boolean useRFC5179CompatibilityMode) {
        this.useRFC5179CompatibilityMode = useRFC5179CompatibilityMode;
    }

}

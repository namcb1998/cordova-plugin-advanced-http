/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset.cordovahttp;

import android.webkit.MimeTypeMap;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class CordovaHttpUploadBinary extends CordovaHttp implements Runnable {
  private String filePath;
  private JSONObject headers;
  private  String urlPost;
  private  String cookie;
  private  String useragent;
  private  String param;
  private  String name;

  public CordovaHttpUploadBinary(String urlString, Object params, JSONObject headers, String filePath, int timeout, CallbackContext callbackContext) {
      super(urlString, params, headers, timeout, callbackContext);
      this.filePath = filePath;
      this.headers = headers;
      this.urlPost = urlString;
    try {
      this.cookie = this.headers.getString("cookie");
      this.useragent = this.headers.getString("user-agent");
      this.param = this.headers.getString("x-instagram-rupload-params");
      this.name = this.headers.getString("x-entity-name");
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

@Override
public void run() {
  try {
    OkHttpClient client = new OkHttpClient().newBuilder()
      .build();
    MediaType mediaType = MediaType.parse("image/png");
    File file = new File(this.filePath);
    int size = (int) file.length();

    byte[] bytes = new byte[size];
    try {
      BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
      buf.read(bytes, 0, bytes.length);
      buf.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    RequestBody body = RequestBody.create(mediaType, bytes);
    Request request = new Request.Builder()
      .url(this.urlPost)
      .method("POST", body)
      .addHeader("accept", "*/*")
      .addHeader("accept-encoding", "gzip, deflate, br")
      .addHeader("accept-language", "en-US,en;q=0.9,vi;q=0.8")
      .addHeader("cookie", this.cookie)
      .addHeader("offset", "0")
      .addHeader("origin", "https://www.instagram.com")
      .addHeader("referer", "https://www.instagram.com/")
      .addHeader("sec-fetch-dest", "empty")
      .addHeader("sec-fetch-mode", "cors")
      .addHeader("sec-fetch-site", "same-site")
      .addHeader("user-agent", this.useragent)
      .addHeader("x-entity-length", String.valueOf(size))
      .addHeader("x-entity-name", this.name)
      .addHeader("x-entity-type", "image/jpeg")
      .addHeader("x-ig-app-id", "1217981644879628")
      .addHeader("x-instagram-ajax", "5714c4dcafaf")
      .addHeader("x-instagram-rupload-params", this.param)
      .addHeader("Content-Type", "image/png")
      .build();
    Response response = client.newCall(request).execute();
    this.returnResponseUploadBinary(response);
  } catch (HttpRequestException e) {
    this.handleHttpRequestException(e);
  } catch (Exception e) {
    this.respondWithError(e.getMessage());
  }
}
}

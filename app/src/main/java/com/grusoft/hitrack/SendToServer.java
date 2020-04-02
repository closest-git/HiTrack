package com.grusoft.hitrack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/***
 * https://stackoverflow.com/questions/35390928/how-to-send-json-object-to-the-server-from-my-android-app
 * https://stuff.mit.edu/afs/sipb/project/android/docs/training/displaying-bitmaps/process-bitmap.html#async-task
 */
public class SendToServer extends AsyncTask<Bitmap, Void, Bitmap> {
    private static Gson gson = new GsonBuilder().create();
    public interface AsyncResponse {    //https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
        void processFinish(Bitmap output);
    }
    public AsyncResponse delegate = null;

    public SendToServer(AsyncResponse delegate){
        this.delegate = delegate;
    }

    protected String core_0(String... params) {
        String data = "";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(params[0]).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            /*http://geekonjava.blogspot.com/2014/03/upload-image-on-server-in-android-using.html
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
            */
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes("PostData=" + params[1]);
            wr.flush();
            wr.close();

            InputStream in = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return data;
    }

    /*
        https://blog.csdn.net/zhangcongyi420/article/details/90247271
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected Bitmap core_1(Bitmap bmp, String... params) {
        String url = params[0];
        //return bmp;

        try {
            //byte[] bytes1 = FileUtil.readFileByBytes("D:\\pic\\22.jpg");
            ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos0);
            byte[] imageBytes0 = baos0.toByteArray();
            //String image1 = Base64Util.encode(imageBytes0);
            String image1 = Base64.encodeToString(imageBytes0, Base64.DEFAULT);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image_64", image1);
            map.put("uuid", "314161");
            map.put("user_id", "user1");
            map.put("user_info", "abc");
            map.put("liveness_control", "NORMAL");
            //map.put("image_type", "FACE_TOKEN");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            //String param = GsonUtils.toJson(map);
            String param = gson.toJson(map);
            // 客户端可自行缓存，过期后重新获取。
            String result = HttpUtil.post(url, "accessToken", "application/json", param);
            JSONObject jobj = new JSONObject(result);
            String result_64 = jobj.getString("result_64");
            jobj.put("result_64","");
            System.out.println(jobj);
            byte[] imageBytes = Base64.decode(result_64, Base64.DEFAULT);   //https://stackoverflow.com/questions/7360403/base-64-encode-and-decode-example-code
            //https://stackoverflow.com/questions/38639436/how-to-convert-bytebuffer-into-image-in-android
            Bitmap bmp_1=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
            //String text = new String(data, StandardCharsets.UTF_8);
            return bmp_1;
        } catch (IOException e) {
            e.printStackTrace();        return null;
        } catch (Exception e) {
            e.printStackTrace();        return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Bitmap doInBackground(Bitmap... bmps) {
        //String result = core_0(params);
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.people_1);
        Bitmap result = core_1(bmps[0],"http://121.37.175.1:8080/card_v0/");
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
        Log.e("TAG", "result"); // this is expecting a response code to be sent from your server upon receiving the POST data
    }
}

/**
 * Json工具类.

 public class GsonUtils {
 private static Gson gson = new GsonBuilder().create();

 public static String toJson(Object value) {
 return gson.toJson(value);
 }

 public static <T> T fromJson(String json, Class<T> classOfT) throws JsonParseException {
 return gson.fromJson(json, classOfT);
 }

 public static <T> T fromJson(String json, Type typeOfT) throws JsonParseException {
 return (T) gson.fromJson(json, typeOfT);
 }
 }
 */
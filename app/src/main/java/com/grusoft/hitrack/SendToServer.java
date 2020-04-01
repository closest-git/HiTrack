package com.grusoft.hitrack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/***
 * https://stackoverflow.com/questions/35390928/how-to-send-json-object-to-the-server-from-my-android-app
 */
public class SendToServer extends AsyncTask<Bitmap, Void, String> {
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

    protected String core_1(Bitmap bmp,String... params) {
        String url = params[0];
        try {
            //byte[] bytes1 = FileUtil.readFileByBytes("D:\\pic\\22.jpg");
            ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos0);
            byte[] imageBytes0 = baos0.toByteArray();
            String image1 = Base64Util.encode(imageBytes0);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image_64", image1);
            map.put("uuid", "314160");
            map.put("user_id", "user1");
            map.put("user_info", "abc");
            map.put("liveness_control", "NORMAL");
            //map.put("image_type", "FACE_TOKEN");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);
            // 客户端可自行缓存，过期后重新获取。
            String result = HttpUtil.post(url, "accessToken", "application/json", param);
            System.out.println(result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return "";
        }
    }

    @Override
    protected String doInBackground(Bitmap... bmps) {
        //String result = core_0(params);
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.people_1);
        String result = core_1(bmps[0],"http://121.37.175.1:8080/card_v0/");
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
    }
}

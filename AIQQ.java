package utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class AIQQ {

    int APP_ID = 2132976182;
    String APP_KEY = "R3tUXwwkzsVY6LU6";

    public String ocr_generalocr_qq(String image_path) throws Exception {
        // 秒级时间戳
        int time_stamp = getCurrentTime();

        String nonce_str = randomString();

        HashMap params = new HashMap();
        params.put("app_id", APP_ID);
        params.put("time_stamp", time_stamp);
        params.put("nonce_str", nonce_str);
        params.put("image", image_to_base64(image_path));

        String sign = getReqSign(params, APP_KEY);

        params.put("sign", sign);

        sendPost("https://api.ai.qq.com/fcgi-bin/ocr/ocr_generalocr", params);

        return nonce_str;
    }

    // https://mkyong.com/java/how-to-send-http-request-getpost-in-java/
    public void sendPost(String str, HashMap map) throws Exception {
        //String urlParameters  = "param1=data1&param2=data2&param3=data3";

        String urlParameters = getDataString(map, true);
        System.out.println("urlParameters: " + urlParameters);

        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = str;
        URL url = new URL( request );
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "UTF-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
        conn.setUseCaches(false);
        try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write( postData );
        }

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request not worked");
        }
    }

    private String getDataString(HashMap<String, String> params, boolean is_encode) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Set set = params.keySet();
        for(Object key : set){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(key + "=");
            if(is_encode) {
                result.append(URLEncoder.encode(String.valueOf(params.get(key)), "UTF-8"));
            }else{
                result.append(String.valueOf(params.get(key)));
            }
        }

        return result.toString();
    }

    // java中随机生成字符串的方法（三种）https://www.cnblogs.com/jpfss/p/9772019.html
    public String randomString(int count){
        return RandomStringUtils.randomAlphanumeric(count);
    }

    public String randomString(){
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public int getCurrentTime(){
        return (int) (System.currentTimeMillis() / 1000);
    }

    //将<key, value>请求参数对按key进行字典升序排序，得到有序的参数对列表N
    //将列表N中的参数对按URL键值对的格式拼接成字符串，得到字符串T（如：key1=value1&key2=value2），URL键值拼接过程value部分需要URL编码，URL编码算法用大写字母，例如%E8，而不是小写%e8
    //将应用密钥以app_key为键名，组成URL键值拼接到字符串T末尾，得到字符串S（如：key1=value1&key2=value2&app_key=密钥)
    //对字符串S进行MD5运算，将得到的MD5值所有字符转换成大写，得到接口请求签名
    public String getReqSign(HashMap params, String app_key) throws Exception {
        Set set = params.keySet();
        Object[] arr=set.toArray();
        Arrays.sort(arr);

        LinkedHashMap<String, String> hm = new LinkedHashMap();
        for(Object key: arr){
            hm.put(key.toString(), params.get(key).toString());
        }

        hm.put("app_key", app_key);

        String urlParameters = getDataString(hm, true);

        System.out.println("ordered urlParameters: " + urlParameters);

        return getMD5(urlParameters).toUpperCase();
    }

    public String getMD5(String str) throws NoSuchAlgorithmException {
        // 生成一个MD5加密计算摘要
        MessageDigest md = MessageDigest.getInstance("MD5");
        // 计算md5函数
        md.update(str.getBytes());
        // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值

        return new BigInteger(1, md.digest()).toString(16);
    }

    public String image_to_base64(String path) throws IOException {
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BASE64Encoder encoder = new BASE64Encoder();

        //return "0";
        return encoder.encode(data);
    }

    public String getPath(String relative_path) {
        File f = new File(".");
        String absolutePath = f.getAbsolutePath();

        String file = absolutePath + relative_path;

        return file;
    }
}

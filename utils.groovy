package utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import static org.junit.Assert.assertTrue

class utils {
    //通过url获取返回的json对象
    def search_by_url(String url) {
        //httpclient模拟客户端,比URLConnection简单灵活
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(url);
        String result = "";
        boolean is_finish = false;
        int i = 0;
        while(!is_finish && i < 3) {
            System.out.println("开始搜寻......: " + url);
            try {
                //调用HttpClient对象的execute(HttpUriRequest request)发送请求，该方法返回一个HttpResponse。
                HttpResponse response = client.execute(httpget);

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result = result + line;
                }
                rd.close();
                is_finish = true;
            } catch (ClientProtocolException e) {
                i = i + 1;
                e.printStackTrace();
            } catch (IOException e) {
                i = i + 1;
                e.printStackTrace();
            }
        }

        JSONObject jsonobj = JSONObject.parseObject(result);
        return jsonobj;
    }

    def userIds(){
        ArrayList<String> fileContents = read_from_file("src/test/java/utils/userIds.txt")
        return fileContents
    }

    def userIds(int num){
        ArrayList<String> fileContents = read_from_file("src/test/java/utils/userIds.txt")
        return fileContents[0..num-1]
    }

    //输入一个文件,对文件进行读取,读取每一行数据,将其加入到key_list,返回key_list
    def ArrayList<String> read_from_file(String file_path) throws Exception{
        ArrayList<String> key_list = new ArrayList<String>();

        File file = new File(file_path);
        if(file.isFile() && file.exists()) {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), "utf-8");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                //String[] arr = lineTxt.split("\n");
                key_list.add(lineTxt);
            }
            read.close();
        }
        return key_list;
    }

    //推荐商品不重复 入参JSONArray版本
    def 推荐商品不重复(itemIds){
        Set set = new HashSet(itemIds);
        ArrayList<String> array = new ArrayList<>();
        for(int i = 0; i < itemIds.size(); i++){
            if(array.contains(itemIds.get(i))){
                System.out.println("重复id: " + itemIds.get(i));
            }else {
                array.add(itemIds.get(i).toString());
            }
        }

        if(itemIds.size() == set.size()){
            println("数量匹配")
            return true
        }else{
            println("数量不匹配")
            return false
        }
    }

    def 按预期排序(JSONArray numbers, String type) {
       double pre_number = numbers.get(0)
       for(int i = 0; i < numbers.size(); i++){
           if(type == "asc") {
               assertTrue(numbers.get(i) >= pre_number);
           }else{
               assertTrue(numbers.get(i) <= pre_number);
           }
           pre_number = numbers.get(i)
       }
       return true;
    }

    // 编辑距离的算法是首先由俄国科学家Levenshtein提出的，故又叫Levenshtein Distance
    def 判断中文字符串相似度(String s1, String s2){
        //使用递归算法
        if(s1.length() == 0){
            return s2.length()
        }

        if(s2.length() == 0){
            return s1.length()
        }

        String s1_first = s1.charAt(0)
        String s2_first = s2.charAt(0)

        String next_s1 = s1.substring(1, s1.length())
        String next_s2 = s2.substring(1, s2.length())
        if(s1_first == s2_first){
            return 判断中文字符串相似度(next_s1, next_s2)
        }else{
            int len1 = 判断中文字符串相似度(next_s1, s2) + 1
            int len2 = 判断中文字符串相似度(s1, next_s2) + 1
            int len3 = 判断中文字符串相似度(next_s1, next_s2) + 1

            return Math.min(Math.min(len1, len2), len3)
        }
    }

    def minDistance(String str1, String str2){
        // 计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        // 建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        // 赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        // 计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {

//                System.out.println("i = " + i + " j = " + j + " str1 = "
//                        + str1.charAt(i - 1) + " str2 = " + str2.charAt(j - 1));
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 取三个值中最小的
                dif[i][j] = Math.min(Math.min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1), dif[i - 1][j] + 1);

//                System.out.println("i = " + i + ", j = " + j + ", dif[i][j] = " + dif[i][j]);
            }
        }
        //System.out.println("字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
        // 取数组右下角的值，同样不同位置代表不同字符串的比较
        //System.out.println("差异步骤：" + dif[len1][len2]);
        // return dif[len1][len2];
        // 计算相似度
        float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        return similarity
        //System.out.println("相似度：" + similarity);
    }

    def getPubItemInfo(String itemId){
        JSONObject jn = new JSONObject();
        jn.put("itemID", itemId);
        String url = "http://weidian.com/wd/item/getPubInfo?param=" + URLEncoder.encode(jn.toString(), "utf8");
        JSONObject response = search_by_url(url);
        return  response;
    }
}

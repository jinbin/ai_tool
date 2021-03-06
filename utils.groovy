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

    def getPubItemInfo(String itemId){
        JSONObject jn = new JSONObject();
        jn.put("itemID", itemId);
        String url = "http://weidian.com/wd/item/getPubInfo?param=" + URLEncoder.encode(jn.toString(), "utf8");
        JSONObject response = search_by_url(url);
        return  response;
    }
}

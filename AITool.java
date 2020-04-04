package utils;

import com.robrua.nlp.bert.Bert;

public class AITool {
    public int minDistance_recursive(String str1, String str2){
        // 计算两个字符串的长度
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
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 取三个值中最小的
                dif[i][j] = Math.min(Math.min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1), dif[i - 1][j] + 1);
            }
        }
        return dif[len1][len2];
    }

    public float similarity_minDistance(String str1, String str2){
        int min_len = minDistance(str1, str2);
        // 计算相似度
        float similarity = 1 - (float) min_len / Math.max(str1.length(), str2.length());
        return similarity;
    }

    public int minDistance(String s1, String s2){
        if(s1.length() == 0){
            return s2.length();
        }

        if(s2.length() == 0){
            return s1.length();
        }

        int[][] matrix = new int[s1.length() + 1][s2.length() + 1];

        for(int i = 0; i < s1.length() + 1; i++){
            for(int j = 0;j < s2.length() + 1; j++){
                matrix[i][j] = -1;
            }
        }

        // forget to add 1
        for(int j = 0; j < s2.length() + 1; j++){
            matrix[0][j] = j;
        }

        for(int i = 0; i < s1.length() + 1; i++){
            matrix[i][0] = i;
        }

        for(int i = 0; i < s1.length() + 1; i++){
            for(int j = 0; j < s2.length() + 1; j++){
                // 若matrix[i][j] = -1，则表示未计算距离
                if(matrix[i][j] < 0){

                    int tmp1 = matrix[i-1][j-1];
                    if(s1.charAt(i-1) != s2.charAt(j-1)){
                        tmp1 = tmp1 + 1;
                    }
                    matrix[i][j] = Math.min(Math.min(matrix[i-1][j], matrix[i][j-1]), tmp1);
                }
            }
        }

        return matrix[s1.length()][s2.length()];
    }

    // cos x = a * b / |a| * |b|
    // 0.9723421414323189
    // 0.9723421335220337
    public double similarity_bert(String s1, String s2) throws Exception {
        String model = "com/robrua/nlp/easy-bert/bert-chinese-L-12-H-768-A-12";
        float[] s1_value = bert(model, s1);
        float[] s2_value = bert(model, s2);

        return vector_cosX(s1_value, s2_value);
    }

    public float[] bert(String model, String s){
        try(Bert bert = Bert.load(model)) {
            // Embed some sequences
            float[] embedding = bert.embedSequence(s);
            // System.out.println(Arrays.toString(embedding));
            return embedding;
        }
    }

    public float[] vector_add(float[] v1, float[] v2) throws Exception {
        if(v1.length != v2.length){
            throw new Exception("Two vectors' length not equal! Please check.");
        }

        float[] result = new float[v1.length];
        for(int i = 0; i < v1.length; i++){
            result[i] = v1[i] + v2[i];
        }

        return result;
    }

    // dot product of v1, v2 = |v1| * |v2| * cosx
    public float vector_dotproduct(float[] v1, float[] v2) throws Exception {
        if(v1.length != v2.length){
            throw new Exception("Two vectors' length not equal! Please check.");
        }

        float result = 0;
        for(int i = 0; i < v1.length; i++){
            result = result + v1[i] * v2[i];
        }

        return result;
    }

    public float vector_length(float[] v1) throws Exception {
        if(v1.length == 0){
            throw new Exception("The vector's length is zero! Please check.");
        }

        float result = 0;
        for(int i = 0; i < v1.length; i++){
            result = result + v1[i] * v1[i];
        }

        return (float)Math.sqrt(result);
    }

    public float vector_cosX(float[] v1, float[] v2) throws Exception {
        if(v1.length != v2.length){
            throw new Exception("Two vectors' length not equal! Please check.");
        }

        float cosX = vector_dotproduct(v1, v2) / (vector_length(v1) * vector_length(v2));

        return cosX;
    }
}

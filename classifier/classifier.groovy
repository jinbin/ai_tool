package utils.classifier

import com.alibaba.fastjson.JSONObject
import groovy.json.JsonSlurper

/**
 * Created by jinbin on 2018/7/27.
 */

class classifier {

    def classifiers = []
    def classifiers_to_name = [:]
    def categories = [:]

    def Classifier(){
        categories["others"] = 0
    }

    //添加分类器
//    def add_classifier(...closure){
//        for(Object c : closure){
//            classifiers.add(c)
//            categories[c] = 0
//        }
//    }

    def add_classifier(closure, name){
        classifiers.add(closure)
        classifiers_to_name[closure] = name
        categories[name] = 0
    }

    //进行分类
    def process(input){
        def isOther = true
        for(Closure method: classifiers){
            if(method.call(input)){
                isOther = false
                if(categories[classifiers_to_name[method]]){
                    categories[classifiers_to_name[method]] += 1
                }else{
                    categories[classifiers_to_name[method]] = 1
                }
                //分类互斥
                //continue
            }
        }
        if(isOther){
            println("人肉debug")
            println(categories)
            categories["others"] += 1
        }
    }

    def show(){
        println(categories)
    }

}

//def jsonSlurper = new JsonSlurper()
//def object = jsonSlurper.parseText('[{ "adData":"637657","shopId":"329710232" }, ' +
//        '{ "adData":"637657","shopId":"329710232" }] /* some comment */')
//
////定义分类器
//def class1 = { input ->
//    return false
//}
//
//def class2 = { input ->
//    if(input["adData"].equals("637657")){
//        return true
//    }else{
//        return false
//    }
//}
//
////添加分类器
//def classifier = new Classifier()
//classifier.add_classifier(class1)
//classifier.add_classifier(class2)
//
//for (JSONObject jo : object) {
//    classifier.process(jo)
//}
//
////展示分类结果
//classifier.show()



package utils.test

import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import utils.AIQQ

class AIQQTest extends Specification {

    @Shared
    AIQQ aq = new AIQQ()

    @Ignore
    def "test getReqSign"() {
        given:
        HashMap params = new HashMap()
        params.put("app_id", "xxxxx")
        params.put("time_stamp", 1111111)
        params.put("nonce_str", "xxxddddddd")
        params.put("image", ai.image_to_base64(ai.getPath("/src/data/gushi.png")))

        ai.getReqSign(params, "xxxxx")

        and:
        println("ok")
    }

    def "test ocr_generalocr_qq"() {
        given:
        aq.ocr_generalocr_qq("/Users/jinbin/Desktop/OCR.jpeg")

        and:
        println("ok")
    }
}

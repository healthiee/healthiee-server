package healthiee.rest.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloApiController {

    @GetMapping("/api/v1/hello")
    fun hello(): String {
        return "{\"title\":\"world\"}"
    }

//    @PostMapping("/api/v1/hello")
//    fun postHello(@RequestBody params: HelloCreate): String {
//        if (params.title.isEmpty()) {
//            throw Exception("타이틀이 없다.")
//        }
//        return "Hello World"
//    }
}

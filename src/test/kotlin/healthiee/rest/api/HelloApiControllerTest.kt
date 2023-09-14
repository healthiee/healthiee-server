package healthiee.rest.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest
class HelloApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

//    @Test
//    fun test() {
//        mockMvc.perform(
//            post("/api/v1/hello").contentType(MediaType.APPLICATION_JSON)
//                .content("{\"title\":\"\",\"content\":\"내용입니다.\"}")
//        ).andExpect(status().isOk).andExpect(content().string("Hello World")).andDo(print())
//    }

}
package healthiee.rest.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
class HelloApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun test() {
        mockMvc.perform(
            post("/api/v1/hello").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"content\":\"내용입니다.\"}")
        ).andExpect(status().isOk).andExpect(content().string("Hello World")).andDo(print())
    }

}
package healthiee.rest.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService {

    @Transactional
    fun join() {

    }

}
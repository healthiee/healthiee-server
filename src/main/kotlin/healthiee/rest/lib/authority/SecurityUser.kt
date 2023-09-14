package healthiee.rest.lib.authority

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class SecurityUser(
    username: String,
    password: String,
    authorities: Collection<GrantedAuthority>,
) : User(username, password, authorities)
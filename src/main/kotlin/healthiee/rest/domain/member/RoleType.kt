package healthiee.rest.domain.member

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class RoleType(private val permissions: Set<Permission>) {
    ANONYMOUS(emptySet()),
    MEMBER(
        setOf(
            Permission.MEMBER_READ,
            Permission.MEMBER_WRITE,
            Permission.MEMBER_CREATE,
            Permission.MEMBER_DELETE,
        )
    );

    fun getAuthorities(): List<SimpleGrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority("ROLE_${this.name}")).apply {
            permissions.map { add(SimpleGrantedAuthority(it.permission)) }
        }
}
package healthiee.rest.domain.member.entity

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
    ),
    ADMIN(emptySet());

    fun getAuthorities(): List<SimpleGrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority("ROLE_${this.name}")).apply {
            permissions.map { add(SimpleGrantedAuthority(it.permission)) }
        }
}
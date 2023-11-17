package healthiee.rest.domain.member.entity

enum class Permission(val permission: String) {
    MEMBER_READ("member:read"),
    MEMBER_WRITE("member:write"),
    MEMBER_CREATE("member:create"),
    MEMBER_DELETE("member:delete"),
}
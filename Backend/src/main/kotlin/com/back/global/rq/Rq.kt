package com.back.global.rq

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.security.SecurityUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class Rq(
    private val req: HttpServletRequest,
    private val resp: HttpServletResponse,
    private val memberService: MemberService,
) {

    val actor: Member?
        get() = SecurityContextHolder
            .getContext()
            ?.authentication
            ?.principal
            ?.let {
                if (it is SecurityUser) {
                    val role = it.authorities
                        .map { auth -> auth.authority }
                        .firstOrNull { auth -> auth.startsWith("ROLE_") }
                        ?.removePrefix("ROLE_")
                        ?: "USER"

                    Member(it.id, it.email, it.name, role)
                } else {
                    null
                }
            }

    fun getHeader(name: String, defaultValue: String): String {
        return req.getHeader(name)?.takeIf { it.isNotBlank() } ?: defaultValue
    }

    fun setHeader(name: String, value: String?) {
        value?.takeIf { it.isNotBlank() }
            ?.let { resp.setHeader(name, it) }
    }

    fun getCookieValue(name: String, defaultValue: String): String =
        req.cookies
            ?.firstOrNull { it.name == name }
            ?.value
            ?.takeIf { it.isNotBlank() }
            ?: defaultValue

    fun setCrossDomainCookie(name: String, value: String, maxAge: Int) {
        val cookie = ResponseCookie.from(name, value)
            .path("/")
            .maxAge(maxAge.toLong())
            .secure(true)
            .httpOnly(true)
            .build()

        resp.addHeader("Set-Cookie", cookie.toString())
    }

    fun deleteCrossDomainCookie(name: String) {
        setCrossDomainCookie(name, "", 0)
    }

    fun sendRedirect(url: String) {
        resp.sendRedirect(url)
    }

    val actorFromDb: Member?
        get() {
            val currentActor = actor ?: return null
            return memberService.findById(currentActor.id).orElse(null)
        }
}
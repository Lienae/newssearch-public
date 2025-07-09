package com.tjeoun.newssearch.interceptor;

import com.tjeoun.newssearch.config.principal.PrincipalDetails;
import com.tjeoun.newssearch.dto.AdminLogDTO;
import com.tjeoun.newssearch.entity.AdminLog;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.AdminLogEnum;
import com.tjeoun.newssearch.service.AdminLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.tjeoun.newssearch.util.AdminLogUtils.parseUriToAdminLogAction;

@Component
@RequiredArgsConstructor
public class AdminLogInterceptor implements HandlerInterceptor {
    private final AdminLogService adminLogService;

    @Override
    public void afterCompletion(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final Object handler,
                                 final Exception ex) {
        if(isAdmin()) {
            AdminLogDTO dto = createAdminLogDTO(request.getRequestURI(),getClientIp(request),getAdmin());
            if(dto != null) {
                adminLogService.save(AdminLog.createEntity(dto));
            }
        }
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    private Member getAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof PrincipalDetails principalDetails) {
            return principalDetails.getMember();
        }
        return null;
    }
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
    public static String extractTargetTable(String uri) {
        if (uri.startsWith("/admin/boards/")) return "board";
        else if (uri.startsWith("/admin/news/")) return "news";
        else if (uri.startsWith("/admin/members/")) return "member";
        else return "unknown";
    }
    public static AdminLogDTO createAdminLogDTO(String uri, String ipAddress, Member admin) {
        Map<String, Object> action = parseUriToAdminLogAction(uri);
        if (action == null) return null;

        return AdminLogDTO.builder()
                .action((AdminLogEnum)action.get("enum"))
                .ipAddress(ipAddress)
                .targetId((Long)action.get("targetId"))
                .targetTable(extractTargetTable(uri))
                .admin(admin)
                .build();
    }
}

package org.csps.backend.aspect;

import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.csps.backend.annotation.Auditable;
import org.csps.backend.domain.enums.AuditAction;
import org.csps.backend.service.AuditLogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingAspect {

    private final AuditLogService auditLogService;

    /* intercepts methods annotated with @Auditable and logs the action */
    @AfterReturning(pointcut = "@annotation(auditable)")
    public void auditOperation(JoinPoint joinPoint, Auditable auditable) {
        try {
            Long adminId = getCurrentAdminId();
            if (adminId == null) {
                log.warn("audit logging: no admin found in security context");
                return;
            }

            String resourceId = extractResourceIdFromRequest();
            AuditAction action = auditable.action();
            String resourceType = auditable.resourceType();
            String description = "[" + LocalDateTime.now() + "] " + action.name() + " operation on " + resourceType;

            auditLogService.logAction(adminId, action, resourceType, resourceId, description);
            log.info("audit logged: {} {} by admin {}", action, resourceType, adminId);
        } catch (Exception e) {
            log.error("failed to log audit action: {}", e.getMessage(), e);
        }
    }

    /* helper method to get current admin ID from authentication context */
    private Long getCurrentAdminId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                try {
                    return Long.parseLong((String) principal);
                } catch (NumberFormatException e) {
                    log.error("failed to parse admin ID from principal: {}", e.getMessage());
                }
            } else if (principal instanceof Long) {
                return (Long) principal;
            }
        }
        return null;
    }

    /* helper method to extract resource ID from current request URI */
    private String extractResourceIdFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String requestUri = attributes.getRequest().getRequestURI();
            /* extract ID from URI - last path segment that's a number or UUID */
            String[] segments = requestUri.split("/");
            if (segments.length > 0) {
                String lastSegment = segments[segments.length - 1];
                /* return last segment if it's not empty and not a query param */
                if (!lastSegment.isEmpty() && !lastSegment.contains("?")) {
                    return lastSegment;
                }
            }
        }
        return "unknown";
    }
}

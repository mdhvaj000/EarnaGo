package com.example.domain.security

import com.example.data.model.UserEntity
import com.example.data.model.UserRole

/**
 * Enumeration of sensitive system operations requiring Role-Based Access Control (RBAC).
 */
enum class AppPermission(
    val title: String,
    val description: String,
    val minimumRoleRequired: UserRole
) {
    DISTRIBUTE_COMMISSIONS(
        title = "Distribute Network Commissions",
        description = "Manually trigger or override MLM network commission distribution and bonuses.",
        minimumRoleRequired = UserRole.ADMIN
    ),
    MANAGE_KYC(
        title = "Approve / Reject Member KYC",
        description = "Review government photo ID and address documents submitted by members.",
        minimumRoleRequired = UserRole.ADMIN
    ),
    MODIFY_ROYALTY_CONFIG(
        title = "Modify Owner Royalty & Settlement Bank",
        description = "Update 5% platform royalty percentage and main owner bank account details.",
        minimumRoleRequired = UserRole.OWNER
    ),
    MANAGE_TASKS(
        title = "Manage Platform Business Tasks",
        description = "Create, edit, or deactivate 15-module income tasks for members.",
        minimumRoleRequired = UserRole.ADMIN
    ),
    MANAGE_PRODUCTS(
        title = "Manage Marketplace Products",
        description = "Add or update product catalog pricing, BV values, and inventory.",
        minimumRoleRequired = UserRole.ADMIN
    ),
    VIEW_OWNER_CONSOLE(
        title = "Access Owner Master Console",
        description = "Access full system revenue analytics, owner royalty ledger, and system overview.",
        minimumRoleRequired = UserRole.ADMIN
    ),
    EXECUTE_SYSTEM_AUDIT(
        title = "Run AI Legal & Health Audits",
        description = "Trigger autonomous AI compliance scans and self-healing app diagnostics.",
        minimumRoleRequired = UserRole.ADMIN
    ),
    REQUEST_WITHDRAWAL(
        title = "Request Bank Payout / Withdrawal",
        description = "Request payout of verified wallet balance to personal bank account.",
        minimumRoleRequired = UserRole.MEMBER
    ),
    COMPLETE_TASKS(
        title = "Complete Income Tasks",
        description = "Perform daily module tasks and earn net rewards.",
        minimumRoleRequired = UserRole.MEMBER
    ),
    PURCHASE_PRODUCTS(
        title = "Purchase Marketplace Products",
        description = "Order digital and physical products using wallet or UPI.",
        minimumRoleRequired = UserRole.MEMBER
    )
}

/**
 * Result object returned when evaluating RBAC permissions.
 */
data class AuthorizationResult(
    val isGranted: Boolean,
    val permission: AppPermission,
    val actorRole: UserRole?,
    val message: String
)

/**
 * Enterprise-grade Role-Based Access Control (RBAC) Security Engine.
 * Enforces security rules across Domain, Data, and ViewModel layers.
 */
object RbacSecurityEngine {

    /**
     * Determines whether a given user role possesses the required rank/weight.
     * Hierarchy: OWNER > ADMIN > MEMBER
     */
    fun hasRoleHierarchy(actorRole: UserRole, requiredRole: UserRole): Boolean {
        val actorWeight = when (actorRole) {
            UserRole.OWNER -> 3
            UserRole.ADMIN -> 2
            UserRole.MEMBER -> 1
        }

        val requiredWeight = when (requiredRole) {
            UserRole.OWNER -> 3
            UserRole.ADMIN -> 2
            UserRole.MEMBER -> 1
        }

        return actorWeight >= requiredWeight
    }

    /**
     * Evaluates permission for a specified user actor.
     */
    fun evaluatePermission(actor: UserEntity?, permission: AppPermission): AuthorizationResult {
        if (actor == null) {
            return AuthorizationResult(
                isGranted = false,
                permission = permission,
                actorRole = null,
                message = "Access Denied: Unauthenticated user actor."
            )
        }

        val granted = hasRoleHierarchy(actor.role, permission.minimumRoleRequired)

        val message = if (granted) {
            "Access Granted: Role '${actor.role}' authorized for '${permission.title}'."
        } else {
            "Access Denied: '${permission.title}' requires role '${permission.minimumRoleRequired.name}', but current user role is '${actor.role.name}'."
        }

        return AuthorizationResult(
            isGranted = granted,
            permission = permission,
            actorRole = actor.role,
            message = message
        )
    }

    /**
     * Enforces permission and throws SecurityException if unauthorized.
     */
    @Throws(SecurityException::class)
    fun enforcePermission(actor: UserEntity?, permission: AppPermission) {
        val result = evaluatePermission(actor, permission)
        if (!result.isGranted) {
            throw SecurityException(result.message)
        }
    }
}

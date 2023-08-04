package com.github.drednote.telegram.filter;

public sealed interface PriorityUpdateFilter extends UpdateFilter
    permits AccessPermissionFilter, ConcurrentUserRequestFilter, RoleFilter {
}

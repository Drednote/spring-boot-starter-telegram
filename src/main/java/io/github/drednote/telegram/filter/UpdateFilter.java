package io.github.drednote.telegram.filter;

/**
 * Can be Bot Scope for keeping context during update handler
 */
public interface UpdateFilter extends PreUpdateFilter, PostUpdateFilter {

}
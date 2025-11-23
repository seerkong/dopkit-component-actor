package com.dopkit.dispatch;

/**
 * Matching modes for action-aware routing.
 */
public enum ActionMatchMode {
    /**
     * All actions (including {@code null}) are accepted once the path matches.
     */
    ALL,

    /**
     * Only actions contained in the configured allow-set are accepted.
     */
    IN,

    /**
     * All actions except those listed in the configured deny-set are accepted.
     */
    NOT_IN
}

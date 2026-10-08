package com.intecx.inscope.enumeration;

public enum ProjectStatus {
    DRAFT,
    UNDER_REVIEW,
    ESTIMATED,
    APPROVED,
    REJECTED,
    IN_PROGRESS,
    COMPLETED;

    public boolean canTransitionTo(ProjectStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        return switch (this) {
            case DRAFT -> nextStatus == UNDER_REVIEW;
            case UNDER_REVIEW -> nextStatus == DRAFT || nextStatus == ESTIMATED;
            case ESTIMATED -> nextStatus == UNDER_REVIEW || nextStatus == APPROVED || nextStatus == REJECTED;
            case REJECTED -> nextStatus == DRAFT || nextStatus == UNDER_REVIEW;
            case APPROVED -> nextStatus == IN_PROGRESS;
            case IN_PROGRESS -> nextStatus == COMPLETED;
            case COMPLETED -> false;
        };
    }
}

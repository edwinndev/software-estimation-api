package com.intecx.inscope.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProjectStatusTest {

    private static final Map<ProjectStatus, Set<ProjectStatus>> ALLOWED_TRANSITIONS = Map.of(
            ProjectStatus.DRAFT, Set.of(ProjectStatus.UNDER_REVIEW),
            ProjectStatus.UNDER_REVIEW, Set.of(ProjectStatus.DRAFT, ProjectStatus.ESTIMATED),
            ProjectStatus.ESTIMATED, Set.of(
                    ProjectStatus.UNDER_REVIEW,
                    ProjectStatus.APPROVED,
                    ProjectStatus.REJECTED
            ),
            ProjectStatus.REJECTED, Set.of(ProjectStatus.DRAFT, ProjectStatus.UNDER_REVIEW),
            ProjectStatus.APPROVED, Set.of(ProjectStatus.IN_PROGRESS),
            ProjectStatus.IN_PROGRESS, Set.of(ProjectStatus.COMPLETED),
            ProjectStatus.COMPLETED, Set.of()
    );

    @Test
    void allowsEveryConfiguredTransition() {
        ALLOWED_TRANSITIONS.forEach((current, nextStatuses) ->
                nextStatuses.forEach(next -> assertTrue(current.canTransitionTo(next)))
        );
    }

    @Test
    void rejectsEveryTransitionNotConfigured() {
        for (ProjectStatus current : ProjectStatus.values()) {
            for (ProjectStatus next : ProjectStatus.values()) {
                boolean expected = ALLOWED_TRANSITIONS.get(current).contains(next);
                assertEquals(expected, current.canTransitionTo(next), "%s -> %s".formatted(current, next));
            }
        }
    }

    @Test
    void completedDoesNotAllowAnyFurtherTransition() {
        for (ProjectStatus next : ProjectStatus.values()) {
            assertFalse(ProjectStatus.COMPLETED.canTransitionTo(next));
        }
    }
}

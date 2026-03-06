package com.example.springai.onboarding.connector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTypeTest {

    @Test
    void allConnectorTypes_HaveDisplayName() {
        for (ConnectorType type : ConnectorType.values()) {
            assertNotNull(type.getDisplayName());
            assertFalse(type.getDisplayName().isEmpty());
        }
    }

    @Test
    void allConnectorTypes_HaveDescription() {
        for (ConnectorType type : ConnectorType.values()) {
            assertNotNull(type.getDescription());
            assertFalse(type.getDescription().isEmpty());
        }
    }

    @Test
    void connectorType_Values_ContainsExpectedTypes() {
        ConnectorType[] types = ConnectorType.values();
        assertEquals(7, types.length);

        assertEquals(ConnectorType.AWS_DIRECTORY_SERVICE, ConnectorType.valueOf("AWS_DIRECTORY_SERVICE"));
        assertEquals(ConnectorType.AWS_WORKMAIL, ConnectorType.valueOf("AWS_WORKMAIL"));
        assertEquals(ConnectorType.CLAUDE_AI, ConnectorType.valueOf("CLAUDE_AI"));
        assertEquals(ConnectorType.JIRA, ConnectorType.valueOf("JIRA"));
        assertEquals(ConnectorType.ARMIS, ConnectorType.valueOf("ARMIS"));
        assertEquals(ConnectorType.GITLAB, ConnectorType.valueOf("GITLAB"));
        assertEquals(ConnectorType.CROWDSTRIKE, ConnectorType.valueOf("CROWDSTRIKE"));
    }

    @Test
    void connectorType_DisplayNames_AreCorrect() {
        assertEquals("AWS Directory Service", ConnectorType.AWS_DIRECTORY_SERVICE.getDisplayName());
        assertEquals("AWS WorkMail", ConnectorType.AWS_WORKMAIL.getDisplayName());
        assertEquals("Claude AI", ConnectorType.CLAUDE_AI.getDisplayName());
        assertEquals("Jira", ConnectorType.JIRA.getDisplayName());
        assertEquals("Armis", ConnectorType.ARMIS.getDisplayName());
        assertEquals("GitLab", ConnectorType.GITLAB.getDisplayName());
        assertEquals("CrowdStrike", ConnectorType.CROWDSTRIKE.getDisplayName());
    }
}

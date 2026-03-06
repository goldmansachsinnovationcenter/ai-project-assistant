package com.example.springai.onboarding.connector;

/**
 * Enum representing the types of connectors available in GSIC-TRACK.
 */
public enum ConnectorType {
    AWS_DIRECTORY_SERVICE("AWS Directory Service", "Create users in AWS Directory Services"),
    AWS_WORKMAIL("AWS WorkMail", "Create email accounts in AWS WorkMail"),
    CLAUDE_AI("Claude AI", "Invite users to Claude AI platform"),
    JIRA("Jira", "Onboard users to Jira projects"),
    ARMIS("Armis", "Onboard users to Armis security platform"),
    GITLAB("GitLab", "Onboard users to GitLab repositories and groups"),
    CROWDSTRIKE("CrowdStrike", "Onboard users to CrowdStrike endpoint security");

    private final String displayName;
    private final String description;

    ConnectorType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}

package org.greencodeinitiative.creedengo.integration.tests.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RuleMetadata(
		@JsonProperty("key")             String key,
		@JsonProperty("type")            String type,
		@JsonProperty("defaultSeverity") String defaultSeverity
) {}

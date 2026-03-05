package org.greencodeinitiative.creedengo.integration.tests.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProfileMetadata(
		@JsonProperty("name")     String name,
		@JsonProperty("language") String language,
		@JsonProperty("ruleKeys") List<String> ruleKeys
) {}

package org.greencodeinitiative.creedengo.integration.tests.profile;

import lombok.Data;

import java.util.List;

@Data
public class ProfileMetadata {

	private String name;
	private String language;
	private List<String> ruleKeys;

}

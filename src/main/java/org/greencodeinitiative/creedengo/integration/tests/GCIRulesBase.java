package org.greencodeinitiative.creedengo.integration.tests;

import org.assertj.core.groups.Tuple;
import org.sonarqube.ws.Common;
import org.sonarqube.ws.Components;
import org.sonarqube.ws.Issues;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarqube.ws.Common.RuleType.CODE_SMELL;
import static org.sonarqube.ws.Common.Severity.MINOR;

public class GCIRulesBase extends BuildProjectEngine {

    protected static final String[] EXTRACT_FIELDS = new String[]{
            "rule", "message",
//            "line"
            "textRange.startLine", "textRange.endLine",
//            "textRange.startOffset", "textRange.endOffset",
            "severity", "type",
//            "debt",
            "effort"
    };
    protected static final Common.Severity SEVERITY = MINOR;
    protected static final Common.RuleType TYPE = CODE_SMELL;
    protected static final String EFFORT_1MIN = "1min";
    protected static final String EFFORT_5MIN = "5min";
    protected static final String EFFORT_10MIN = "10min";
    protected static final String EFFORT_15MIN = "15min";
    protected static final String EFFORT_20MIN = "20min";
    protected static final String EFFORT_50MIN = "50min";

    protected void checkIssuesForFile(String filePath, String ruleId, String ruleMsg, int[] startLines, int[] endLines) {
        checkIssuesForFile(filePath, ruleId, ruleMsg, startLines, endLines, SEVERITY, TYPE, EFFORT_5MIN);
    }

    protected void checkIssuesForFile(String filePath, String ruleId, String ruleMsg, int[] startLines, int[] endLines, Common.Severity severity, Common.RuleType type, String effort) {

        assertThat(startLines.length)
                .isEqualTo(endLines.length);

        String projectKey = analyzedProjects.get(0).getProjectKey();

        String componentKey = projectKey + ":" + filePath;

//        System.out.println("--- COMPONENT KEY : " + componentKey);

        // launch the search
        Components.ShowWsResponse respComponent = showComponent(componentKey);
        Components.Component comp = respComponent.getComponent();
//        System.out.println("--- COMPONENT --- " + comp);
//        System.out.println("--- COMPONENT KEY --- " + comp.getKey());
//        System.out.println("--- COMPONENT PATH --- " + comp.getPath());
//        System.out.println("--- PATH ok --- " + filePath.equals(comp.getPath()));
        assertThat(filePath)
                .withFailMessage("File not found: " + filePath)
                .isEqualTo(comp.getPath());

        // check issues
        Issues.SearchWsResponse respIssues = searchIssuesForComponent(componentKey, ruleId);

//		System.out.println("--- NB ISSUES : " + respIssues.getIssuesCount());
//		System.out.println("--- NB ISSUES_LIST : " + respIssues.getIssuesList().size());
//        respIssues.getIssuesList().forEach(issue -> {
////			if (issue.getRule().equals("creedengo-java:GCI27")) {
//				System.out.println("--- Issue --- " + issue.getRule() + " / " + issue.getLine());
////			}
//		});

//        List<Issues.Issue> issues = issuesForFile(projectKey, filePath, ruleId);
        List<Issues.Issue> issues = respIssues.getIssuesList();

        List<Tuple> expectedTuples = new ArrayList<>();
        for (int i = 0; i < startLines.length; i++) {
            expectedTuples.add(Tuple.tuple(ruleId, ruleMsg, startLines[i], endLines[i], severity, type, effort));
        }

        assertThat(issues)
                .hasSizeGreaterThanOrEqualTo(startLines.length)
//                .hasSize(lines.length)
                .extracting(EXTRACT_FIELDS)
                .containsAll(expectedTuples);
//                .containsExactlyElementsOf(expectedTuples);
    }

}

package org.company.textblocktostring;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Assert;

import static org.company.textblocktostring.TextblockConverter.INTENTION_VISIBLE_TEXT;

public class TextblockConverterText extends LightJavaCodeInsightFixtureTestCase {
	@Override
	protected String getTestDataPath() {
		return "src/test/testData";
	}

	protected void doTest(String testName) {
		myFixture.configureByFile(testName + ".java");
		IntentionAction action = myFixture.findSingleIntention(INTENTION_VISIBLE_TEXT);
		Assert.assertNotNull(action);
		myFixture.launchAction(action);
		myFixture.checkResultByFile(testName + ".after.java");
	}

	public void testIntention_onHtml() {
		doTest("html");
	}

	public void testIntention_onJson() {
		doTest("json");
	}

}

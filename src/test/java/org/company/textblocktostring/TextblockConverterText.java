package org.company.textblocktostring;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Assert;

public class TextblockConverterText extends LightJavaCodeInsightFixtureTestCase {
	@Override
	protected String getTestDataPath() {
		return "src/test/testData";
	}

	protected void doTest(String testName, String hint) {
		myFixture.configureByFile(testName + ".java");
		IntentionAction action = myFixture.findSingleIntention(hint);
		Assert.assertNotNull(action);
		myFixture.launchAction(action);
		myFixture.checkResultByFile(testName + ".after.java");
	}

	public void testIntention() {
		doTest("before.template", "Convert text block to string literal");
	}

}

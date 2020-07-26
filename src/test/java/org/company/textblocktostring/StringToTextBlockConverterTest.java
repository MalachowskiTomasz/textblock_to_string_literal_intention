package org.company.textblocktostring;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Assert;

import static org.company.textblocktostring.StringToTextBlockConverter.INTENTION_VISIBLE_TEXT;

public class StringToTextBlockConverterTest extends BasePlatformTestCase {
	@Override
	protected String getTestDataPath() {
		return "src/test/stringToTextBlockTestData";
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

	public void testIntention_onSingleLine() {
		doTest("singleline");
	}

	public void testIntention_onJson() {
		doTest("json");
	}

	public void testIntention_withVariable() {
		doTest("withVariable");
	}

	public void testIntention_onSingleLineWithVariable() {
		doTest("singleLineWithVariable");
	}

}

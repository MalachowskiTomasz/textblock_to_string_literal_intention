package org.company.textblocktostring;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NonNls
public class TextblockConverter extends PsiElementBaseIntentionAction implements IntentionAction {

	public static final String THREE_QUOTES = "\"\"\"";
	public static final String EMPTY = "";
	public static final String NEW_LINE = "\n";
	public static final char SPACE = ' ';
	public static final char TAB = '\t';

	@Override
	@NotNull
	public String getText() {
		return "Convert text block to string literal";
	}

	@Override
	@NotNull
	public String getFamilyName() {
		return "StringIntention";
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
		if (element == null) {
			return false;
		}

		if (element instanceof PsiJavaToken) {
			PsiJavaToken token = (PsiJavaToken) element;
			return token.getTokenType() == JavaTokenType.TEXT_BLOCK_LITERAL;
		}
		return false;
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

		@Nullable PsiElementBase expression = PsiTreeUtil.getParentOfType(element, PsiElementBase.class, false);
		if (isNullOrEmpty(expression)) {
			return;
		}

		String formattedText = removeAllTextBlocks(element);
		formattedText = removeFirstEndLine(formattedText);
		formattedText = escapeQuotes(formattedText);
		formattedText = removeTabsOnBeginningOfLine(formattedText);

		replaceTextBlockWithStringLiteral(project, element, formattedText);
	}

	private void replaceTextBlockWithStringLiteral(@NotNull Project project, @NotNull PsiElement element, String stringLiteral) {
		PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
		CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);

		PsiExpression expression = factory.createExpressionFromText(stringLiteral, element.getContext());
		codeStylist.reformat(expression);
		element.getParent().replace(expression);
	}

	@NotNull
	private String removeTabsOnBeginningOfLine(String formattedText) {
		String[] linesArray = formattedText.split(NEW_LINE);
		AtomicInteger minimumCountOfTabs = new AtomicInteger();

		List<String> lines = Stream.of(linesArray)
				.peek(line -> calculateNumberOfTabsAtStartLine(minimumCountOfTabs, line))
				.map(line -> removeTabsAtTheStart(line, minimumCountOfTabs.get()))
				.map(this::removeEndLine)
				.map(this::surroundLineWithQuotes)
				.collect(Collectors.toList());

		removeWhitespacesAtTheEnd(lines);

		return String.join("+\n", lines);
	}

	private String surroundLineWithQuotes(String line) {
		return String.format("\"%s\\n\"", line);
	}

	private String removeEndLine(String line) {
		return StringUtils.removeEnd(line, NEW_LINE);
	}

	private String removeTabsAtTheStart(String line, int minimumCountOfTabs) {
		return line.replaceFirst(String.format(" *\t{0,%s}", minimumCountOfTabs), EMPTY);
	}

	private void calculateNumberOfTabsAtStartLine(AtomicInteger minimumCountOfTabs, String expression) {
		if (minimumCountOfTabs.get() <= 0) {
			int currentCharIndex = 0;
			while (expression.charAt(currentCharIndex) == SPACE) {
				currentCharIndex++;
			}
			while (expression.charAt(currentCharIndex) == TAB) {
				minimumCountOfTabs.incrementAndGet();
				currentCharIndex++;
			}
		}
	}

	private void removeWhitespacesAtTheEnd(List<String> lines) {
		if (lines.get(lines.size() - 1).matches("\"[\\s\\\\n]*\"")) {
			lines.remove(lines.size() - 1);
		}
		lines.set(lines.size() - 1, lines.get(lines.size() - 1).replace("\\n\"", "\""));
	}

	private boolean isNullOrEmpty(@Nullable PsiElementBase conditionalExpression) {
		return conditionalExpression == null || conditionalExpression.getText() == null;
	}

	@NotNull
	private String escapeQuotes(String formattedText) {
		return formattedText.replaceAll("\"", "\\\\\"");
	}

	@NotNull
	private String removeFirstEndLine(String formattedText) {
		if (formattedText.startsWith(NEW_LINE)) {
			formattedText = StringUtils.removeStart(formattedText, NEW_LINE);
		}
		return formattedText;
	}

	@NotNull
	private String removeAllTextBlocks(@NotNull PsiElement element) {
		return element.getText().replaceAll(THREE_QUOTES, EMPTY);
	}

	@Override
	public boolean startInWriteAction() {
		return true;
	}

}


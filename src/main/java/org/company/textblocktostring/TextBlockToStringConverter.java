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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.company.textblocktostring.CommonLiterals.*;

@NonNls
public class TextBlockToStringConverter extends PsiElementBaseIntentionAction implements IntentionAction {

	public static final String INTENTION_VISIBLE_TEXT = "Convert text block to string literal";

	@Override
	@NotNull
	public String getText() {
		return INTENTION_VISIBLE_TEXT;
	}

	@Override
	@NotNull
	public String getFamilyName() {
		return "StringIntention";
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
		if (element == null) return false;

		if (element instanceof PsiJavaToken) {
			PsiJavaToken token = (PsiJavaToken) element;
			return token.getTokenType() == JavaTokenType.TEXT_BLOCK_LITERAL;
		}
		return false;
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

		@Nullable PsiElementBase expression = PsiTreeUtil.getParentOfType(element, PsiElementBase.class, false);
		if (isNullOrEmpty(expression)) return;

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
		List<String> unformattedLines = new ArrayList<>(Arrays.asList(formattedText.split(NEW_LINE)));
		AtomicInteger minimumCountOfTabs = new AtomicInteger();

		deleteWhitespacesAtBegin(unformattedLines);

		List<String> lines = unformattedLines.stream()
				.peek(line -> calculateNumberOfTabsAtStartLine(minimumCountOfTabs, line))
				.map(line -> removeTabsAtTheStart(line, minimumCountOfTabs.get()))
				.map(this::removeEndLine)
				.map(this::surroundLineWithQuotes)
				.collect(Collectors.toList());

		removeWhitespacesAtTheEnd(lines);

		return String.join("+\n", lines);
	}

	private void deleteWhitespacesAtBegin(List<String> lines) {
		while (StringUtils.isBlank(lines.get(0))) lines.remove(0);
	}

	private String surroundLineWithQuotes(String line) {
		return String.format("\"%s\\n\"", line);
	}

	private String removeEndLine(String line) {
		return line.replaceAll("[\n ]*]+$", EMPTY);
	}

	private String removeTabsAtTheStart(String line, int minimumCountOfTabs) {
		return line.replaceFirst(String.format(" *\t{0,%s}", minimumCountOfTabs), EMPTY);
	}

	private void calculateNumberOfTabsAtStartLine(AtomicInteger minimumCountOfTabs, String expression) {
		if (minimumCountOfTabs.get() <= 0) {
			int currentCharIndex = 0;
			while (expression.charAt(currentCharIndex) == SPACE) currentCharIndex++;
			while (expression.charAt(currentCharIndex) == TAB) {
				minimumCountOfTabs.incrementAndGet();
				currentCharIndex++;
			}
		}
	}

	private void removeWhitespacesAtTheEnd(List<String> lines) {
		while (lines.get(lines.size() - 1).matches("\"[\\s\\\\n]*\"")) lines.remove(lines.size() - 1);
		lines.set(lines.size() - 1, lines.get(lines.size() - 1).replace("\\n\"", QUOTE));
	}

	private boolean isNullOrEmpty(@Nullable PsiElementBase conditionalExpression) {
		return conditionalExpression == null || conditionalExpression.getText() == null;
	}

	@NotNull
	private String escapeQuotes(String formattedText) {
		return formattedText.replaceAll(QUOTE, ESCAPED_QUOTE);
	}

	@NotNull
	private String removeFirstEndLine(String formattedText) {
		return formattedText.replaceFirst(NEW_LINE + "*", EMPTY);
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


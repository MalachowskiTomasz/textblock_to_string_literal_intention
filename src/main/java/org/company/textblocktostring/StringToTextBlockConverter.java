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
import org.company.textblocktostring.totextblock.PolyadicExpressionToTextBlockConverter;
import org.company.textblocktostring.totextblock.SingleLiteralToTextBlockConverter;
import org.company.textblocktostring.totextblock.ToTextBlockConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonNls
public class StringToTextBlockConverter extends PsiElementBaseIntentionAction implements IntentionAction {

	public static final String INTENTION_VISIBLE_TEXT = "Convert string literal to text block";

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
	public boolean startInWriteAction() {
		return true;
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
		if (element == null) return false;

		if (element instanceof PsiJavaToken) {
			PsiJavaToken token = (PsiJavaToken) element;
			return token.getTokenType() == JavaTokenType.STRING_LITERAL;
		}
		return false;
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
		PsiElement elementBase = PsiTreeUtil.getParentOfType(element, PsiElementBase.class, false);

		ToTextBlockConverter expressionConverter = converterFor(elementBase);
		if (!expressionConverter.isValid(elementBase)) return;
		replaceStringWithTextBlock(project, expressionConverter.baseElement(elementBase), expressionConverter.convert(elementBase));
	}

	@NotNull
	private ToTextBlockConverter converterFor(PsiElement elementBase) {
		return PolyadicExpressionToTextBlockConverter.isRelevant(elementBase)
				? new PolyadicExpressionToTextBlockConverter()
				: new SingleLiteralToTextBlockConverter();
	}

	private void replaceStringWithTextBlock(@NotNull Project project, @NotNull PsiElement element, String stringLiteral) {
		PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
		CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);

		PsiExpression expression = factory.createExpressionFromText(stringLiteral, element.getContext());
		codeStylist.reformat(expression);
		element.replace(expression);
	}

}


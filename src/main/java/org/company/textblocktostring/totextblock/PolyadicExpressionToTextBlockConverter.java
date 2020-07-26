package org.company.textblocktostring.totextblock;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPolyadicExpression;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PolyadicExpressionToTextBlockConverter extends ToTextBlockConverter {

    public static boolean isRelevant(PsiElement element) {
        return element.getParent() instanceof PsiPolyadicExpression;
    }

    @Override
    public boolean isValid(PsiElement element) {
        PsiPolyadicExpression polyadicExpression = ((PsiPolyadicExpression) element.getParent());
        return isValidPolyadicExpression(polyadicExpression);
    }

    private boolean isValidPolyadicExpression(PsiPolyadicExpression expression) {
        return !isNullOrEmpty(expression) && containsOnlyStringLiteralsOperands(expression);
    }

    private boolean isNullOrEmpty(PsiPolyadicExpression expression) {
        return expression == null || expression.getOperands() == null || expression.getOperands().length <= 0;
    }

    private boolean containsOnlyStringLiteralsOperands(PsiPolyadicExpression expression) {
        for (PsiExpression operand : expression.getOperands())
            if (!(operand instanceof PsiLiteralExpression)) return false;
        return true;
    }

    @Override
    public String convert(PsiElement element) {
        return convertToExpression((PsiPolyadicExpression) element.getParent());
    }

    private String convertToExpression(PsiPolyadicExpression expression) {
        String modifiedExpression = Arrays.stream(expression.getOperands())
                .map(PsiElement::getText)
                .map(this::reformatSingleStringLiteral)
                .collect(Collectors.joining());
        return surroundWithTripleBraces(modifiedExpression);
    }

    @Override
    public PsiElement baseElement(PsiElement element) {
        return element.getParent();
    }
}

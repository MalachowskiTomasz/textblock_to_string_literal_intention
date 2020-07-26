package org.company.textblocktostring.totextblock;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPolyadicExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.company.textblocktostring.CommonLiterals.COMMA;

public class PolyadicExpressionToTextBlockConverter extends ToTextBlockConverter {

    public static final String STRING_FORMAT_SPECIFIER = "%s";

    public static boolean isRelevant(PsiElement element) {
        return element.getParent() instanceof PsiPolyadicExpression;
    }

    @Override
    public boolean isValid(PsiElement element) {
        PsiPolyadicExpression polyadicExpression = ((PsiPolyadicExpression) element.getParent());
        return isValidPolyadicExpression(polyadicExpression);
    }

    private boolean isValidPolyadicExpression(PsiPolyadicExpression expression) {
        return !isNullOrEmpty(expression);
    }

    private boolean isNullOrEmpty(PsiPolyadicExpression expression) {
        return expression == null || expression.getOperands() == null || expression.getOperands().length <= 0;
    }

    @Override
    public String convert(PsiElement element) {
        return convertToExpression((PsiPolyadicExpression) element.getParent());
    }

    private String convertToExpression(PsiPolyadicExpression expression) {
        Collection<PsiElement> nonStringLiterals = new HashSet<>();
        String modifiedExpression = Arrays.stream(expression.getOperands())
                .peek(o -> addToCollectionIfNotStringLiteral(o, nonStringLiterals))
                .map(this::getTextOrPlaceFormatIndicatorForNonLiterals)
                .map(this::reformatSingleStringLiteral)
                .collect(Collectors.joining());

        String result = surroundWithTripleBraces(modifiedExpression);
        return nonStringLiterals.isEmpty()
                ? result
                : surroundWithStringFormat(result, nonStringLiterals);
    }

    @NotNull
    private String surroundWithStringFormat(String result, Collection<PsiElement> nonStringLiterals) {
        String nonStringLiteralsTextSeparatedByComma = nonStringLiterals.stream()
                .map(PsiElement::getText)
                .collect(Collectors.joining(COMMA));
        return "String.format(" + result + "," + nonStringLiteralsTextSeparatedByComma + ")";
    }

    private String getTextOrPlaceFormatIndicatorForNonLiterals(@NotNull PsiExpression psiExpression) {
        return psiExpression instanceof PsiLiteralExpression ? psiExpression.getText() : STRING_FORMAT_SPECIFIER;
    }

    private void addToCollectionIfNotStringLiteral(PsiExpression e, Collection<PsiElement> nonStringLiterals) {
        if (!(e instanceof PsiLiteralExpression)) nonStringLiterals.add(e);
    }

    @Override
    public PsiElement baseElement(PsiElement element) {
        return element.getParent();
    }
}

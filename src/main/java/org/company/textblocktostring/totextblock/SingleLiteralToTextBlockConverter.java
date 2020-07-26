package org.company.textblocktostring.totextblock;

import com.intellij.psi.PsiElement;

public class SingleLiteralToTextBlockConverter extends ToTextBlockConverter {

    @Override
    public boolean isValid(PsiElement element) {
        return element != null && element.getText() != null;
    }

    @Override
    public String convert(PsiElement element) {
        return surroundWithTripleBraces(reformatSingleStringLiteral(element.getText()));
    }

    @Override
    public PsiElement baseElement(PsiElement element) {
        return element;
    }
}

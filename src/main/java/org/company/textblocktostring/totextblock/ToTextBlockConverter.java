package org.company.textblocktostring.totextblock;

import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;

import static org.company.textblocktostring.CommonLiterals.*;

public abstract class ToTextBlockConverter {

    public abstract boolean isValid(PsiElement element);

    public abstract String convert(PsiElement element);

    public abstract PsiElement baseElement(PsiElement element);

    protected String reformatSingleStringLiteral(String text) {
        String result = removeOpeningAndClosingQuotes(text);
        result = unescapeNewLine(result);
        result = unescapeQuotes(result);
        return result;
    }

    private String unescapeQuotes(String text) {
        return text.replaceAll(ESCAPED_QUOTE, QUOTE);
    }

    private String unescapeNewLine(String text) {
        return text.replace(ESCAPED_NEW_LINE, NEW_LINE);
    }

    private String removeOpeningAndClosingQuotes(String s) {
        return StringUtils.removeStart(StringUtils.removeEnd(s, QUOTE), QUOTE);
    }

    protected String surroundWithTripleBraces(String text) {
        return THREE_QUOTES + NEW_LINE + text + NEW_LINE + THREE_QUOTES;
    }
}

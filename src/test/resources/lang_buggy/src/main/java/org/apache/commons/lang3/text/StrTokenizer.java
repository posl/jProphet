















package org.apache.commons.lang3.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;





























































public class StrTokenizer implements ListIterator<String>, Cloneable {

    private static final StrTokenizer CSV_TOKENIZER_PROTOTYPE;
    private static final StrTokenizer TSV_TOKENIZER_PROTOTYPE;
    static {
        CSV_TOKENIZER_PROTOTYPE = new StrTokenizer();
        CSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StrMatcher.commaMatcher());
        CSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StrMatcher.doubleQuoteMatcher());
        CSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StrMatcher.noneMatcher());
        CSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StrMatcher.trimMatcher());
        CSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        CSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);

        TSV_TOKENIZER_PROTOTYPE = new StrTokenizer();
        TSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StrMatcher.tabMatcher());
        TSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StrMatcher.doubleQuoteMatcher());
        TSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StrMatcher.noneMatcher());
        TSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StrMatcher.trimMatcher());
        TSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        TSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
    }

    
    private char chars[];
    
    private String tokens[];
    
    private int tokenPos;

    
    private StrMatcher delimMatcher = StrMatcher.splitMatcher();
    
    private StrMatcher quoteMatcher = StrMatcher.noneMatcher();
    
    private StrMatcher ignoredMatcher = StrMatcher.noneMatcher();
    
    private StrMatcher trimmerMatcher = StrMatcher.noneMatcher();

    
    private boolean emptyAsNull = false;
    
    private boolean ignoreEmptyTokens = true;

    






    private static StrTokenizer getCSVClone() {
        return (StrTokenizer) CSV_TOKENIZER_PROTOTYPE.clone();
    }










    public static StrTokenizer getCSVInstance() {
        return getCSVClone();
    }










    public static StrTokenizer getCSVInstance(final String input) {
        final StrTokenizer tok = getCSVClone();
        tok.reset(input);
        return tok;
    }










    public static StrTokenizer getCSVInstance(final char[] input) {
        final StrTokenizer tok = getCSVClone();
        tok.reset(input);
        return tok;
    }






    private static StrTokenizer getTSVClone() {
        return (StrTokenizer) TSV_TOKENIZER_PROTOTYPE.clone();
    }










    public static StrTokenizer getTSVInstance() {
        return getTSVClone();
    }








    public static StrTokenizer getTSVInstance(final String input) {
        final StrTokenizer tok = getTSVClone();
        tok.reset(input);
        return tok;
    }








    public static StrTokenizer getTSVInstance(final char[] input) {
        final StrTokenizer tok = getTSVClone();
        tok.reset(input);
        return tok;
    }

    






    public StrTokenizer() {
        super();
        this.chars = null;
    }







    public StrTokenizer(final String input) {
        super();
        if (input != null) {
            chars = input.toCharArray();
        } else {
            chars = null;
        }
    }







    public StrTokenizer(final String input, final char delim) {
        this(input);
        setDelimiterChar(delim);
    }







    public StrTokenizer(final String input, final String delim) {
        this(input);
        setDelimiterString(delim);
    }







    public StrTokenizer(final String input, final StrMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }









    public StrTokenizer(final String input, final char delim, final char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }









    public StrTokenizer(final String input, final StrMatcher delim, final StrMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }







    public StrTokenizer(final char[] input) {
        super();
        this.chars = ArrayUtils.clone(input);
    }







    public StrTokenizer(final char[] input, final char delim) {
        this(input);
        setDelimiterChar(delim);
    }







    public StrTokenizer(final char[] input, final String delim) {
        this(input);
        setDelimiterString(delim);
    }







    public StrTokenizer(final char[] input, final StrMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }









    public StrTokenizer(final char[] input, final char delim, final char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }









    public StrTokenizer(final char[] input, final StrMatcher delim, final StrMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }

    
    





    public int size() {
        checkTokenized();
        return tokens.length;
    }








    public String nextToken() {
        if (hasNext()) {
            return tokens[tokenPos++];
        }
        return null;
    }






    public String previousToken() {
        if (hasPrevious()) {
            return tokens[--tokenPos];
        }
        return null;
    }






    public String[] getTokenArray() {
        checkTokenized();
        return tokens.clone();
    }






    public List<String> getTokenList() {
        checkTokenized();
        final List<String> list = new ArrayList<String>(tokens.length);
        for (final String element : tokens) {
            list.add(element);
        }
        return list;
    }








    public StrTokenizer reset() {
        tokenPos = 0;
        tokens = null;
        return this;
    }









    public StrTokenizer reset(final String input) {
        reset();
        if (input != null) {
            this.chars = input.toCharArray();
        } else {
            this.chars = null;
        }
        return this;
    }









    public StrTokenizer reset(final char[] input) {
        reset();
        this.chars = ArrayUtils.clone(input);
        return this;
    }

    
    





    @Override
    public boolean hasNext() {
        checkTokenized();
        return tokenPos < tokens.length;
    }







    @Override
    public String next() {
        if (hasNext()) {
            return tokens[tokenPos++];
        }
        throw new NoSuchElementException();
    }






    @Override
    public int nextIndex() {
        return tokenPos;
    }






    @Override
    public boolean hasPrevious() {
        checkTokenized();
        return tokenPos > 0;
    }






    @Override
    public String previous() {
        if (hasPrevious()) {
            return tokens[--tokenPos];
        }
        throw new NoSuchElementException();
    }






    @Override
    public int previousIndex() {
        return tokenPos - 1;
    }






    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is unsupported");
    }






    @Override
    public void set(final String obj) {
        throw new UnsupportedOperationException("set() is unsupported");
    }






    @Override
    public void add(final String obj) {
        throw new UnsupportedOperationException("add() is unsupported");
    }

    
    



    private void checkTokenized() {
        if (tokens == null) {
            if (chars == null) {
                
                final List<String> split = tokenize(null, 0, 0);
                tokens = split.toArray(new String[split.size()]);
            } else {
                final List<String> split = tokenize(chars, 0, chars.length);
                tokens = split.toArray(new String[split.size()]);
            }
        }
    }





















    protected List<String> tokenize(final char[] chars, final int offset, final int count) {
        if (chars == null || count == 0) {
            return Collections.emptyList();
        }
        final StrBuilder buf = new StrBuilder();
        final List<String> tokens = new ArrayList<String>();
        int pos = offset;
        
        
        while (pos >= 0 && pos < count) {
            
            pos = readNextToken(chars, pos, count, buf, tokens);
            
            
            if (pos >= count) {
                addToken(tokens, "");
            }
        }
        return tokens;
    }







    private void addToken(final List<String> list, String tok) {
        if (StringUtils.isEmpty(tok)) {
            if (isIgnoreEmptyTokens()) {
                return;
            }
            if (isEmptyTokenAsNull()) {
                tok = null;
            }
        }
        list.add(tok);
    }












    private int readNextToken(final char[] chars, int start, final int len, final StrBuilder workArea, final List<String> tokens) {
        
        
        while (start < len) {
            final int removeLen = Math.max(
                    getIgnoredMatcher().isMatch(chars, start, start, len),
                    getTrimmerMatcher().isMatch(chars, start, start, len));
            if (removeLen == 0 ||
                getDelimiterMatcher().isMatch(chars, start, start, len) > 0 ||
                getQuoteMatcher().isMatch(chars, start, start, len) > 0) {
                break;
            }
            start += removeLen;
        }
        
        
        if (start >= len) {
            addToken(tokens, "");
            return -1;
        }
        
        
        final int delimLen = getDelimiterMatcher().isMatch(chars, start, start, len);
        if (delimLen > 0) {
            addToken(tokens, "");
            return start + delimLen;
        }
        
        
        final int quoteLen = getQuoteMatcher().isMatch(chars, start, start, len);
        if (quoteLen > 0) {
            return readWithQuotes(chars, start + quoteLen, len, workArea, tokens, start, quoteLen);
        }
        return readWithQuotes(chars, start, len, workArea, tokens, 0, 0);
    }















    private int readWithQuotes(final char[] chars, final int start, final int len, final StrBuilder workArea, 
                               final List<String> tokens, final int quoteStart, final int quoteLen) {
        
        
        workArea.clear();
        int pos = start;
        boolean quoting = quoteLen > 0;
        int trimStart = 0;
        
        while (pos < len) {
            
            
            
            if (quoting) {
                
                
                
                
                
                
                if (isQuote(chars, pos, len, quoteStart, quoteLen)) {
                    if (isQuote(chars, pos + quoteLen, len, quoteStart, quoteLen)) {
                        
                        workArea.append(chars, pos, quoteLen);
                        pos += quoteLen * 2;
                        trimStart = workArea.size();
                        continue;
                    }
                    
                    
                    quoting = false;
                    pos += quoteLen;
                    continue;
                }
                
                
                workArea.append(chars[pos++]);
                trimStart = workArea.size();
                
            } else {
                
                
                
                final int delimLen = getDelimiterMatcher().isMatch(chars, pos, start, len);
                if (delimLen > 0) {
                    
                    addToken(tokens, workArea.substring(0, trimStart));
                    return pos + delimLen;
                }
                
                
                if (quoteLen > 0 && isQuote(chars, pos, len, quoteStart, quoteLen)) {
                    quoting = true;
                    pos += quoteLen;
                    continue;
                }
                
                
                final int ignoredLen = getIgnoredMatcher().isMatch(chars, pos, start, len);
                if (ignoredLen > 0) {
                    pos += ignoredLen;
                    continue;
                }
                
                
                
                
                final int trimmedLen = getTrimmerMatcher().isMatch(chars, pos, start, len);
                if (trimmedLen > 0) {
                    workArea.append(chars, pos, trimmedLen);
                    pos += trimmedLen;
                    continue;
                }
                
                
                workArea.append(chars[pos++]);
                trimStart = workArea.size();
            }
        }
        
        
        addToken(tokens, workArea.substring(0, trimStart));
        return -1;
    }












    private boolean isQuote(final char[] chars, final int pos, final int len, final int quoteStart, final int quoteLen) {
        for (int i = 0; i < quoteLen; i++) {
            if (pos + i >= len || chars[pos + i] != chars[quoteStart + i]) {
                return false;
            }
        }
        return true;
    }

    
    





    public StrMatcher getDelimiterMatcher() {
        return this.delimMatcher;
    }









    public StrTokenizer setDelimiterMatcher(final StrMatcher delim) {
        if (delim == null) {
            this.delimMatcher = StrMatcher.noneMatcher();
        } else {
            this.delimMatcher = delim;
        }
        return this;
    }







    public StrTokenizer setDelimiterChar(final char delim) {
        return setDelimiterMatcher(StrMatcher.charMatcher(delim));
    }







    public StrTokenizer setDelimiterString(final String delim) {
        return setDelimiterMatcher(StrMatcher.stringMatcher(delim));
    }

    
    









    public StrMatcher getQuoteMatcher() {
        return quoteMatcher;
    }










    public StrTokenizer setQuoteMatcher(final StrMatcher quote) {
        if (quote != null) {
            this.quoteMatcher = quote;
        }
        return this;
    }










    public StrTokenizer setQuoteChar(final char quote) {
        return setQuoteMatcher(StrMatcher.charMatcher(quote));
    }

    
    









    public StrMatcher getIgnoredMatcher() {
        return ignoredMatcher;
    }










    public StrTokenizer setIgnoredMatcher(final StrMatcher ignored) {
        if (ignored != null) {
            this.ignoredMatcher = ignored;
        }
        return this;
    }










    public StrTokenizer setIgnoredChar(final char ignored) {
        return setIgnoredMatcher(StrMatcher.charMatcher(ignored));
    }

    
    









    public StrMatcher getTrimmerMatcher() {
        return trimmerMatcher;
    }










    public StrTokenizer setTrimmerMatcher(final StrMatcher trimmer) {
        if (trimmer != null) {
            this.trimmerMatcher = trimmer;
        }
        return this;
    }

    






    public boolean isEmptyTokenAsNull() {
        return this.emptyAsNull;
    }








    public StrTokenizer setEmptyTokenAsNull(final boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
        return this;
    }

    






    public boolean isIgnoreEmptyTokens() {
        return ignoreEmptyTokens;
    }








    public StrTokenizer setIgnoreEmptyTokens(final boolean ignoreEmptyTokens) {
        this.ignoreEmptyTokens = ignoreEmptyTokens;
        return this;
    }

    





    public String getContent() {
        if (chars == null) {
            return null;
        }
        return new String(chars);
    }

    







    @Override
    public Object clone() {
        try {
            return cloneReset();
        } catch (final CloneNotSupportedException ex) {
            return null;
        }
    }








    Object cloneReset() throws CloneNotSupportedException {
        
        final StrTokenizer cloned = (StrTokenizer) super.clone();
        if (cloned.chars != null) {
            cloned.chars = cloned.chars.clone();
        }
        cloned.reset();
        return cloned;
    }

    





    @Override
    public String toString() {
        if (tokens == null) {
            return "StrTokenizer[not tokenized yet]";
        }
        return "StrTokenizer" + getTokenList();
    }

}

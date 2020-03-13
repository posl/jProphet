















package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;










public class CharSet implements Serializable {

    
    
    
    

    private static final long serialVersionUID = 5947847346149275958L;

    
    
    

    public static final CharSet EMPTY = new CharSet((String) null);

    
    
    

    public static final CharSet ASCII_ALPHA = new CharSet("a-zA-Z");

    
    
    

    public static final CharSet ASCII_ALPHA_LOWER = new CharSet("a-z");

    
    
    

    public static final CharSet ASCII_ALPHA_UPPER = new CharSet("A-Z");

    
    
    

    public static final CharSet ASCII_NUMERIC = new CharSet("0-9");

    
    
    
    

    protected static final Map<String, CharSet> COMMON = Collections.synchronizedMap(new HashMap<String, CharSet>());
    
    static {
        COMMON.put(null, EMPTY);
        COMMON.put("", EMPTY);
        COMMON.put("a-zA-Z", ASCII_ALPHA);
        COMMON.put("A-Za-z", ASCII_ALPHA);
        COMMON.put("a-z", ASCII_ALPHA_LOWER);
        COMMON.put("A-Z", ASCII_ALPHA_UPPER);
        COMMON.put("0-9", ASCII_NUMERIC);
    }

    
    private final Set<CharRange> set = Collections.synchronizedSet(new HashSet<CharRange>());

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public static CharSet getInstance(final String... setStrs) {
        if (setStrs == null) {
            return null;
        }
        if (setStrs.length == 1) {
            final CharSet common = COMMON.get(setStrs[0]);
            if (common != null) {
                return common;
            }
        }
        return new CharSet(setStrs); 
    }

    
    
    
    
    
    
    

    protected CharSet(final String... set) {
        super();
        final int sz = set.length;
        for (int i = 0; i < sz; i++) {
            add(set[i]);
        }
    }

    
    
    
    
    

    protected void add(final String str) {
        if (str == null) {
            return;
        }

        final int len = str.length();
        int pos = 0;
        while (pos < len) {
            final int remainder = len - pos;
            if (remainder >= 4 && str.charAt(pos) == '^' && str.charAt(pos + 2) == '-') {
                
                set.add(CharRange.isNotIn(str.charAt(pos + 1), str.charAt(pos + 3)));
                pos += 4;
            } else if (remainder >= 3 && str.charAt(pos + 1) == '-') {
                
                set.add(CharRange.isIn(str.charAt(pos), str.charAt(pos + 2)));
                pos += 3;
            } else if (remainder >= 2 && str.charAt(pos) == '^') {
                
                set.add(CharRange.isNot(str.charAt(pos + 1)));
                pos += 2;
            } else {
                
                set.add(CharRange.is(str.charAt(pos)));
                pos += 1;
            }
        }
    }

    
    
    
    
    
    



     CharRange[] getCharRanges() {
        return set.toArray(new CharRange[set.size()]);
    }

    
    
    
    
    
    
    

    public boolean contains(final char ch) {
        for (final CharRange range : set) {
            if (range.contains(ch)) {
                return true;
            }
        }
        return false;
    }

    
    
    
    
    
    
    
    
    
    
    
    

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CharSet == false) {
            return false;
        }
        final CharSet other = (CharSet) obj;
        return set.equals(other.set);
    }

    
    
    
    
    

    @Override
    public int hashCode() {
        return 89 + set.hashCode();
    }

    
    
    
    

    @Override
    public String toString() {
        return set.toString();
    }

}

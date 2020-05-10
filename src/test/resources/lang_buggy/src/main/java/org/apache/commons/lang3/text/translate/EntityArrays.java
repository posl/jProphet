/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http:
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.text.translate;

/**
 * Class holding various entity data for HTML and XML - generally for use with
 * the LookupTranslator.
 * All arrays are of length [*][2].
 *
 * @since 3.0
 * @version $Id$
 */
public class EntityArrays {

    /**
     * Mapping to escape <a href="https:
     * characters to their named HTML 3.x equivalents.
     * @return the mapping table
     */
    public static String[][] ISO8859_1_ESCAPE() { return ISO8859_1_ESCAPE.clone(); }
    private static final String[][] ISO8859_1_ESCAPE = {
        {"\u00A0", "&nbsp;"}, 
        {"\u00A1", "&iexcl;"}, 
        {"\u00A2", "&cent;"}, 
        {"\u00A3", "&pound;"}, 
        {"\u00A4", "&curren;"}, 
        {"\u00A5", "&yen;"}, 
        {"\u00A6", "&brvbar;"}, 
        {"\u00A7", "&sect;"}, 
        {"\u00A8", "&uml;"}, 
        {"\u00A9", "&copy;"}, 
        {"\u00AA", "&ordf;"}, 
        {"\u00AB", "&laquo;"}, 
        {"\u00AC", "&not;"}, 
        {"\u00AD", "&shy;"}, 
        {"\u00AE", "&reg;"}, 
        {"\u00AF", "&macr;"}, 
        {"\u00B0", "&deg;"}, 
        {"\u00B1", "&plusmn;"}, 
        {"\u00B2", "&sup2;"}, 
        {"\u00B3", "&sup3;"}, 
        {"\u00B4", "&acute;"}, 
        {"\u00B5", "&micro;"}, 
        {"\u00B6", "&para;"}, 
        {"\u00B7", "&middot;"}, 
        {"\u00B8", "&cedil;"}, 
        {"\u00B9", "&sup1;"}, 
        {"\u00BA", "&ordm;"}, 
        {"\u00BB", "&raquo;"}, 
        {"\u00BC", "&frac14;"}, 
        {"\u00BD", "&frac12;"}, 
        {"\u00BE", "&frac34;"}, 
        {"\u00BF", "&iquest;"}, 
        {"\u00C0", "&Agrave;"}, 
        {"\u00C1", "&Aacute;"}, 
        {"\u00C2", "&Acirc;"}, 
        {"\u00C3", "&Atilde;"}, 
        {"\u00C4", "&Auml;"}, 
        {"\u00C5", "&Aring;"}, 
        {"\u00C6", "&AElig;"}, 
        {"\u00C7", "&Ccedil;"}, 
        {"\u00C8", "&Egrave;"}, 
        {"\u00C9", "&Eacute;"}, 
        {"\u00CA", "&Ecirc;"}, 
        {"\u00CB", "&Euml;"}, 
        {"\u00CC", "&Igrave;"}, 
        {"\u00CD", "&Iacute;"}, 
        {"\u00CE", "&Icirc;"}, 
        {"\u00CF", "&Iuml;"}, 
        {"\u00D0", "&ETH;"}, 
        {"\u00D1", "&Ntilde;"}, 
        {"\u00D2", "&Ograve;"}, 
        {"\u00D3", "&Oacute;"}, 
        {"\u00D4", "&Ocirc;"}, 
        {"\u00D5", "&Otilde;"}, 
        {"\u00D6", "&Ouml;"}, 
        {"\u00D7", "&times;"}, 
        {"\u00D8", "&Oslash;"}, 
        {"\u00D9", "&Ugrave;"}, 
        {"\u00DA", "&Uacute;"}, 
        {"\u00DB", "&Ucirc;"}, 
        {"\u00DC", "&Uuml;"}, 
        {"\u00DD", "&Yacute;"}, 
        {"\u00DE", "&THORN;"}, 
        {"\u00DF", "&szlig;"}, 
        {"\u00E0", "&agrave;"}, 
        {"\u00E1", "&aacute;"}, 
        {"\u00E2", "&acirc;"}, 
        {"\u00E3", "&atilde;"}, 
        {"\u00E4", "&auml;"}, 
        {"\u00E5", "&aring;"}, 
        {"\u00E6", "&aelig;"}, 
        {"\u00E7", "&ccedil;"}, 
        {"\u00E8", "&egrave;"}, 
        {"\u00E9", "&eacute;"}, 
        {"\u00EA", "&ecirc;"}, 
        {"\u00EB", "&euml;"}, 
        {"\u00EC", "&igrave;"}, 
        {"\u00ED", "&iacute;"}, 
        {"\u00EE", "&icirc;"}, 
        {"\u00EF", "&iuml;"}, 
        {"\u00F0", "&eth;"}, 
        {"\u00F1", "&ntilde;"}, 
        {"\u00F2", "&ograve;"}, 
        {"\u00F3", "&oacute;"}, 
        {"\u00F4", "&ocirc;"}, 
        {"\u00F5", "&otilde;"}, 
        {"\u00F6", "&ouml;"}, 
        {"\u00F7", "&divide;"}, 
        {"\u00F8", "&oslash;"}, 
        {"\u00F9", "&ugrave;"}, 
        {"\u00FA", "&uacute;"}, 
        {"\u00FB", "&ucirc;"}, 
        {"\u00FC", "&uuml;"}, 
        {"\u00FD", "&yacute;"}, 
        {"\u00FE", "&thorn;"}, 
        {"\u00FF", "&yuml;"}, 
    };

    /**
     * Reverse of {@link #ISO8859_1_ESCAPE()} for unescaping purposes.
     * @return the mapping table
     */
    public static String[][] ISO8859_1_UNESCAPE() { return ISO8859_1_UNESCAPE.clone(); }
    private static final String[][] ISO8859_1_UNESCAPE = invert(ISO8859_1_ESCAPE);

    /**
     * Mapping to escape additional <a href="http:
     * references</a>. Note that this must be used with {@link #ISO8859_1_ESCAPE()} to get the full list of
     * HTML 4.0 character entities.
     * @return the mapping table
     */
    public static String[][] HTML40_EXTENDED_ESCAPE() { return HTML40_EXTENDED_ESCAPE.clone(); }
    private static final String[][] HTML40_EXTENDED_ESCAPE = {
        
        {"\u0192", "&fnof;"}, 
        
        {"\u0391", "&Alpha;"}, 
        {"\u0392", "&Beta;"}, 
        {"\u0393", "&Gamma;"}, 
        {"\u0394", "&Delta;"}, 
        {"\u0395", "&Epsilon;"}, 
        {"\u0396", "&Zeta;"}, 
        {"\u0397", "&Eta;"}, 
        {"\u0398", "&Theta;"}, 
        {"\u0399", "&Iota;"}, 
        {"\u039A", "&Kappa;"}, 
        {"\u039B", "&Lambda;"}, 
        {"\u039C", "&Mu;"}, 
        {"\u039D", "&Nu;"}, 
        {"\u039E", "&Xi;"}, 
        {"\u039F", "&Omicron;"}, 
        {"\u03A0", "&Pi;"}, 
        {"\u03A1", "&Rho;"}, 
        
        {"\u03A3", "&Sigma;"}, 
        {"\u03A4", "&Tau;"}, 
        {"\u03A5", "&Upsilon;"}, 
        {"\u03A6", "&Phi;"}, 
        {"\u03A7", "&Chi;"}, 
        {"\u03A8", "&Psi;"}, 
        {"\u03A9", "&Omega;"}, 
        {"\u03B1", "&alpha;"}, 
        {"\u03B2", "&beta;"}, 
        {"\u03B3", "&gamma;"}, 
        {"\u03B4", "&delta;"}, 
        {"\u03B5", "&epsilon;"}, 
        {"\u03B6", "&zeta;"}, 
        {"\u03B7", "&eta;"}, 
        {"\u03B8", "&theta;"}, 
        {"\u03B9", "&iota;"}, 
        {"\u03BA", "&kappa;"}, 
        {"\u03BB", "&lambda;"}, 
        {"\u03BC", "&mu;"}, 
        {"\u03BD", "&nu;"}, 
        {"\u03BE", "&xi;"}, 
        {"\u03BF", "&omicron;"}, 
        {"\u03C0", "&pi;"}, 
        {"\u03C1", "&rho;"}, 
        {"\u03C2", "&sigmaf;"}, 
        {"\u03C3", "&sigma;"}, 
        {"\u03C4", "&tau;"}, 
        {"\u03C5", "&upsilon;"}, 
        {"\u03C6", "&phi;"}, 
        {"\u03C7", "&chi;"}, 
        {"\u03C8", "&psi;"}, 
        {"\u03C9", "&omega;"}, 
        {"\u03D1", "&thetasym;"}, 
        {"\u03D2", "&upsih;"}, 
        {"\u03D6", "&piv;"}, 
        
        {"\u2022", "&bull;"}, 
        
        {"\u2026", "&hellip;"}, 
        {"\u2032", "&prime;"}, 
        {"\u2033", "&Prime;"}, 
        {"\u203E", "&oline;"}, 
        {"\u2044", "&frasl;"}, 
        
        {"\u2118", "&weierp;"}, 
        {"\u2111", "&image;"}, 
        {"\u211C", "&real;"}, 
        {"\u2122", "&trade;"}, 
        {"\u2135", "&alefsym;"}, 
        
        
        
        {"\u2190", "&larr;"}, 
        {"\u2191", "&uarr;"}, 
        {"\u2192", "&rarr;"}, 
        {"\u2193", "&darr;"}, 
        {"\u2194", "&harr;"}, 
        {"\u21B5", "&crarr;"}, 
        {"\u21D0", "&lArr;"}, 
        
        
        
        {"\u21D1", "&uArr;"}, 
        {"\u21D2", "&rArr;"}, 
        
        
        
        {"\u21D3", "&dArr;"}, 
        {"\u21D4", "&hArr;"}, 
        
        {"\u2200", "&forall;"}, 
        {"\u2202", "&part;"}, 
        {"\u2203", "&exist;"}, 
        {"\u2205", "&empty;"}, 
        {"\u2207", "&nabla;"}, 
        {"\u2208", "&isin;"}, 
        {"\u2209", "&notin;"}, 
        {"\u220B", "&ni;"}, 
        
        {"\u220F", "&prod;"}, 
        
        
        {"\u2211", "&sum;"}, 
        
        
        {"\u2212", "&minus;"}, 
        {"\u2217", "&lowast;"}, 
        {"\u221A", "&radic;"}, 
        {"\u221D", "&prop;"}, 
        {"\u221E", "&infin;"}, 
        {"\u2220", "&ang;"}, 
        {"\u2227", "&and;"}, 
        {"\u2228", "&or;"}, 
        {"\u2229", "&cap;"}, 
        {"\u222A", "&cup;"}, 
        {"\u222B", "&int;"}, 
        {"\u2234", "&there4;"}, 
        {"\u223C", "&sim;"}, 
        
        
        {"\u2245", "&cong;"}, 
        {"\u2248", "&asymp;"}, 
        {"\u2260", "&ne;"}, 
        {"\u2261", "&equiv;"}, 
        {"\u2264", "&le;"}, 
        {"\u2265", "&ge;"}, 
        {"\u2282", "&sub;"}, 
        {"\u2283", "&sup;"}, 
        
        
        
        
        {"\u2286", "&sube;"}, 
        {"\u2287", "&supe;"}, 
        {"\u2295", "&oplus;"}, 
        {"\u2297", "&otimes;"}, 
        {"\u22A5", "&perp;"}, 
        {"\u22C5", "&sdot;"}, 
        
        
        {"\u2308", "&lceil;"}, 
        {"\u2309", "&rceil;"}, 
        {"\u230A", "&lfloor;"}, 
        {"\u230B", "&rfloor;"}, 
        {"\u2329", "&lang;"}, 
        
        
        {"\u232A", "&rang;"}, 
        
        
        
        {"\u25CA", "&loz;"}, 
        
        {"\u2660", "&spades;"}, 
        
        {"\u2663", "&clubs;"}, 
        {"\u2665", "&hearts;"}, 
        {"\u2666", "&diams;"}, 

        
        {"\u0152", "&OElig;"}, 
        {"\u0153", "&oelig;"}, 
        
        {"\u0160", "&Scaron;"}, 
        {"\u0161", "&scaron;"}, 
        {"\u0178", "&Yuml;"}, 
        
        {"\u02C6", "&circ;"}, 
        {"\u02DC", "&tilde;"}, 
        
        {"\u2002", "&ensp;"}, 
        {"\u2003", "&emsp;"}, 
        {"\u2009", "&thinsp;"}, 
        {"\u200C", "&zwnj;"}, 
        {"\u200D", "&zwj;"}, 
        {"\u200E", "&lrm;"}, 
        {"\u200F", "&rlm;"}, 
        {"\u2013", "&ndash;"}, 
        {"\u2014", "&mdash;"}, 
        {"\u2018", "&lsquo;"}, 
        {"\u2019", "&rsquo;"}, 
        {"\u201A", "&sbquo;"}, 
        {"\u201C", "&ldquo;"}, 
        {"\u201D", "&rdquo;"}, 
        {"\u201E", "&bdquo;"}, 
        {"\u2020", "&dagger;"}, 
        {"\u2021", "&Dagger;"}, 
        {"\u2030", "&permil;"}, 
        {"\u2039", "&lsaquo;"}, 
        
        {"\u203A", "&rsaquo;"}, 
        
        {"\u20AC", "&euro;"}, 
    };

    /**
     * Reverse of {@link #HTML40_EXTENDED_ESCAPE()} for unescaping purposes.
     * @return the mapping table
     */
    public static String[][] HTML40_EXTENDED_UNESCAPE() { return HTML40_EXTENDED_UNESCAPE.clone(); }
    private static final String[][] HTML40_EXTENDED_UNESCAPE = invert(HTML40_EXTENDED_ESCAPE);

    /**
     * Mapping to escape the basic XML and HTML character entities.
     *
     * Namely: {@code " & < >}
     * @return the mapping table
     */
    public static String[][] BASIC_ESCAPE() { return BASIC_ESCAPE.clone(); }
    private static final String[][] BASIC_ESCAPE = {
        {"\"", "&quot;"}, 
        {"&", "&amp;"},   
        {"<", "&lt;"},    
        {">", "&gt;"},    
    };

    /**
     * Reverse of {@link #BASIC_ESCAPE()} for unescaping purposes.
     * @return the mapping table
     */
    public static String[][] BASIC_UNESCAPE() { return BASIC_UNESCAPE.clone(); }
    private static final String[][] BASIC_UNESCAPE = invert(BASIC_ESCAPE);

    /**
     * Mapping to escape the apostrophe character to its XML character entity.
     * @return the mapping table
     */
    public static String[][] APOS_ESCAPE() { return APOS_ESCAPE.clone(); }
    private static final String[][] APOS_ESCAPE = {
        {"'", "&apos;"}, 
    };

    /**
     * Reverse of {@link #APOS_ESCAPE()} for unescaping purposes.
     * @return the mapping table
     */
    public static String[][] APOS_UNESCAPE() { return APOS_UNESCAPE.clone(); }
    private static final String[][] APOS_UNESCAPE = invert(APOS_ESCAPE);

    /**
     * Mapping to escape the Java control characters.
     *
     * Namely: {@code \b \n \t \f \r}
     * @return the mapping table
     */
    public static String[][] JAVA_CTRL_CHARS_ESCAPE() { return JAVA_CTRL_CHARS_ESCAPE.clone(); }
    private static final String[][] JAVA_CTRL_CHARS_ESCAPE = {
        {"\b", "\\b"},
        {"\n", "\\n"},
        {"\t", "\\t"},
        {"\f", "\\f"},
        {"\r", "\\r"}
    };

    /**
     * Reverse of {@link #JAVA_CTRL_CHARS_ESCAPE()} for unescaping purposes.
     * @return the mapping table
     */
    public static String[][] JAVA_CTRL_CHARS_UNESCAPE() { return JAVA_CTRL_CHARS_UNESCAPE.clone(); }
    private static final String[][] JAVA_CTRL_CHARS_UNESCAPE = invert(JAVA_CTRL_CHARS_ESCAPE);

    /**
     * Used to invert an escape array into an unescape array
     * @param array String[][] to be inverted
     * @return String[][] inverted array
     */
    public static String[][] invert(final String[][] array) {
        final String[][] newarray = new String[array.length][2];
        for(int i = 0; i<array.length; i++) {
            newarray[i][0] = array[i][1];
            newarray[i][1] = array[i][0];
        }
        return newarray;
    }

}

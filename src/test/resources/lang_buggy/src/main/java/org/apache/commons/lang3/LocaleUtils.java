















package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;











public class LocaleUtils {

    
    private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry = 
        new ConcurrentHashMap<String, List<Locale>>();

    
    private static final ConcurrentMap<String, List<Locale>> cCountriesByLanguage = 
        new ConcurrentHashMap<String, List<Locale>>();

    
    
    
    
    
    

    public LocaleUtils() {
      super();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public static Locale toLocale(final String str) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len < 2) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        final char ch0 = str.charAt(0);
        if (ch0 == '_') {
            if (len < 3) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            final char ch1 = str.charAt(1);
            final char ch2 = str.charAt(2);
            if (!Character.isUpperCase(ch1) || !Character.isUpperCase(ch2)) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (len == 3) {
                return new Locale("", str.substring(1, 3));
            }
            if (len < 5) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (str.charAt(3) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            return new Locale("", str.substring(1, 3), str.substring(4));
        } else {
            final char ch1 = str.charAt(1);
            if (!Character.isLowerCase(ch0) || !Character.isLowerCase(ch1)) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (len == 2) {
                return new Locale(str);
            }
            if (len < 5) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (str.charAt(2) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            final char ch3 = str.charAt(3);
            if (ch3 == '_') {
                return new Locale(str.substring(0, 2), "", str.substring(4));
            }
            final char ch4 = str.charAt(4);
            if (!Character.isUpperCase(ch3) || !Character.isUpperCase(ch4)) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (len == 5) {
                return new Locale(str.substring(0, 2), str.substring(3, 5));
            }
            if (len < 7) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (str.charAt(5) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            return new Locale(str.substring(0, 2), str.substring(3, 5), str.substring(6));
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    

    public static List<Locale> localeLookupList(final Locale locale) {
        return localeLookupList(locale, locale);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public static List<Locale> localeLookupList(final Locale locale, final Locale defaultLocale) {
        final List<Locale> list = new ArrayList<Locale>(4);
        if (locale != null) {
            list.add(locale);
            if (locale.getVariant().length() > 0) {
                list.add(new Locale(locale.getLanguage(), locale.getCountry()));
            }
            if (locale.getCountry().length() > 0) {
                list.add(new Locale(locale.getLanguage(), ""));
            }
            if (list.contains(defaultLocale) == false) {
                list.add(defaultLocale);
            }
        }
        return Collections.unmodifiableList(list);
    }

    
    
    
    
    
    
    
    
    

    public static List<Locale> availableLocaleList() {
        return SyncAvoid.AVAILABLE_LOCALE_LIST;
    }

    
    
    
    
    
    
    
    
    

    public static Set<Locale> availableLocaleSet() {
        return SyncAvoid.AVAILABLE_LOCALE_SET;
    }

    
    
    
    
    
    

    public static boolean isAvailableLocale(final Locale locale) {
        return availableLocaleList().contains(locale);
    }

    
    
    
    
    
    
    
    
    

    public static List<Locale> languagesByCountry(final String countryCode) {
        if (countryCode == null) {
            return Collections.emptyList();
        }
        List<Locale> langs = cLanguagesByCountry.get(countryCode);
        if (langs == null) {
            langs = new ArrayList<Locale>();
            final List<Locale> locales = availableLocaleList();
            for (int i = 0; i < locales.size(); i++) {
                final Locale locale = locales.get(i);
                if (countryCode.equals(locale.getCountry()) &&
                        locale.getVariant().isEmpty()) {
                    langs.add(locale);
                }
            }
            langs = Collections.unmodifiableList(langs);
            cLanguagesByCountry.putIfAbsent(countryCode, langs);
            langs = cLanguagesByCountry.get(countryCode);
        }
        return langs;
    }

    
    
    
    
    
    
    
    
    

    public static List<Locale> countriesByLanguage(final String languageCode) {
        if (languageCode == null) {
            return Collections.emptyList();
        }
        List<Locale> countries = cCountriesByLanguage.get(languageCode);
        if (countries == null) {
            countries = new ArrayList<Locale>();
            final List<Locale> locales = availableLocaleList();
            for (int i = 0; i < locales.size(); i++) {
                final Locale locale = locales.get(i);
                if (languageCode.equals(locale.getLanguage()) &&
                        locale.getCountry().length() != 0 &&
                        locale.getVariant().isEmpty()) {
                    countries.add(locale);
                }
            }
            countries = Collections.unmodifiableList(countries);
            cCountriesByLanguage.putIfAbsent(languageCode, countries);
            countries = cCountriesByLanguage.get(languageCode);
        }
        return countries;
    }

    
    
    static class SyncAvoid {
        
        private static final List<Locale> AVAILABLE_LOCALE_LIST;
        
        private static final Set<Locale> AVAILABLE_LOCALE_SET;
        
        static {
            final List<Locale> list = new ArrayList<Locale>(Arrays.asList(Locale.getAvailableLocales()));  
            AVAILABLE_LOCALE_LIST = Collections.unmodifiableList(list);
            AVAILABLE_LOCALE_SET = Collections.unmodifiableSet(new HashSet<Locale>(list));
        }
    }

}

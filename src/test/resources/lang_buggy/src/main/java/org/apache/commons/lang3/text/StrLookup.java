















package org.apache.commons.lang3.text;

import java.util.Map;

















public abstract class StrLookup<V> {




    private static final StrLookup<String> NONE_LOOKUP;



    private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP;
    static {
        NONE_LOOKUP = new MapStrLookup<String>(null);
        StrLookup<String> lookup = null;
        try {
            final Map<?, ?> propMap = System.getProperties();
            @SuppressWarnings("unchecked") 
            final Map<String, String> properties = (Map<String, String>) propMap;
            lookup = new MapStrLookup<String>(properties);
        } catch (final SecurityException ex) {
            lookup = NONE_LOOKUP;
        }
        SYSTEM_PROPERTIES_LOOKUP = lookup;
    }

    





    public static StrLookup<?> noneLookup() {
        return NONE_LOOKUP;
    }












    public static StrLookup<String> systemPropertiesLookup() {
        return SYSTEM_PROPERTIES_LOOKUP;
    }











    public static <V> StrLookup<V> mapLookup(final Map<String, V> map) {
        return new MapStrLookup<V>(map);
    }

    



    protected StrLookup() {
        super();
    }
























    public abstract String lookup(String key);

    



    static class MapStrLookup<V> extends StrLookup<V> {

        
        private final Map<String, V> map;






        MapStrLookup(final Map<String, V> map) {
            this.map = map;
        }










        @Override
        public String lookup(final String key) {
            if (map == null) {
                return null;
            }
            final Object obj = map.get(key);
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }
    }
}

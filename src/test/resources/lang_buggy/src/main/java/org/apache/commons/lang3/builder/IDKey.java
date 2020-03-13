

















package org.apache.commons.lang3.builder;










 
final class IDKey {
        private final Object value;
        private final int id;




 
        public IDKey(final Object _value) {
            
            id = System.identityHashCode(_value);  
            
            
            
            value = _value;
        }




 
        @Override
        public int hashCode() {
           return id;
        }





 
        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof IDKey)) {
                return false;
            }
            final IDKey idKey = (IDKey) other;
            if (id != idKey.id) {
                return false;
            }
            
            return value == idKey.value;
         }
}

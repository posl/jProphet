















package org.apache.commons.lang3.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;












public class DefaultExceptionContext implements ExceptionContext, Serializable {

    
    private static final long serialVersionUID = 20110706L;

    
    private final List<Pair<String, Object>> contextValues = new ArrayList<Pair<String,Object>>();




    @Override
    public DefaultExceptionContext addContextValue(final String label, final Object value) {
        contextValues.add(new ImmutablePair<String, Object>(label, value));
        return this;
    }




    @Override
    public DefaultExceptionContext setContextValue(final String label, final Object value) {
        for (final Iterator<Pair<String, Object>> iter = contextValues.iterator(); iter.hasNext();) {
            final Pair<String, Object> p = iter.next();
            if (StringUtils.equals(label, p.getKey())) {
                iter.remove();
            }
        }
        addContextValue(label, value);
        return this;
    }




    @Override
    public List<Object> getContextValues(final String label) {
        final List<Object> values = new ArrayList<Object>();
        for (final Pair<String, Object> pair : contextValues) {
            if (StringUtils.equals(label, pair.getKey())) {
                values.add(pair.getValue());
            }
        }
        return values;
    }




    @Override
    public Object getFirstContextValue(final String label) {
        for (final Pair<String, Object> pair : contextValues) {
            if (StringUtils.equals(label, pair.getKey())) {
                return pair.getValue();
            }
        }
        return null;
    }




    @Override
    public Set<String> getContextLabels() {
        final Set<String> labels = new HashSet<String>();
        for (final Pair<String, Object> pair : contextValues) {
            labels.add(pair.getKey());
        }
        return labels;
    }




    @Override
    public List<Pair<String, Object>> getContextEntries() {
        return contextValues;
    }







    @Override
    public String getFormattedExceptionMessage(final String baseMessage){
        final StringBuilder buffer = new StringBuilder(256);
        if (baseMessage != null) {
            buffer.append(baseMessage);
        }
        
        if (contextValues.size() > 0) {
            if (buffer.length() > 0) {
                buffer.append('\n');
            }
            buffer.append("Exception Context:\n");
            
            int i = 0;
            for (final Pair<String, Object> pair : contextValues) {
                buffer.append("\t[");
                buffer.append(++i);
                buffer.append(':');
                buffer.append(pair.getKey());
                buffer.append("=");
                final Object value = pair.getValue();
                if (value == null) {
                    buffer.append("null");
                } else {
                    String valueStr;
                    try {
                        valueStr = value.toString();
                    } catch (final Exception e) {
                        valueStr = "Exception thrown on toString(): " + ExceptionUtils.getStackTrace(e);
                    }
                    buffer.append(valueStr);
                }
                buffer.append("]\n");
            }
            buffer.append("---------------------------------");
        }
        return buffer.toString();
    }

}

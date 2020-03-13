















package org.apache.commons.lang3.exception;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;





























































public class ContextedRuntimeException extends RuntimeException implements ExceptionContext {

    
    private static final long serialVersionUID = 20110706L;
    
    private final ExceptionContext exceptionContext;






    public ContextedRuntimeException() {
        super();
        exceptionContext = new DefaultExceptionContext();
    }








    public ContextedRuntimeException(final String message) {
        super(message);
        exceptionContext = new DefaultExceptionContext();
    }








    public ContextedRuntimeException(final Throwable cause) {
        super(cause);
        exceptionContext = new DefaultExceptionContext();
    }









    public ContextedRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
        exceptionContext = new DefaultExceptionContext();
    }








    public ContextedRuntimeException(final String message, final Throwable cause, ExceptionContext context) {
        super(message, cause);
        if (context == null) {
            context = new DefaultExceptionContext();
        }
        exceptionContext = context;
    }

    













    @Override
    public ContextedRuntimeException addContextValue(final String label, final Object value) {        
        exceptionContext.addContextValue(label, value);
        return this;
    }














    @Override
    public ContextedRuntimeException setContextValue(final String label, final Object value) {        
        exceptionContext.setContextValue(label, value);
        return this;
    }




    @Override
    public List<Object> getContextValues(final String label) {
        return this.exceptionContext.getContextValues(label);
    }




    @Override
    public Object getFirstContextValue(final String label) {
        return this.exceptionContext.getFirstContextValue(label);
    }




    @Override
    public List<Pair<String, Object>> getContextEntries() {
        return this.exceptionContext.getContextEntries();
    }




    @Override
    public Set<String> getContextLabels() {
        return exceptionContext.getContextLabels();
    }







    @Override
    public String getMessage(){
        return getFormattedExceptionMessage(super.getMessage());
    }








    public String getRawMessage() {
        return super.getMessage();
    }




    @Override
    public String getFormattedExceptionMessage(final String baseMessage) {
        return exceptionContext.getFormattedExceptionMessage(baseMessage);
    }

}

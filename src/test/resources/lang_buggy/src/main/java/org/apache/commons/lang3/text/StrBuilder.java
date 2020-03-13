















package org.apache.commons.lang3.text;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.Builder;














































public class StrBuilder implements CharSequence, Appendable, Serializable, Builder<String> {




    static final int CAPACITY = 32;






    private static final long serialVersionUID = 7628716375283629643L;

    
    protected char[] buffer; 
    
    protected int size; 
    
    private String newLine;
    
    private String nullText;

    



    public StrBuilder() {
        this(CAPACITY);
    }






    public StrBuilder(int initialCapacity) {
        super();
        if (initialCapacity <= 0) {
            initialCapacity = CAPACITY;
        }
        buffer = new char[initialCapacity];
    }







    public StrBuilder(final String str) {
        super();
        if (str == null) {
            buffer = new char[CAPACITY];
        } else {
            buffer = new char[str.length() + CAPACITY];
            append(str);
        }
    }

    





    public String getNewLineText() {
        return newLine;
    }







    public StrBuilder setNewLineText(final String newLine) {
        this.newLine = newLine;
        return this;
    }

    





    public String getNullText() {
        return nullText;
    }







    public StrBuilder setNullText(String nullText) {
        if (nullText != null && nullText.length() == 0) {
            nullText = null;
        }
        this.nullText = nullText;
        return this;
    }

    





    @Override
    public int length() {
        return size;
    }









    public StrBuilder setLength(final int length) {
        if (length < 0) {
            throw new StringIndexOutOfBoundsException(length);
        }
        if (length < size) {
            size = length;
        } else if (length > size) {
            ensureCapacity(length);
            final int oldEnd = size;
            final int newEnd = length;
            size = length;
            for (int i = oldEnd; i < newEnd; i++) {
                buffer[i] = '\0';
            }
        }
        return this;
    }

    





    public int capacity() {
        return buffer.length;
    }







    public StrBuilder ensureCapacity(final int capacity) {
        if (capacity > buffer.length) {
            final char[] old = buffer;
            buffer = new char[capacity * 2];
            System.arraycopy(old, 0, buffer, 0, size);
        }
        return this;
    }






    public StrBuilder minimizeCapacity() {
        if (buffer.length > length()) {
            final char[] old = buffer;
            buffer = new char[length()];
            System.arraycopy(old, 0, buffer, 0, size);
        }
        return this;
    }

    








    public int size() {
        return size;
    }









    public boolean isEmpty() {
        return size == 0;
    }












    public StrBuilder clear() {
        size = 0;
        return this;
    }

    









    @Override
    public char charAt(final int index) {
        if (index < 0 || index >= length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return buffer[index];
    }











    public StrBuilder setCharAt(final int index, final char ch) {
        if (index < 0 || index >= length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        buffer[index] = ch;
        return this;
    }










    public StrBuilder deleteCharAt(final int index) {
        if (index < 0 || index >= size) {
            throw new StringIndexOutOfBoundsException(index);
        }
        deleteImpl(index, index + 1, 1);
        return this;
    }

    





    public char[] toCharArray() {
        if (size == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char chars[] = new char[size];
        System.arraycopy(buffer, 0, chars, 0, size);
        return chars;
    }











    public char[] toCharArray(final int startIndex, int endIndex) {
        endIndex = validateRange(startIndex, endIndex);
        final int len = endIndex - startIndex;
        if (len == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char chars[] = new char[len];
        System.arraycopy(buffer, startIndex, chars, 0, len);
        return chars;
    }







    public char[] getChars(char[] destination) {
        final int len = length();
        if (destination == null || destination.length < len) {
            destination = new char[len];
        }
        System.arraycopy(buffer, 0, destination, 0, len);
        return destination;
    }











    public void getChars(final int startIndex, final int endIndex, final char destination[], final int destinationIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        }
        if (endIndex < 0 || endIndex > length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        if (startIndex > endIndex) {
            throw new StringIndexOutOfBoundsException("end < start");
        }
        System.arraycopy(buffer, startIndex, destination, destinationIndex, endIndex - startIndex);
    }

    









    public StrBuilder appendNewLine() {
        if (newLine == null)  {
            append(SystemUtils.LINE_SEPARATOR);
            return this;
        }
        return append(newLine);
    }






    public StrBuilder appendNull() {
        if (nullText == null)  {
            return this;
        }
        return append(nullText);
    }








    public StrBuilder append(final Object obj) {
        if (obj == null) {
            return appendNull();
        } 
        return append(obj.toString());        
    }









    @Override
    public StrBuilder append(final CharSequence seq) {
        if (seq == null) {
            return appendNull();
        } 
        return append(seq.toString());        
    }











    @Override
    public StrBuilder append(final CharSequence seq, final int startIndex, final int length) {
        if (seq == null) {
            return appendNull();
        } 
        return append(seq.toString(), startIndex, length);
    }
    







    public StrBuilder append(final String str) {
        if (str == null) {
            return appendNull();
        }
        final int strLen = str.length();
        if (strLen > 0) {
            final int len = length();
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, buffer, len);
            size += strLen;
        }
        return this;
    }
   










    public StrBuilder append(final String str, final int startIndex, final int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || (startIndex + length) > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            final int len = length();
            ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, buffer, len);
            size += length;
        }
        return this;
    }










    public StrBuilder append(final String format, final Object... objs) {
        return append(String.format(format, objs));
    }








    public StrBuilder append(final StringBuffer str) {
        if (str == null) {
            return appendNull();
        }
        final int strLen = str.length();
        if (strLen > 0) {
            final int len = length();
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, buffer, len);
            size += strLen;
        }
        return this;
    }










    public StrBuilder append(final StringBuffer str, final int startIndex, final int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || (startIndex + length) > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            final int len = length();
            ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, buffer, len);
            size += length;
        }
        return this;
    }









    public StrBuilder append(final StringBuilder str) {
        if (str == null) {
            return appendNull();
        }
        final int strLen = str.length();
        if (strLen > 0) {
            final int len = length();
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, buffer, len);
            size += strLen;
        }
        return this;
    }
    










    public StrBuilder append(final StringBuilder str, final int startIndex, final int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || (startIndex + length) > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            final int len = length();
            ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, buffer, len);
            size += length;
        }
        return this;
    }








    public StrBuilder append(final StrBuilder str) {
        if (str == null) {
            return appendNull();
        }
        final int strLen = str.length();
        if (strLen > 0) {
            final int len = length();
            ensureCapacity(len + strLen);
            System.arraycopy(str.buffer, 0, buffer, len, strLen);
            size += strLen;
        }
        return this;
    }










    public StrBuilder append(final StrBuilder str, final int startIndex, final int length) {
        if (str == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || (startIndex + length) > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            final int len = length();
            ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, buffer, len);
            size += length;
        }
        return this;
    }








    public StrBuilder append(final char[] chars) {
        if (chars == null) {
            return appendNull();
        }
        final int strLen = chars.length;
        if (strLen > 0) {
            final int len = length();
            ensureCapacity(len + strLen);
            System.arraycopy(chars, 0, buffer, len, strLen);
            size += strLen;
        }
        return this;
    }










    public StrBuilder append(final char[] chars, final int startIndex, final int length) {
        if (chars == null) {
            return appendNull();
        }
        if (startIndex < 0 || startIndex > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid startIndex: " + length);
        }
        if (length < 0 || (startIndex + length) > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid length: " + length);
        }
        if (length > 0) {
            final int len = length();
            ensureCapacity(len + length);
            System.arraycopy(chars, startIndex, buffer, len, length);
            size += length;
        }
        return this;
    }







    public StrBuilder append(final boolean value) {
        if (value) {
            ensureCapacity(size + 4);
            buffer[size++] = 't';
            buffer[size++] = 'r';
            buffer[size++] = 'u';
            buffer[size++] = 'e';
        } else {
            ensureCapacity(size + 5);
            buffer[size++] = 'f';
            buffer[size++] = 'a';
            buffer[size++] = 'l';
            buffer[size++] = 's';
            buffer[size++] = 'e';
        }
        return this;
    }








    @Override
    public StrBuilder append(final char ch) {
        final int len = length();
        ensureCapacity(len + 1);
        buffer[size++] = ch;
        return this;
    }







    public StrBuilder append(final int value) {
        return append(String.valueOf(value));
    }







    public StrBuilder append(final long value) {
        return append(String.valueOf(value));
    }







    public StrBuilder append(final float value) {
        return append(String.valueOf(value));
    }







    public StrBuilder append(final double value) {
        return append(String.valueOf(value));
    }

    








    public StrBuilder appendln(final Object obj) {
        return append(obj).appendNewLine();
    }









    public StrBuilder appendln(final String str) {
        return append(str).appendNewLine();
    }











    public StrBuilder appendln(final String str, final int startIndex, final int length) {
        return append(str, startIndex, length).appendNewLine();
    }










    public StrBuilder appendln(final String format, final Object... objs) {
        return append(format, objs).appendNewLine();
    }









    public StrBuilder appendln(final StringBuffer str) {
        return append(str).appendNewLine();
    }









    public StrBuilder appendln(final StringBuilder str) {
        return append(str).appendNewLine();
    }











    public StrBuilder appendln(final StringBuilder str, final int startIndex, final int length) {
        return append(str, startIndex, length).appendNewLine();
    }











    public StrBuilder appendln(final StringBuffer str, final int startIndex, final int length) {
        return append(str, startIndex, length).appendNewLine();
    }









    public StrBuilder appendln(final StrBuilder str) {
        return append(str).appendNewLine();
    }











    public StrBuilder appendln(final StrBuilder str, final int startIndex, final int length) {
        return append(str, startIndex, length).appendNewLine();
    }









    public StrBuilder appendln(final char[] chars) {
        return append(chars).appendNewLine();
    }











    public StrBuilder appendln(final char[] chars, final int startIndex, final int length) {
        return append(chars, startIndex, length).appendNewLine();
    }








    public StrBuilder appendln(final boolean value) {
        return append(value).appendNewLine();
    }








    public StrBuilder appendln(final char ch) {
        return append(ch).appendNewLine();
    }








    public StrBuilder appendln(final int value) {
        return append(value).appendNewLine();
    }








    public StrBuilder appendln(final long value) {
        return append(value).appendNewLine();
    }








    public StrBuilder appendln(final float value) {
        return append(value).appendNewLine();
    }








    public StrBuilder appendln(final double value) {
        return append(value).appendNewLine();
    }

    










    public <T> StrBuilder appendAll(final T... array) {
        if (array != null && array.length > 0) {
            for (final Object element : array) {
                append(element);
            }
        }
        return this;
    }










    public StrBuilder appendAll(final Iterable<?> iterable) {
        if (iterable != null) {
            for (final Object o : iterable) {
                append(o);
            }
        }
        return this;
    }










    public StrBuilder appendAll(final Iterator<?> it) {
        if (it != null) {
            while (it.hasNext()) {
                append(it.next());
            }
        }
        return this;
    }

    










    public StrBuilder appendWithSeparators(final Object[] array, String separator) {
        if (array != null && array.length > 0) {
            separator = ObjectUtils.toString(separator);
            append(array[0]);
            for (int i = 1; i < array.length; i++) {
                append(separator);
                append(array[i]);
            }
        }
        return this;
    }











    public StrBuilder appendWithSeparators(final Iterable<?> iterable, String separator) {
        if (iterable != null) {
            separator = ObjectUtils.toString(separator);
            final Iterator<?> it = iterable.iterator();
            while (it.hasNext()) {
                append(it.next());
                if (it.hasNext()) {
                    append(separator);
                }
            }
        }
        return this;
    }











    public StrBuilder appendWithSeparators(final Iterator<?> it, String separator) {
        if (it != null) {
            separator = ObjectUtils.toString(separator);
            while (it.hasNext()) {
                append(it.next());
                if (it.hasNext()) {
                    append(separator);
                }
            }
        }
        return this;
    }

    




















    public StrBuilder appendSeparator(final String separator) {
        return appendSeparator(separator, null);
    }




























    public StrBuilder appendSeparator(final String standard, final String defaultIfEmpty) {
        final String str = isEmpty() ? defaultIfEmpty : standard;
        if (str != null) {
            append(str);
        }
        return this;
    }




















    public StrBuilder appendSeparator(final char separator) {
        if (size() > 0) {
            append(separator);
        }
        return this;
    }












    public StrBuilder appendSeparator(final char standard, final char defaultIfEmpty) {
        if (size() > 0) {
            append(standard);
        } else {
            append(defaultIfEmpty);
        }
        return this;
    }





















    public StrBuilder appendSeparator(final String separator, final int loopIndex) {
        if (separator != null && loopIndex > 0) {
            append(separator);
        }
        return this;
    }





















    public StrBuilder appendSeparator(final char separator, final int loopIndex) {
        if (loopIndex > 0) {
            append(separator);
        }
        return this;
    }

    







    public StrBuilder appendPadding(final int length, final char padChar) {
        if (length >= 0) {
            ensureCapacity(size + length);
            for (int i = 0; i < length; i++) {
                buffer[size++] = padChar;
            }
        }
        return this;
    }

    











    public StrBuilder appendFixedWidthPadLeft(final Object obj, final int width, final char padChar) {
        if (width > 0) {
            ensureCapacity(size + width);
            String str = (obj == null ? getNullText() : obj.toString());
            if (str == null) {
                str = "";
            }
            final int strLen = str.length();
            if (strLen >= width) {
                str.getChars(strLen - width, strLen, buffer, size);
            } else {
                final int padLen = width - strLen;
                for (int i = 0; i < padLen; i++) {
                    buffer[size + i] = padChar;
                }
                str.getChars(0, strLen, buffer, size + padLen);
            }
            size += width;
        }
        return this;
    }











    public StrBuilder appendFixedWidthPadLeft(final int value, final int width, final char padChar) {
        return appendFixedWidthPadLeft(String.valueOf(value), width, padChar);
    }












    public StrBuilder appendFixedWidthPadRight(final Object obj, final int width, final char padChar) {
        if (width > 0) {
            ensureCapacity(size + width);
            String str = (obj == null ? getNullText() : obj.toString());
            if (str == null) {
                str = "";
            }
            final int strLen = str.length();
            if (strLen >= width) {
                str.getChars(0, width, buffer, size);
            } else {
                final int padLen = width - strLen;
                str.getChars(0, strLen, buffer, size);
                for (int i = 0; i < padLen; i++) {
                    buffer[size + strLen + i] = padChar;
                }
            }
            size += width;
        }
        return this;
    }











    public StrBuilder appendFixedWidthPadRight(final int value, final int width, final char padChar) {
        return appendFixedWidthPadRight(String.valueOf(value), width, padChar);
    }

    









    public StrBuilder insert(final int index, final Object obj) {
        if (obj == null) {
            return insert(index, nullText);
        }
        return insert(index, obj.toString());
    }










    public StrBuilder insert(final int index, String str) {
        validateIndex(index);
        if (str == null) {
            str = nullText;
        }
        if (str != null) {
            final int strLen = str.length();
            if (strLen > 0) {
                final int newSize = size + strLen;
                ensureCapacity(newSize);
                System.arraycopy(buffer, index, buffer, index + strLen, size - index);
                size = newSize;
                str.getChars(0, strLen, buffer, index);
            }
        }
        return this;
    }










    public StrBuilder insert(final int index, final char chars[]) {
        validateIndex(index);
        if (chars == null) {
            return insert(index, nullText);
        }
        final int len = chars.length;
        if (len > 0) {
            ensureCapacity(size + len);
            System.arraycopy(buffer, index, buffer, index + len, size - index);
            System.arraycopy(chars, 0, buffer, index, len);
            size += len;
        }
        return this;
    }












    public StrBuilder insert(final int index, final char chars[], final int offset, final int length) {
        validateIndex(index);
        if (chars == null) {
            return insert(index, nullText);
        }
        if (offset < 0 || offset > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid offset: " + offset);
        }
        if (length < 0 || offset + length > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid length: " + length);
        }
        if (length > 0) {
            ensureCapacity(size + length);
            System.arraycopy(buffer, index, buffer, index + length, size - index);
            System.arraycopy(chars, offset, buffer, index, length);
            size += length;
        }
        return this;
    }









    public StrBuilder insert(int index, final boolean value) {
        validateIndex(index);
        if (value) {
            ensureCapacity(size + 4);
            System.arraycopy(buffer, index, buffer, index + 4, size - index);
            buffer[index++] = 't';
            buffer[index++] = 'r';
            buffer[index++] = 'u';
            buffer[index] = 'e';
            size += 4;
        } else {
            ensureCapacity(size + 5);
            System.arraycopy(buffer, index, buffer, index + 5, size - index);
            buffer[index++] = 'f';
            buffer[index++] = 'a';
            buffer[index++] = 'l';
            buffer[index++] = 's';
            buffer[index] = 'e';
            size += 5;
        }
        return this;
    }









    public StrBuilder insert(final int index, final char value) {
        validateIndex(index);
        ensureCapacity(size + 1);
        System.arraycopy(buffer, index, buffer, index + 1, size - index);
        buffer[index] = value;
        size++;
        return this;
    }









    public StrBuilder insert(final int index, final int value) {
        return insert(index, String.valueOf(value));
    }









    public StrBuilder insert(final int index, final long value) {
        return insert(index, String.valueOf(value));
    }









    public StrBuilder insert(final int index, final float value) {
        return insert(index, String.valueOf(value));
    }









    public StrBuilder insert(final int index, final double value) {
        return insert(index, String.valueOf(value));
    }

    








    private void deleteImpl(final int startIndex, final int endIndex, final int len) {
        System.arraycopy(buffer, endIndex, buffer, startIndex, size - endIndex);
        size -= len;
    }










    public StrBuilder delete(final int startIndex, int endIndex) {
        endIndex = validateRange(startIndex, endIndex);
        final int len = endIndex - startIndex;
        if (len > 0) {
            deleteImpl(startIndex, endIndex, len);
        }
        return this;
    }

    






    public StrBuilder deleteAll(final char ch) {
        for (int i = 0; i < size; i++) {
            if (buffer[i] == ch) {
                final int start = i;
                while (++i < size) {
                    if (buffer[i] != ch) {
                        break;
                    }
                }
                final int len = i - start;
                deleteImpl(start, i, len);
                i -= len;
            }
        }
        return this;
    }







    public StrBuilder deleteFirst(final char ch) {
        for (int i = 0; i < size; i++) {
            if (buffer[i] == ch) {
                deleteImpl(i, i + 1, 1);
                break;
            }
        }
        return this;
    }

    






    public StrBuilder deleteAll(final String str) {
        final int len = (str == null ? 0 : str.length());
        if (len > 0) {
            int index = indexOf(str, 0);
            while (index >= 0) {
                deleteImpl(index, index + len, len);
                index = indexOf(str, index);
            }
        }
        return this;
    }







    public StrBuilder deleteFirst(final String str) {
        final int len = (str == null ? 0 : str.length());
        if (len > 0) {
            final int index = indexOf(str, 0);
            if (index >= 0) {
                deleteImpl(index, index + len, len);
            }
        }
        return this;
    }

    










    public StrBuilder deleteAll(final StrMatcher matcher) {
        return replace(matcher, null, 0, size, -1);
    }











    public StrBuilder deleteFirst(final StrMatcher matcher) {
        return replace(matcher, null, 0, size, 1);
    }

    










    private void replaceImpl(final int startIndex, final int endIndex, final int removeLen, final String insertStr, final int insertLen) {
        final int newSize = size - removeLen + insertLen;
        if (insertLen != removeLen) {
            ensureCapacity(newSize);
            System.arraycopy(buffer, endIndex, buffer, startIndex + insertLen, size - endIndex);
            size = newSize;
        }
        if (insertLen > 0) {
            insertStr.getChars(0, insertLen, buffer, startIndex);
        }
    }












    public StrBuilder replace(final int startIndex, int endIndex, final String replaceStr) {
        endIndex = validateRange(startIndex, endIndex);
        final int insertLen = (replaceStr == null ? 0 : replaceStr.length());
        replaceImpl(startIndex, endIndex, endIndex - startIndex, replaceStr, insertLen);
        return this;
    }

    








    public StrBuilder replaceAll(final char search, final char replace) {
        if (search != replace) {
            for (int i = 0; i < size; i++) {
                if (buffer[i] == search) {
                    buffer[i] = replace;
                }
            }
        }
        return this;
    }









    public StrBuilder replaceFirst(final char search, final char replace) {
        if (search != replace) {
            for (int i = 0; i < size; i++) {
                if (buffer[i] == search) {
                    buffer[i] = replace;
                    break;
                }
            }
        }
        return this;
    }

    







    public StrBuilder replaceAll(final String searchStr, final String replaceStr) {
        final int searchLen = (searchStr == null ? 0 : searchStr.length());
        if (searchLen > 0) {
            final int replaceLen = (replaceStr == null ? 0 : replaceStr.length());
            int index = indexOf(searchStr, 0);
            while (index >= 0) {
                replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceLen);
                index = indexOf(searchStr, index + replaceLen);
            }
        }
        return this;
    }








    public StrBuilder replaceFirst(final String searchStr, final String replaceStr) {
        final int searchLen = (searchStr == null ? 0 : searchStr.length());
        if (searchLen > 0) {
            final int index = indexOf(searchStr, 0);
            if (index >= 0) {
                final int replaceLen = (replaceStr == null ? 0 : replaceStr.length());
                replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceLen);
            }
        }
        return this;
    }

    











    public StrBuilder replaceAll(final StrMatcher matcher, final String replaceStr) {
        return replace(matcher, replaceStr, 0, size, -1);
    }












    public StrBuilder replaceFirst(final StrMatcher matcher, final String replaceStr) {
        return replace(matcher, replaceStr, 0, size, 1);
    }

    
















    public StrBuilder replace(
            final StrMatcher matcher, final String replaceStr,
            final int startIndex, int endIndex, final int replaceCount) {
        endIndex = validateRange(startIndex, endIndex);
        return replaceImpl(matcher, replaceStr, startIndex, endIndex, replaceCount);
    }
















    private StrBuilder replaceImpl(
            final StrMatcher matcher, final String replaceStr,
            final int from, int to, int replaceCount) {
        if (matcher == null || size == 0) {
            return this;
        }
        final int replaceLen = (replaceStr == null ? 0 : replaceStr.length());
        final char[] buf = buffer;
        for (int i = from; i < to && replaceCount != 0; i++) {
            final int removeLen = matcher.isMatch(buf, i, from, to);
            if (removeLen > 0) {
                replaceImpl(i, i + removeLen, removeLen, replaceStr, replaceLen);
                to = to - removeLen + replaceLen;
                i = i + replaceLen - 1;
                if (replaceCount > 0) {
                    replaceCount--;
                }
            }
        }
        return this;
    }

    





    public StrBuilder reverse() {
        if (size == 0) {
            return this;
        }
        
        final int half = size / 2;
        final char[] buf = buffer;
        for (int leftIdx = 0, rightIdx = size - 1; leftIdx < half; leftIdx++,rightIdx--) {
            final char swap = buf[leftIdx];
            buf[leftIdx] = buf[rightIdx];
            buf[rightIdx] = swap;
        }
        return this;
    }

    






    public StrBuilder trim() {
        if (size == 0) {
            return this;
        }
        int len = size;
        final char[] buf = buffer;
        int pos = 0;
        while (pos < len && buf[pos] <= ' ') {
            pos++;
        }
        while (pos < len && buf[len - 1] <= ' ') {
            len--;
        }
        if (len < size) {
            delete(len, size);
        }
        if (pos > 0) {
            delete(0, pos);
        }
        return this;
    }

    








    public boolean startsWith(final String str) {
        if (str == null) {
            return false;
        }
        final int len = str.length();
        if (len == 0) {
            return true;
        }
        if (len > size) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (buffer[i] != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }









    public boolean endsWith(final String str) {
        if (str == null) {
            return false;
        }
        final int len = str.length();
        if (len == 0) {
            return true;
        }
        if (len > size) {
            return false;
        }
        int pos = size - len;
        for (int i = 0; i < len; i++,pos++) {
            if (buffer[pos] != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    




    @Override
    public CharSequence subSequence(final int startIndex, final int endIndex) {
      if (startIndex < 0) {
          throw new StringIndexOutOfBoundsException(startIndex);
      }
      if (endIndex > size) {
          throw new StringIndexOutOfBoundsException(endIndex);
      }
      if (startIndex > endIndex) {
          throw new StringIndexOutOfBoundsException(endIndex - startIndex);
      }
      return substring(startIndex, endIndex);
    }








    public String substring(final int start) {
        return substring(start, size);
    }














    public String substring(final int startIndex, int endIndex) {
        endIndex = validateRange(startIndex, endIndex);
        return new String(buffer, startIndex, endIndex - startIndex);
    }













    public String leftString(final int length) {
        if (length <= 0) {
            return "";
        } else if (length >= size) {
            return new String(buffer, 0, size);
        } else {
            return new String(buffer, 0, length);
        }
    }













    public String rightString(final int length) {
        if (length <= 0) {
            return "";
        } else if (length >= size) {
            return new String(buffer, 0, size);
        } else {
            return new String(buffer, size - length, length);
        }
    }

















    public String midString(int index, final int length) {
        if (index < 0) {
            index = 0;
        }
        if (length <= 0 || index >= size) {
            return "";
        }
        if (size <= index + length) {
            return new String(buffer, index, size - index);
        } else {
            return new String(buffer, index, length);
        }
    }

    






    public boolean contains(final char ch) {
        final char[] thisBuf = buffer;
        for (int i = 0; i < this.size; i++) {
            if (thisBuf[i] == ch) {
                return true;
            }
        }
        return false;
    }







    public boolean contains(final String str) {
        return indexOf(str, 0) >= 0;
    }












    public boolean contains(final StrMatcher matcher) {
        return indexOf(matcher, 0) >= 0;
    }

    






    public int indexOf(final char ch) {
        return indexOf(ch, 0);
    }








    public int indexOf(final char ch, int startIndex) {
        startIndex = (startIndex < 0 ? 0 : startIndex);
        if (startIndex >= size) {
            return -1;
        }
        final char[] thisBuf = buffer;
        for (int i = startIndex; i < size; i++) {
            if (thisBuf[i] == ch) {
                return i;
            }
        }
        return -1;
    }









    public int indexOf(final String str) {
        return indexOf(str, 0);
    }











    public int indexOf(final String str, int startIndex) {
        startIndex = (startIndex < 0 ? 0 : startIndex);
        if (str == null || startIndex >= size) {
            return -1;
        }
        final int strLen = str.length();
        if (strLen == 1) {
            return indexOf(str.charAt(0), startIndex);
        }
        if (strLen == 0) {
            return startIndex;
        }
        if (strLen > size) {
            return -1;
        }
        final char[] thisBuf = buffer;
        final int len = size - strLen + 1;
        outer:
        for (int i = startIndex; i < len; i++) {
            for (int j = 0; j < strLen; j++) {
                if (str.charAt(j) != thisBuf[i + j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }











    public int indexOf(final StrMatcher matcher) {
        return indexOf(matcher, 0);
    }













    public int indexOf(final StrMatcher matcher, int startIndex) {
        startIndex = (startIndex < 0 ? 0 : startIndex);
        if (matcher == null || startIndex >= size) {
            return -1;
        }
        final int len = size;
        final char[] buf = buffer;
        for (int i = startIndex; i < len; i++) {
            if (matcher.isMatch(buf, i, startIndex, len) > 0) {
                return i;
            }
        }
        return -1;
    }

    






    public int lastIndexOf(final char ch) {
        return lastIndexOf(ch, size - 1);
    }








    public int lastIndexOf(final char ch, int startIndex) {
        startIndex = (startIndex >= size ? size - 1 : startIndex);
        if (startIndex < 0) {
            return -1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (buffer[i] == ch) {
                return i;
            }
        }
        return -1;
    }









    public int lastIndexOf(final String str) {
        return lastIndexOf(str, size - 1);
    }











    public int lastIndexOf(final String str, int startIndex) {
        startIndex = (startIndex >= size ? size - 1 : startIndex);
        if (str == null || startIndex < 0) {
            return -1;
        }
        final int strLen = str.length();
        if (strLen > 0 && strLen <= size) {
            if (strLen == 1) {
                return lastIndexOf(str.charAt(0), startIndex);
            }

            outer:
            for (int i = startIndex - strLen + 1; i >= 0; i--) {
                for (int j = 0; j < strLen; j++) {
                    if (str.charAt(j) != buffer[i + j]) {
                        continue outer;
                    }
                }
                return i;
            }
            
        } else if (strLen == 0) {
            return startIndex;
        }
        return -1;
    }











    public int lastIndexOf(final StrMatcher matcher) {
        return lastIndexOf(matcher, size);
    }













    public int lastIndexOf(final StrMatcher matcher, int startIndex) {
        startIndex = (startIndex >= size ? size - 1 : startIndex);
        if (matcher == null || startIndex < 0) {
            return -1;
        }
        final char[] buf = buffer;
        final int endIndex = startIndex + 1;
        for (int i = startIndex; i >= 0; i--) {
            if (matcher.isMatch(buf, i, 0, endIndex) > 0) {
                return i;
            }
        }
        return -1;
    }

    
































    public StrTokenizer asTokenizer() {
        return new StrBuilderTokenizer();
    }

    



















    public Reader asReader() {
        return new StrBuilderReader();
    }

    




















    public Writer asWriter() {
        return new StrBuilderWriter();
    }

    





























    







    public boolean equalsIgnoreCase(final StrBuilder other) {
        if (this == other) {
            return true;
        }
        if (this.size != other.size) {
            return false;
        }
        final char thisBuf[] = this.buffer;
        final char otherBuf[] = other.buffer;
        for (int i = size - 1; i >= 0; i--) {
            final char c1 = thisBuf[i];
            final char c2 = otherBuf[i];
            if (c1 != c2 && Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
                return false;
            }
        }
        return true;
    }








    public boolean equals(final StrBuilder other) {
        if (this == other) {
            return true;
        }
        if (this.size != other.size) {
            return false;
        }
        final char thisBuf[] = this.buffer;
        final char otherBuf[] = other.buffer;
        for (int i = size - 1; i >= 0; i--) {
            if (thisBuf[i] != otherBuf[i]) {
                return false;
            }
        }
        return true;
    }








    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof StrBuilder) {
            return equals((StrBuilder) obj);
        }
        return false;
    }






    @Override
    public int hashCode() {
        final char buf[] = buffer;
        int hash = 0;
        for (int i = size - 1; i >= 0; i--) {
            hash = 31 * hash + buf[i];
        }
        return hash;
    }

    









    @Override
    public String toString() {
        return new String(buffer, 0, size);
    }







    public StringBuffer toStringBuffer() {
        return new StringBuffer(size).append(buffer, 0, size);
    }








    public StringBuilder toStringBuilder() {
        return new StringBuilder(size).append(buffer, 0, size);
    }







    @Override
    public String build() {
        return toString();
    }

    









    protected int validateRange(final int startIndex, int endIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        }
        if (endIndex > size) {
            endIndex = size;
        }
        if (startIndex > endIndex) {
            throw new StringIndexOutOfBoundsException("end < start");
        }
        return endIndex;
    }







    protected void validateIndex(final int index) {
        if (index < 0 || index > size) {
            throw new StringIndexOutOfBoundsException(index);
        }
    }

    



    class StrBuilderTokenizer extends StrTokenizer {




        StrBuilderTokenizer() {
            super();
        }

        
        @Override
        protected List<String> tokenize(final char[] chars, final int offset, final int count) {
            if (chars == null) {
                return super.tokenize(StrBuilder.this.buffer, 0, StrBuilder.this.size());
            } else {
                return super.tokenize(chars, offset, count);
            }
        }

        
        @Override
        public String getContent() {
            final String str = super.getContent();
            if (str == null) {
                return StrBuilder.this.toString();
            } else {
                return str;
            }
        }
    }

    



    class StrBuilderReader extends Reader {
        
        private int pos;
        
        private int mark;




        StrBuilderReader() {
            super();
        }

        
        @Override
        public void close() {
            
        }

        
        @Override
        public int read() {
            if (ready() == false) {
                return -1;
            }
            return StrBuilder.this.charAt(pos++);
        }

        
        @Override
        public int read(final char b[], final int off, int len) {
            if (off < 0 || len < 0 || off > b.length ||
                    (off + len) > b.length || (off + len) < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (pos >= StrBuilder.this.size()) {
                return -1;
            }
            if (pos + len > size()) {
                len = StrBuilder.this.size() - pos;
            }
            StrBuilder.this.getChars(pos, pos + len, b, off);
            pos += len;
            return len;
        }

        
        @Override
        public long skip(long n) {
            if (pos + n > StrBuilder.this.size()) {
                n = StrBuilder.this.size() - pos;
            }
            if (n < 0) {
                return 0;
            }
            pos += n;
            return n;
        }

        
        @Override
        public boolean ready() {
            return pos < StrBuilder.this.size();
        }

        
        @Override
        public boolean markSupported() {
            return true;
        }

        
        @Override
        public void mark(final int readAheadLimit) {
            mark = pos;
        }

        
        @Override
        public void reset() {
            pos = mark;
        }
    }

    



    class StrBuilderWriter extends Writer {




        StrBuilderWriter() {
            super();
        }

        
        @Override
        public void close() {
            
        }

        
        @Override
        public void flush() {
            
        }

        
        @Override
        public void write(final int c) {
            StrBuilder.this.append((char) c);
        }

        
        @Override
        public void write(final char[] cbuf) {
            StrBuilder.this.append(cbuf);
        }

        
        @Override
        public void write(final char[] cbuf, final int off, final int len) {
            StrBuilder.this.append(cbuf, off, len);
        }

        
        @Override
        public void write(final String str) {
            StrBuilder.this.append(str);
        }

        
        @Override
        public void write(final String str, final int off, final int len) {
            StrBuilder.this.append(str, off, len);
        }
    }

}

/*
 * Copyright / Copr. 2010-2013 Atos - Public Sector France -
 * BS & Innovation for the DataLift project,
 * Contributor(s) : L. Bihanic, H. Devos, O. Ventura, M. Chetima
 *
 * Contact: dlfr-datalift@atos.net
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package fr.sparna.commons.lang;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;


/**
 * An enhanced version of {@link java.util.Properties Properties}
 * that provides the following additional features.
 * <p>
 * Enhanced features include:</p>
 * <ul>
 *  <li><em>Variables substitution</em>: any occurrence of
 *   "<code>${<em>property-name</em>}</code>" in a property value is
 *   automatically replaced by the value of
 *   <code><em>property-name</em></code> upon lookup by
 *   {@link #getProperty(String)} or one of the get&lt;Type&gt;()
 *   convenience methods. Using {@link #get(Object)} allows
 *   accessing the raw property value.</li>
 *  <li><em>Overriding by system properties</em>: any property value
 *   is automatically overridden if the same property is also defined
 *   in the {@link System#getProperties() system properties} or as an
 *   environment variable. For accessing environment variables, the
 *   (invalid) character '.' (period) is replaced by '_'
 *   (underscore).</li>
 * </ul>
 * <p>
 * Variables substitution is performed dynamically during property
 * query. While this adds some overhead on each query, this allows
 * dynamically reflecting the changes that occurred on other
 * properties.</p>
 * <dl>
 *  <dt>${parameter}</dt>
 *  <dd>The value of parameter is substituted.</dd>
 *  <dt>${parameter:-expr}</dt>
 *  <dd><i>Use Default Value</i>.  If parameter is unset or null, the
 *   expansion of expr is substituted.  Otherwise, the value of
 *   parameter is substituted.</dd>
 *  <dt>${parameter:=expr}</dt>
 *  <dd><i>Assign Default Value</i>.  If parameter is unset or null,
 *   the expansion of expr is assigned to parameter.  The value of
 *   parameter is then substituted.</dd>
 *  <dt>${parameter:?expr}</dt>
 *  <dd><i>Throw Exception if Null or Unset</i>.  If parameter is null
 *   or unset, a {@link RuntimeException runtime exception} is thrown
 *   with the expansion of expr as detail message.  Otherwise, the
 *   value of parameter is substituted.</dd>
 *  <dt>${parameter:+expr}</dt>
 *  <dd><i>Use Alternate Value</i>.  If parameter is null or unset,
 *   nothing is substituted, otherwise the expansion of expr is
 *   substituted.</dd>
 *  <dt>${parameter:offset}<br/>${parameter:offset:length}</dt>
 *  <dd><i>Substring Expansion</i>.  Expands to up to length
 *   characters of parameter starting at the character specified by
 *   offset.  If length is omitted, expands to the substring of
 *   parameter starting at the character specified by offset.  length
 *   must evaluate to a number greater than or equal to zero.  If
 *   offset evaluates to a number less than zero, the value is used
 *   as an offset from the end of the value of parameter.  Substring
 *   indexing is zero-based.</dd>
 *  <dt>${#parameter}</dt>
 *  <dd><i>String Length</i>. The length in characters of the value
 *   of parameter is substituted.</dd>
 * </dl>
 *
 * @author lbihanic
 */
public class VersatileProperties extends Properties
{
    //------------------------------------------------------------------------
    // Constants
    //------------------------------------------------------------------------

    private final static char PATTERN_MARKER = ':';
    private final static char ESCAPE_MARKER = '\\';
    private final static char LENGTH_MARKER = '#';

    //------------------------------------------------------------------------
    // Instance members
    //------------------------------------------------------------------------

    /** Flyweight context for parameter expansion. */
    private final ExpansionContext context = new ExpansionContext();

    private boolean systemPropertiesOverride = true;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Creates an empty property list with no default values.
     */
    public VersatileProperties() {
        super();
    }

    /**
     * Creates an empty property list with the specified defaults.
     * @param  defaults   the defaults, i.e. the property list where
     *                    values are looked for when a property is
     *                    not defined in this property list.
     */
    public VersatileProperties(Properties defaults) {
        super(defaults);
    }

    //------------------------------------------------------------------------
    // Properties contract support
    //------------------------------------------------------------------------

    /**
     * Returns the value to which the specified key is mapped in
     * this property list.
     * @param  key   the property key.
     *
     * @return the value to which the key is mapped in this property
     *         list or <code>null</code> if the key is not mapped to
     *         any value.
     * @throws NullPointerException  if the key is <code>null</code>.
     */
    public Object get(Object key) {
        Object value = null;

        if ((key instanceof String) && (this.systemPropertiesOverride)) {
            value = this.getSystemProperty((String)key);
        }

        if (value == null) {
            value = super.get(key);
        }
        if ((value == null) && (key instanceof String)
                            && (! this.systemPropertiesOverride)) {
            value = this.getSystemProperty((String)key);
        }
        return value;
    }

    /**
     * Searches for the property with the specified key in this
     * property list.  If the key is not found in this property list,
     * the default property list, and its defaults, recursively, are
     * then checked.  The method returns <code>null</code> if the
     * property is not found.
     * @param  key   key whose associated value is to be returned.
     *
     * @return the value associated with <code>key</code> in this
     *         property list or <code>def</code> if the associated
     *         value does not exist.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     */
    public final String getProperty(String key) {
        Object oVal = this.get(key);
        String sVal = (oVal instanceof String)? (String)oVal: null;
        return this.resolveVariables(
                        ((sVal == null) && (this.defaults != null))?
                                        this.defaults.getProperty(key): sVal);
    }

    /**
     * Searches for the property with the specified key in this
     * property list.  If the key is not found in this property list,
     * the default property list, and its defaults, recursively, are
     * then checked.  The method returns the default value argument
     * if the property is not found.
     * @param  key   key whose associated value is to be returned.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code>.
     *
     * @return the value associated with <code>key</code> in this
     *         property list or <code>def</code> if the associated
     *         value does not exist.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     */
    public final String getProperty(String key, String def) {
        String val = this.getProperty(key);
        return (val == null)? this.resolveVariables(def): val;
    }

    /**
     * Sets the property with the specified key in this property
     * list.  If <code>value<code> is <code>null</code>, the property
     * is removed from this property list.
     * @param  key     the key to be placed into this property list.
     * @param  value   the value corresponding to <code>key</code>.
     *
     * @return the previous value of the specified key in this
     *         property list, or <code>null</code> if it did not have
     *         one.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     */
    public Object setProperty(String key, String value) {
        Object oldValue = null;

        if (value == null) {
            oldValue = this.remove(key);
        }
        else {
            oldValue = super.setProperty(key, value);
        }
        return oldValue;
    }

    /**
     * Defines whether {@link System#getProperties() system properties}
     * override the locally defined ones. Defaults to <code>true</code>.
     * @param  override   <code>true</code> for system properties to
     *                    override locally defined ones,
     *                    <code>false</code> otherwise.
     */
    public void setSystemPropertiesOverride(boolean override) {
        this.systemPropertiesOverride = override;
    }

    //------------------------------------------------------------------------
    // Convenience methods
    //------------------------------------------------------------------------

    /**
     * Returns the {@link java.math.BigDecimal BigDecimal} value
     * represented by the string associated with the specified key
     * in this property list.  Returns the specified default if there
     * is no value associated with the key or value can not be
     * converted into a BigDecimal.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #putBigDecimal}.</p>
     * @param  key   key whose associated value is to be returned as
     *               a BigDecimal.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code> or the associated value cannot
     *               be interpreted as a BigDecimal.
     *
     * @return the BigDecimal value represented by the string
     *         associated with <code>key</code> in this property list
     *         or <code>def</code> if the associated value does not
     *         exist or cannot be interpreted as a BigDecimal.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putBigDecimal(String,java.math.BigDecimal)
     * @see #getProperty(String,String)
     */
    public final BigDecimal getBigDecimal(String key, BigDecimal def) {
        BigDecimal val = def;
        String sVal = this.getProperty(key);
        if (sVal != null) {
            try {
                val = new BigDecimal(sVal);
            }
            catch (ArithmeticException e)      { /* Ignore... */ }
            catch (IllegalArgumentException e) { /* Ignore... */ }
        }
        return val;
    }

    /**
     * Returns the {@link java.math.BigInteger BigInteger} value
     * represented by the string associated with the specified key
     * in this property list.  Returns the specified default if there
     * is no value associated with the key or value can not be
     * converted into a BigInteger.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #putBigInteger}.</p>
     * @param  key   key whose associated value is to be returned as
     *               a BigInteger.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code> or the associated value cannot
     *               be interpreted as a BigInteger.
     *
     * @return the BigInteger value represented by the string
     *         associated with <code>key</code> in this property list
     *         or <code>def</code> if the associated value does not
     *         exist or cannot be interpreted as a BigInteger.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putBigInteger(String,java.math.BigInteger)
     * @see #getProperty(String,String)
     */
    public final BigInteger getBigInteger(String key, BigInteger def) {
        BigInteger val = def;
        String sVal = this.getProperty(key);
        if (sVal != null) {
            try {
                val = new BigInteger(sVal);
            }
            catch (ArithmeticException e)      { /* Ignore... */ }
            catch (IllegalArgumentException e) { /* Ignore... */ }
        }
        return val;
    }

    /**
     * Returns the boolean value represented by the string associated
     * with the specified key in this property list.  Valid strings
     * are "<code>true</code>", "<code>yes</code>" and "<code>1</code>"
     * which represents true, and "<code>false</code>",
     * "<code>no</code>"and "<code>0</code>" which represents false.
     * Case is ignored, so, for example, "<code>TRUE</code>" and
     * "<code>False</code>" are also valid.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #putBoolean}.</p>
     * @param  key   key whose associated value is to be returned as
     *               a boolean.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code> or the associated value cannot
     *               be interpreted as a boolean.
     *
     * @return the boolean value represented by the string
     *         associated with <code>key</code> in this property list
     *         or <code>def</code> if the associated value does not
     *         exist or cannot be interpreted as a boolean.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putBoolean(String,boolean)
     * @see #getProperty(String,String)
     */
    public final boolean getBoolean(String key, boolean def) {
        return this.parseBoolean(this.getProperty(key), def);
    }

    /**
     * Returns the byte array value represented by the string
     * associated with the specified key in this property list.  Valid
     * strings are <em>Base64</em> encoded binary data, as defined in
     * <a href=http://www.ietf.org/rfc/rfc2045.txt>RFC 2045</a>,
     * Section 6.8.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #putByteArray}.</p>
     * @param  key   key whose associated value is to be returned as
     *               a byte array.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code> or the associated value is not
     *               a valid Base64 encoded byte array.
     *
     * @return the byte array value represented by the string
     *         associated with <code>key</code> in this property list
     *         or <code>def</code> if the associated value does not
     *         exist or cannot be interpreted as a byte array.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putByteArray(String,byte[])
     * @see #getProperty(String,String)
     */
    public byte[] getByteArray(String key, byte[] def) {
        byte[] val = null;
        String sVal = this.getProperty(key);
        if (sVal != null) {
            try {
                val = Base64.decode(this.getProperty(key), false);
            }
            catch (IOException e) { /* Ignore... */ }
        }
        return ((val != null) && (val.length != 0))? val: def;
    }

    /**
     * Returns the double value represented by the string associated
     * with the specified key in this property list.  Returns the
     * specified default if there is no value associated with the key
     * or value can not be converted into a double.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #putDouble}.</p>
     * @param  key   key whose associated value is to be returned as
     *               a double.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code> or the associated value cannot
     *               be interpreted as a double.
     *
     * @return the double value represented by the string
     *         associated with <code>key</code> in this property list
     *         or <code>def</code> if the associated value does not
     *         exist or cannot be interpreted as a double.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putDouble(String,double)
     * @see #getProperty(String,String)
     */
    public final double getDouble(String key, double def) {
        double val = def;
        String sVal = this.getProperty(key);
        if (sVal != null) {
            try {
                val = Double.parseDouble(sVal);
            }
            catch (IllegalArgumentException e) { /* Ignore... */ }
        }
        return val;
     }

    /**
     * Returns the int value represented by the string associated
     * with the specified key in this property list.  Returns the
     * specified default if there is no value associated with the key
     * or value can not be converted into an int.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #putInt}.</p>
     * @param  key   key whose associated value is to be returned as
     *               an int.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code> or the associated value cannot
     *               be interpreted as an int.
     *
     * @return the integer value represented by the string
     *         associated with <code>key</code> in this property list
     *         or <code>def</code> if the associated value does not
     *         exist or cannot be interpreted as an integer.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putInt(String,int)
     * @see #getProperty(String,String)
     */
    public final int getInt(String key, int def) {
        int val = def;
        String sVal = this.getProperty(key);
        if (sVal != null) {
            try {
                val = Integer.parseInt(sVal);
            }
            catch (IllegalArgumentException e) { /* Ignore... */ }
        }
        return val;
    }

    /**
     * Returns the long value represented by the string associated
     * with the specified key in this property list.  Returns the
     * specified default if there is no value associated with the key
     * or value can not be converted into a long.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #putLong}.</p>
     * @param  key   key whose associated value is to be returned as
     *               a long.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code> or the associated value cannot
     *               be interpreted as a long.
     *
     * @return the long integer value represented by the string
     *         associated with <code>key</code> in this property list
     *         or <code>def</code> if the associated value does not
     *         exist or cannot be interpreted as a long integer.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putLong(String,long)
     * @see #getProperty(String,String)
     */
    public final long getLong(String key, long def) {
        long val = def;
        String sVal = this.getProperty(key);
        if (sVal != null) {
            try {
                val = Long.parseLong(sVal);
            }
            catch (IllegalArgumentException e) { /* Ignore... */ }
        }
        return val;
    }

    /**
     * Returns the string value associated with the specified key in
     * this property list.
     * <p>
     * Provided for parallelism with the <code>getBoolean</code>,
     * <code>getInt</code>... methods.</p>
     * @param  key   key whose associated value is to be returned.
     * @param  def   the value to be returned in the event that this
     *               property list has no value associated with
     *               <code>key</code>.
     *
     * @return the value associated with <code>key</code> in this
     *         property list or <code>def</code> if the associated
     *         value does not exist.
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #putString(String,String)
     * @see #getProperty(String,String)
     */
    public final String getString(String key, String def) {
        return this.getProperty(key, def);
    }

    /**
     * Associates a string representing the specified
     * {@link java.math.BigDecimal BigDecimal} value
     * with the specified key in this property list.  The associated
     * string is the one that would be returned if the BigDecimal
     * value were passed to {@link java.math.BigDecimal#toString()}.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #getBigDecimal}.</p>
     * @param  key     key with which the string form of value is to
     *                 be associated.
     * @param  value   value whose string form is to be associated
     *                 with key.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getBigDecimal(String,java.math.BigDecimal)
     */
    public final void putBigDecimal(String key, BigDecimal value) {
        this.setProperty(key, (value == null)? null: value.toString());
    }

    /**
     * Associates a string representing the specified
     * {@link java.math.BigInteger BigInteger} value
     * with the specified key in this property list.  The associated
     * string is the one that would be returned if the BigInteger
     * value were passed to {@link java.math.BigInteger#toString()}.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #getBigInteger}.</p>
     *
     * @param  key     key with which the string form of value is to
     *                 be associated.
     * @param  value   value whose string form is to be associated
     *                 with key.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getBigInteger(String,java.math.BigInteger)
     */
    public final void putBigInteger(String key, BigInteger value) {
        this.setProperty(key, (value == null)? null: value.toString());
    }

    /**
     * Associates a string representing the specified boolean value
     * with the specified key in this property list.  The associated
     * string is the one that would be returned if the boolean
     * value were passed to {@link java.lang.Boolean#toString(boolean)}.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #getBoolean}.</p>
     * @param  key     key with which the string form of value is to
     *                 be associated.
     * @param  value   value whose string form is to be associated
     *                 with key.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getBoolean(String,boolean)
     */
    public final void putBoolean(String key, boolean value) {
        this.setProperty(key, Boolean.toString(value));
    }

    /**
     * Associates a string representing the specified byte array
     * with the specified key in this property list.  The associated
     * string is the <em>Base64</em> encoding of the byte array, as
     * defined in <a href=http://www.ietf.org/rfc/rfc2045.txt>RFC 2045</a>,
     * Section 6.8, with one minor change: the string will consist
     * solely of characters from the <em>Base64 alphabet</em>; it will
     * not contain any newline characters.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #getByteArray}.</p>
     * @param  key     key with which the string form of value is to
     *                 be associated.
     * @param  value   value whose string form is to be associated
     *                 with key.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getByteArray(String,byte[])
     */
    public final void putByteArray(String key, byte[] value) {
        this.setProperty(key,
                        (value == null)? null: Base64.encode(value, null));
    }

    /**
     * Associates a string representing the specified double value
     * with the specified key in this property list.  The associated
     * string is the one that would be returned if the double
     * value were passed to {@link Double#toString(double)}.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #getDouble}.</p>
     * @param  key     key with which the string form of value is to
     *                 be associated.
     * @param  value   value whose string form is to be associated
     *                 with key.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getDouble(String,double)
     */
    public final void putDouble(String key, double value) {
        this.setProperty(key, Double.toString(value));
    }

    /**
     * Associates a string representing the specified integer value
     * with the specified key in this property list.  The associated
     * string is the one that would be returned if the integer
     * value were passed to {@link Integer#toString(int)}.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #getInt}.</p>
     * @param  key     key with which the string form of value is to
     *                 be associated.
     * @param  value   value whose string form is to be associated
     *                 with key.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getInt(String,int)

     */
    public final void putInt(String key, int value) {
        this.setProperty(key, Integer.toString(value));
    }

    /**
     * Associates a string representing the specified long value
     * with the specified key in this property list.  The associated
     * string is the one that would be returned if the long
     * value were passed to {@link Long#toString(long)}.
     * <p>
     * This method is intended for use in conjunction with
     * {@link #getLong}.</p>
     * @param  key     key with which the string form of value is to
     *                 be associated.
     * @param  value   value whose string form is to be associated
     *                 with key.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getLong(String,long)
     */
    public final void putLong(String key, long value) {
        this.setProperty(key, Long.toString(value));
    }

    /**
     * Associates the specified string value with the specified key
     * in this property list.  If <code>value<code> is
     * <code>null</code>, the property is removed from the property
     * list.
     * <p>
     * Provided for parallelism with the <code>putBoolean</code>,
     * <code>putInt</code>... methods.</p>
     * @param  key     the key to be placed into this property list.
     * @param  value   the value corresponding to <code>key</code>.
     *
     * @throws NullPointerException if <code>key</code> is
     *                              <code>null</code>.
     *
     * @see #getString(String,String)
     * @see #setProperty(String,String)
     */
    public final void putString(String key, String value) {
        this.setProperty(key, value);
    }

    //------------------------------------------------------------------------
    // Specific implementation
    //------------------------------------------------------------------------

    /**
     * Returns the value to which the specified key is mapped in
     * the Java VM system properties. If the key is not present in the
     * system properties, the methods checks the environment for a
     * variable named <code>key</code>.
     * @param  key   the property key.
     *
     * @return the value to which the key is mapped in the system
     *         properties or environment or <code>null</code> if the
     *         key is not mapped.
     * @throws NullPointerException  if the key is <code>null</code>.
     */
    private Object getSystemProperty(String key) {
        Object value = null;
        try {
            value = System.getProperty(key);
            if (value == null) {
                value = System.getenv(key.replace('.', '_'));
            }
        }
        catch (SecurityException e) { /* Ignore... */ }

        return value;
    }

    /**
     * Resolves the variables (i.e. substrings following the
     * "<code>${<em>property-name</em>}</code>" convention) in
     * <code>pattern</code>, replacing then by the value of the
     * corresponding property.  If a property in undefined, the
     * variable is not substituted and the substring is returned
     * unchanged.
     * @param  pattern   the pattern to be evaluated.
     *
     * @return the result of <code>pattern</code> evaluation with
     *         the known properties replaced by their value.
     */
    public final String resolveVariables(String pattern) {
        String result = pattern;        // Default to plain old value!

        if ((pattern != null) && (pattern.length() != 0)
                              && (pattern.indexOf("${") != -1)) {
            StringBuffer[] segments = new StringBuffer[2];
            segments[0] = new StringBuffer(Math.max(pattern.length() * 2, 32));
            segments[1] = new StringBuffer(32);

            int part = 0;
            boolean inQuote = false;
            boolean inParam = false;
            int braceStack = 0;

            for (int i=0, max=pattern.length(); i<max; i++) {
                char ch = pattern.charAt(i);
                if (inQuote) {
                    // just copy quotes in parts
                    segments[part].append(ch);
                    if (ch == '\'') { inQuote = false; }
                }
                else {
                    switch (ch) {
                        case '$':
                            if ((!inParam) && (i+1 < pattern.length()) &&
                                              (pattern.charAt(i+1) == '{')) {
                                inParam = true;
                                part = 1;
                                i++;            // Skip "${".
                                braceStack++;
                            }
                            else {
                                segments[part].append(ch);
                            }
                            break;

                        case '{':
                            braceStack++;
                            segments[part].append(ch);
                            break;

                        case '}':
                            braceStack--;
                            if ((braceStack == 0) && (inParam)) {
                                String variable = segments[1].toString();
                                segments[1].setLength(0);
                                part = 0;
                                inParam = false;

                                String value = this.resolve(variable);
                                if (value == null) {
                                    segments[part].append("${")
                                                  .append(variable).append('}');
                                }
                                else {
                                    segments[part].append(value);
                                }
                            }
                            else {
                                segments[part].append(ch);
                            }
                            break;

                        case '\'':
                            if ((i+1 < pattern.length()) &&
                                (pattern.charAt(i+1) == '\'')) {
                                i++;        // Handle doubles: insert one quote.
                            }
                            else {
                                inQuote = true;
                            }
                            // Fall through.

                        default:
                            segments[part].append(ch);
                            break;
                    }
                }
            }
            result = segments[0].toString();
        }
        return result;
    }

    private boolean parseBoolean(String s, boolean def) {
        boolean value = def;

        if ((s != null) && (s.length() != 0)) {
            s = s.toLowerCase();
            if ((s.equals(Boolean.TRUE.toString())) || (s.equals("yes"))
                                                    || (s.equals("1"))) {
                value = true;
            }
            else if ((s.equals(Boolean.FALSE.toString())) || (s.equals("no"))
                                                          || (s.equals("0"))) {
                value = false;
            }
            // Else: use default value.
        }
        return value;
    }

    private String resolve(String variable) {
        String value = null;

        if (variable != null) {
            int lengthMarkerPos = variable.indexOf(LENGTH_MARKER);
            if (((lengthMarkerPos != -1) && (lengthMarkerPos < 2)) ||
                (variable.indexOf(PATTERN_MARKER) > 0)) {
                value = ExpansionOperation.parse(variable,
                                                 this.context).resolve();
            }
            else {
                value = this.getProperty(variable);
            }
        }
        // System.out.println("\"" + variable + "\" -> \"" +  value + "\"");
        return value;
    }

    private final class ExpansionContext {
        public String parameter = null;
        public String alternate = null;
        public ExpansionOperation operation = null;
        public int offset = -1;
        public int length = -1;

        public ExpansionContext() {
            super();
            this.reset();
        }

        public void reset() {
            this.parameter = null;
            this.alternate = null;
            this.operation = null;
            this.offset = -1;
            this.length = -1;
        }

        public String resolve() {
            String result = null;

            if (this.operation != null) {
                result = this.operation.execute(this);
            }
            return result;
        }

        public String getParameterValue() {
            return getProperty(this.parameter);
        }

        public void setParameterValue(String value) {
            setProperty(this.parameter, value);
        }

        public String getAlternateValue() {
            return this.resolve(this.alternate);
        }

        public String resolve(String expr) {
            return resolveVariables(expr);
        }
    }

    private enum ExpansionOperation
    {
        SUBSTITUTE_VALUE     ('v'),
        USE_DEFAULT_VALUE    ('-'),
        ASSIGN_DEFAULT_VALUE ('='),
        ERROR_IF_UNSET       ('?'),
        USE_ALTERNATE_VALUE  ('+'),
        SUBSTITUTE_LENGTH    (LENGTH_MARKER),
        VALUE_SUBSTRING      ('s');

        private final int opCode;

        private ExpansionOperation(char opCode) {
            this.opCode = opCode;
        }

        public String execute(ExpansionContext context) {
            String result = context.getParameterValue();

            switch (this.opCode) {
                case '-':               // Use default value.
                    if (result == null) {
                        result = context.getAlternateValue();
                    }
                    break;

                case '=':               // Assign default value.
                    if (result == null) {
                        result = context.getAlternateValue();
                        context.setParameterValue(result);
                    }
                    break;

                case '?':               // Error if null or unset.
                    if (result == null) {
                        throw new RuntimeException(context.getAlternateValue());
                    }
                    break;

                case '+':               // Use alternate value.
                    if (result != null) {
                        result = context.getAlternateValue();
                    }
                    break;

                case LENGTH_MARKER:     // Parameter value length.
                    result = String.valueOf((int)
                                        ((result != null)? result.length(): 0));
                    break;

                case 's':               // Substring expansion.
                    if (result != null) {
                        try {
                            int offset = context.offset;
                            if (offset < 0) {
                                offset = result.length() + offset;
                                if (offset < 0) {
                                    offset = 0;
                                }
                            }
                            result = result.substring(offset,
                                (context.length != -1)? context.length:
                                                        result.length());
                        }
                        catch (Exception e) { /* Ignore... */ }
                    }
                    break;

                case 'v':               // Parameter value is substituted.
                    // Result already set.
                    break;

                default:
                    // Unknown operation code.
                    throw new UnsupportedOperationException("" + this.opCode);
                    // break;
            }
            return result;
        }

        public static ExpansionContext parse(String spec,
                                             ExpansionContext context) {
            // Initialize context to default op.: returning parameter value.
            context.reset();
            context.parameter = spec;
            context.operation = SUBSTITUTE_VALUE;

            if ((spec != null) && (spec.length() != 0)) {
                if (spec.charAt(0) == LENGTH_MARKER) {
                    context.operation = SUBSTITUTE_LENGTH;
                    context.parameter = spec.substring(1);
                }
                else {
                    // Locate first unescaped pattern marker.
                    int sepIndex = spec.indexOf(PATTERN_MARKER);
                    while ((sepIndex > 0) &&
                           (spec.charAt(sepIndex - 1) == ESCAPE_MARKER)) {
                        sepIndex = spec.indexOf(PATTERN_MARKER, sepIndex + 1);
                    }
                    if (sepIndex > 0) {
                        // Pattern or escape sequence marker found.
                        // => Parse pattern and remove escape sequences.
                        context = parsePattern(spec, sepIndex, context);
                    }
                    // Else: leave context as is to simply return param. value.
                }
            }
            return context;
        }

        public static ExpansionContext parsePattern(String spec, int offset,
                                                    ExpansionContext context) {
            String parameter = spec;

            if ((offset > 0) && (offset < spec.length() - 1) &&
                                (spec.charAt(offset - 1) != ESCAPE_MARKER)) {
                String alternate = null;
                ExpansionOperation operation = null;

                try {
                    parameter = spec.substring(0, offset);
                    offset++;
                    operation = getOperation(spec.charAt(offset));
                    if (operation != null) {
                        alternate = spec.substring(offset + 1);
                    }
                    else {
                        int strOffset = -1;
                        int strLength = -1;

                        // Not a known operation. => Assume substring.
                        int lengthSep = spec.indexOf(PATTERN_MARKER, offset);
                        if (lengthSep != -1) {
                            strOffset = evaluateInteger(
                                    spec.substring(offset, lengthSep).trim(),
                                    context);
                            strLength = evaluateInteger(
                                    spec.substring(lengthSep + 1).trim(),
                                    context);
                        }
                        else {
                            strOffset = evaluateInteger(
                                    spec.substring(offset).trim(), context);
                        }
                        operation = VALUE_SUBSTRING;

                        context.offset = strOffset;
                        context.length = strLength;
                    }
                }
                catch (Exception e) { /* Ignore... */ }

                context.alternate = alternate;
                context.operation = operation;
            }
            context.parameter = removeEscapes(parameter);

            return context;
        }

        private static ExpansionOperation getOperation(char code) {
            ExpansionOperation op = null;
            for (ExpansionOperation o : ExpansionOperation.values()) {
                if (o.opCode == code) {
                    op = o;
                    break;
                }
            }
            return op;
        }

        private static int evaluateInteger(String expr,
                                           ExpansionContext context) {
            int value = -1;

            if ((expr != null) && (expr.length() != 0)) {
                expr = expr.trim();
                try {
                    value = Integer.parseInt(expr);
                }
                catch (NumberFormatException e) {
                    expr = context.resolve(expr);
                    if ((expr != null) && (expr.length() != 0)) {
                        value = Integer.parseInt(expr.trim());
                    }
                    else {
                        throw new NumberFormatException(expr);
                    }
                }
            }
            else {
                throw new NumberFormatException(expr);
            }
            return value;
        }

        private static String removeEscapes(String spec) {
            if (spec == null) {
                return null;
            }

            int escIndex = spec.indexOf(ESCAPE_MARKER);
            if (escIndex != -1) {
                // Escape sequence present. => Analyse and copy.
                StringBuffer result = new StringBuffer(spec.length());
                int pos = 0;
                do {
                    // Copy unescaped characters.
                    while (pos < escIndex) {
                        result.append(spec.charAt(pos++));
                    }
                    // Check escaped character.
                    escIndex++;
                    char escChar = spec.charAt(escIndex);
                    if ((escChar == LENGTH_MARKER)  ||          // \#
                        (escChar == PATTERN_MARKER) ||          // \:
                        (escChar == ESCAPE_MARKER)) {           // \\
                        // Handled escape sequence. => Skip marker ('\').
                        pos++;
                    }
                    // Else copy unhandled escape sequence ('\n', '\t', etc.).

                    // Locate next escape sequence.
                    escIndex = spec.indexOf(ESCAPE_MARKER, escIndex + 1);
                    if (escIndex == -1) {
                        escIndex = spec.length() - 1;
                    }
                }
                while (escIndex != -1);

                spec = result.toString();
            }
            return spec;
        }
    }
}

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
import java.nio.charset.UnmappableCharacterException;
import java.util.Arrays;


/**
 * A fast Java class to encode and decode to and from Base64 in
 * full accordance with
 * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>.
 * <p>
 * The encoder produces the same output as the Sun one except that
 * the Sun's encoder appends a trailing line separator if the last
 * character isn't a pad. Unclear why, but it only adds to the
 * length and is probably a side effect. Both are in conformance
 * with RFC 2045 though.</p>
 * <p>
 * Code used for validation:</p>
 * <blockquote><pre>
 *    Random rand = new Random();
 *    // All separators, including the system one.
 *    String sysSep = System.getProperty("line.separator");
 *    String[] seps = {null, "", "\r\n", "\n", "\r", sysSep};
 *    sun.misc.BASE64Encoder sunEnc = new sun.misc.BASE64Encoder();
 *
 *    for (int i = 0; i &lt; 100000; i++) {
 *        byte[] ba1 = new byte[(int) rand.nextInt(100000)];
 *        rand.nextBytes(ba1);
 *
 *        // Pick a random separator
 *        String sep = seps[rand.nextInt(seps.length)];
 *
 *        String s = Base64.encode(ba1, sep);
 *        byte[] ba2 = Base64.decode(s);
 *
 *        if (Arrays.equals(ba1, ba2) == false) {
 *            System.out.println("Integrity Failure!!");
 *            System.exit(0);
 *        }
 *
 *        String sunS = sunEnc.encode(ba1);
 *
 *        if (sep != null &amp;&amp; sep.equals(sysSep)) {
 *            // trim since we aren't handling last separator exactly
 *            // the same and number of white spaces doesn't matter anyway.
 *            if (sunS.trim().equals(s.trim()) == false) {
 *                System.out.println("Sun conformance Failure!!\n\"" +
                                     s + "\"\n\n    != \n\n\"" + sunS + "\"");
 *                System.exit(0);
 *            }
 *        }
 *    }
 *    System.out.println("Success!!");
 *    System.exit(0);
 * </pre></blockquote>
 * <p>
 * License:<br>
 * Free to use for any legal purpose, though sending an email to
 * <code>base64 @ miginfocom . com</code> to tell me you're using it
 * will ut a smile on my face! :)</p>
 * <p>
 * Reprint/republish for public consumption is allowed as long as
 * this license is included.</p>
 *
 * @author Mikael Grev (2004-08-02)
 */
public final class Base64
{
    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------

    private final static char[] CA =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".
                                                                toCharArray();
    private final static int[] IA = new int[256];

    //-------------------------------------------------------------------------
    // Class initialization
    //-------------------------------------------------------------------------

    static {
        Arrays.fill(IA, -1);
        for (int i = 0, iS = CA.length; i < iS; i++) {
            IA[CA[i]] = i;
        }
    }

    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Default constructor, private on purpose (utility class without
     * any instances).
     */
    private Base64() {
        throw new UnsupportedOperationException();
    }

    //-------------------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------------------

    /**
     * Encodes a raw byte array into a Base64 string representation
     * in accordance with RFC 2045.
     * <p>
     * No line separator will be in breach of RFC 2045 which specifies
     * max 76 per line but will be a little faster.</p>
     *
     * @param  bArr      The bytes to convert. If <code>null</code>
     *                   or length 0, <code>""</code> will be returned.
     * @param  lineSep   Optional line separator after 76 characters,
     *                   unless end of file.  Max 2 chars length. If
     *                   <code>null</code> or length 0 no line breaks
     *                   will be inserted.
     *
     * @return A Base64 encoded string. Never <code>null</code>.
     */
    public final static String encode(byte[] bArr, String lineSep) {
        // Check special case
        if ((bArr == null) || (bArr.length == 0)) {
            return "";
        }

        int last = bArr.length - 1;             // Last byte
        int cCnt = (last / 3 + 1) << 2;         // Character count
        int sepLen = lineSep != null ? lineSep.length() : 0;
        int sepCnt = ((cCnt - 1) / 76) * sepLen;// line separator count
        int encArrLen = cCnt + sepCnt;          // Length of returned string

        char[] encArr = new char[encArrLen];

        for (int rOff = 0, cOff = 0, sepAdd = 0; rOff <= last; rOff += 3) {
            int left = last - rOff;
            int bEnd = (left > 1 ? 2 : left);

            // Collect 1 to 3 bytes to encode
            int block = 0;
            for (int i = 0, r = 16; i <= bEnd; i++, r -= 8) {
                int n = bArr[rOff + i];
                block += (n < 0 ? n + 256 : n) << r;
            }

            // Encode into 2-4 chars appending '=' if not enough data left.
            // Note: >>> is faster than >> !!
            encArr[cOff++] = CA[(block >>> 18) & 0x3f];
            encArr[cOff++] = CA[(block >>> 12) & 0x3f];
            encArr[cOff++] = left > 0 ? CA[(block >>> 6) & 0x3f] : '=';
            encArr[cOff++] = left > 1 ? CA[block & 0x3f] : '=';

            // Possibly insert line break after character 76.
            if (sepCnt > 0 && encArrLen > cOff) {
                // If we have a separator and not end if buf
                if ((cOff - sepAdd) % 76 == 0) {
                    // If after char 76
                    encArr[cOff++] = lineSep.charAt(0);
                    if (sepLen > 1) {
                        encArr[cOff++] = lineSep.charAt(1);
                    }
                    sepAdd += sepLen;
                }
            }
        }
        return new String(encArr);
    }

    /**
     * Decodes a Base64 encoded string. All illegal characters will
     * be ignored and can handle strings both with and without line
     * separators.
     *
     * @param  s   the string to decode. <code>null</code> or empty
     *             string ("") will return an empty array.
     *
     * @return the decoded array of bytes. Never <code>null</code>
     *         but may be of length 0.
     */
    public final static byte[] decode(String s) {
        byte[] result = null;

        try {
            result = decode(s, true);
        }
        catch (IOException e) {
            result = new byte[0];
        }
        return result;
    }

    /**
     * Decodes a Base64 encoded string.  This method can handle
     * strings both with and without line separators.
     *
     * @param  s         the string to decode. <code>null</code> or
     *                   empty string ("") will return an empty array.
     * @param  lenient   whether to silently ignore illegal characters.
     *
     * @return the decoded array of bytes. Never <code>null</code>
     *         but may be of length 0.
     *
     * @throws IOException if <code>lenient</code> was set to
     *         <code>false</code> and an illegal character was found.
     */
    public final static byte[] decode(String s, boolean lenient)
                                                          throws IOException {
        // Check special case
        if ((s == null) || (s.length() == 0)) {
            return new byte[0];
        }

        // Note. Making an array of s is faster sometimes but slower sometimes.
        // Timing values are more uneven due to garbage creation.

        // Count '=' at end and disregard them
        int pad = 0;
        for (int i = s.length() - 1; s.charAt(i) == '='; i--) {
            pad++;
        }

        // Count illegal characters to know what size the returned byte[]
        // will have so we don't have to reallocate it later.
        int sepCnt = 0;         // Number of separator characters. (Actually
                                // illegal characters, but that's a bonus...)
        for (int i=0, iS=s.length() - pad; i<iS; i++) {
            if (IA[s.charAt(i)] < 0) {
                if (lenient == false) {
                    throw new UnmappableCharacterException(i);
                }
                sepCnt++;
            }
        }

        int len = (((s.length() - sepCnt) * 6) >> 3) - pad;
        byte[] b = new byte[len];       // Preallocate byte[] of exact length

        for (int i=0, iS=s.length() - pad, bIx=0; i<iS; bIx += 3) {
            // Assemble three bytes into an int from four "valid" characters.
            int bits = 0;
            for (int j=0; (j < 4) && (i < iS); ) {
                // j only increased if a valid char was found.
                int c = IA[s.charAt(i++)];
                if (c >= 0) {
                    bits += c << (18 - 6 * j++);
                }
            }

            // Add the bytes
            for (int j=0, r=16; (j < 3) && (bIx + j < len); j++, r -= 8) {
                b[bIx + j] = (byte) ((bits >> r) & 0xff);
            }
        }
        return b;
    }
}

/**
 *       Java Image Science Toolkit
 *                  --- 
 *     Multi-Object Image Segmentation
 *
 * Center for Computer-Integrated Surgical Systems and Technology &
 * Johns Hopkins Applied Physics Laboratory &
 * The Johns Hopkins University
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.  The license is available for reading at:
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * @author Blake Lucas (blake@cs.jhu.edu)
 */
package org.imagesci.gac;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class TopologyRule3DLookUpTable.
 */
public class TopologyPreservationRule3D extends TopologyRule3D {

	/** The lut6_18. */
	protected static BitSet lut6_18 = null;

	/** The lut6_26. */
	protected static BitSet lut6_26 = null;

	/** The cube foreground. */
	protected boolean[][] cubeForeground = new boolean[3][3];

	/**
	 * Instantiates a new topology rule2 d look up table.
	 * 
	 * @param rule
	 *            the rule
	 */
	public TopologyPreservationRule3D(Rule rule) {
		super(rule);
		if (rule != Rule.CONNECT_6_26) {
			System.err
					.println("Only 6/26 connectivity rules are supported by the sparse field method implementation. This will hopefully be fixed in future releases.");
			System.err.flush();
			System.exit(-1);
		}
		if (rule == Rule.CONNECT_6_18 || rule == Rule.CONNECT_18_6) {
			loadLUT618();
		} else if (rule == Rule.CONNECT_6_26 || rule == Rule.CONNECT_26_6) {
			loadLUT626();
		}
	}

	/**
	 * Load lu t618.
	 * 
	 * @return true, if successful
	 */
	private boolean loadLUT618() {
		if (lut6_18 != null) {
			return true;
		}
		lut6_18 = loadLUT(TopologyPreservationRule3D.class
				.getResourceAsStream("connectivity6_18.zip"));
		return true;
	}

	/**
	 * Load lu t626.
	 * 
	 * @return true, if successful
	 */
	private boolean loadLUT626() {
		if (lut6_26 != null) {
			return true;
		}
		lut6_26 = loadLUT(TopologyPreservationRule3D.class
				.getResourceAsStream("connectivity6_26.zip"));
		return true;
	}

	/**
	 * Load lut.
	 *
	 * @param fis the fis
	 * @return the bit set
	 */
	private BitSet loadLUT(InputStream fis) {
		final int BUFFER = 4096;
		try {
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			if ((entry = zis.getNextEntry()) != null) {
				int index = 0;
				int count = 0;
				byte[] buff = new byte[(2 << 24)];
				while ((count = zis.read(buff, index,
						Math.min(BUFFER, buff.length - index))) > 0) {
					index += count;
				}
				System.out.println("INDEX " + buff.length + " " + index);
				return fromByteArray(buff);
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * From byte array.
	 * 
	 * @param bytes
	 *            the bytes
	 * 
	 * @return the bit set
	 */
	public static BitSet fromByteArray(byte[] bytes) {
		BitSet bits = new BitSet();
		// int onesCount = 0;
		// int[] bitpos=new int[100];

		for (int i = 0; i < bytes.length * 8; i++) {
			if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
				bits.set(i);
			}
		}

		return bits;
	}

}
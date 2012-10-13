// Copyright 2002-2005 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.

package jpl.pds.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Abstract querier that chunks product results.
 *
 * @author Kelly.
 */
public abstract class ChunkedFileHandler implements FileQuerier {
	/**
	 * Creates a new <code>ChunkedFileHandler</code> instance.
	 */
	protected ChunkedFileHandler() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				TIMER.cancel();
				for (Iterator i = products.values().iterator(); i.hasNext();) {
					Product p = (Product) i.next();
					p.close();
				}
			}
		});

		ProductReaper reaper = new ProductReaper();
		TIMER.schedule(reaper, PRODUCT_EXPIRY, PRODUCT_EXPIRY);
	}

	public byte[] retrieveChunk(String id, long offset, int length) throws IOException {
		Product product = (Product) products.get(id);
		if (product == null) return null;
		product.postponeExpiration();

		File file = product.getFile();
		BufferedInputStream in = null;
		try {
			if (offset < 0) throw new IllegalArgumentException("Can't read past beginning of file");
			if (offset >= file.length()) throw new IllegalArgumentException("Can't start reading past end of file");

			// This is the exception that made it into Tomcat's catalina.out:
			if (length + offset > file.length()) throw new IllegalArgumentException("Can't read past end of file");

			if (length == 0) return new byte[0];

			in = new BufferedInputStream(new FileInputStream(file));

			while (offset > 0)
				offset -= in.skip(offset);
			
			int bufOffset = 0;
			int left = length;
			byte[] data = new byte[length];
			while (left > 0) {
				int n = in.read(data, bufOffset, left);
				if (n == -1) break;
				bufOffset += n;
				left -= n;
			}
			return data;
		} finally {
			if (in != null) try {
				in.close();
			} catch (IOException ignore) {}
		}
	}

	public void close(String id) throws IOException {
		Product product = (Product) products.remove(id);
		if (product == null) return;
		product.close();
	}

	/**
	 * Get the file corresponding to a product ID.
	 *
	 * @param id Product ID.
	 * @return a <code>File</code> value.
	 */
	protected File getFile(String id) {
		Product product = (Product) products.get(id);
		if (product == null) return null;
		return product.getFile();
	}

	/**
	 * Add a new product.
	 *
	 * @param id Product ID.
	 * @param file File that contains the product date.
	 * @param temporary True if the file is a temporary file, false otherwise.
	 */
	protected void addProduct(String id, File file, boolean temporary) {
		products.put(id, new Product(id, file, temporary)); 
	}

	/**
	 * Container for products being managed.
	 */
	private class Product {
		/**
		 * Creates a new <code>Product</code> instance.
		 *
		 * @param id Product ID.
		 * @param file File that contains the product date.
		 * @param temporary True if the file is a temporary file, false otherwise.
		 */
		public Product(String id, File file, boolean temporary) {
			this.id = id;
			this.file = file;
			this.temporary = temporary;
			lastActivity = System.currentTimeMillis();
		}

		/**
		 * Postpone expiration of this product.
		 */
		public void postponeExpiration() {
			lastActivity = System.currentTimeMillis();
		}			

		public boolean isStale() {
			return lastActivity < (System.currentTimeMillis() - PRODUCT_EXPIRY);
		}

		/**
		 * Return the file that contains this product's date.
		 *
		 * @return a <code>File</code> value.
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Close off this product.
		 */
		public synchronized void close() {
			if (temporary) file.delete();
		}

		/** Product ID. */
		private String id;

		/** File containing product data. */
		private File file;

		/** True if <var>file</var> is temporary and should be deleted eventually. */
		private boolean temporary;

		/** Time of last I/O. */
		private long lastActivity;
	}

	/**
	 * Clock task that checks for stale products and removes them.
	 */
	private class ProductReaper extends TimerTask {
		public void run() {
			for (Iterator i = products.values().iterator(); i.hasNext();) {
				Product p = (Product) i.next();
				if (p.isStale()) {
					p.close();
					i.remove();
				}
			}
		}
	}

	/** Map of product IDs to Products. */
	private Map products = Collections.synchronizedMap(new HashMap());

	/** One timer is all that's needed. */
	private static final Timer TIMER = new Timer();

	/** In how much time (in milliseconds) should a product expire. */
	private static final long PRODUCT_EXPIRY = Long.getLong("jpl.pds.server.ChunkedFileHandler.expiry", 10*60*1000).longValue();
}

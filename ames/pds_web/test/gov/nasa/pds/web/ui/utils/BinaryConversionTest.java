package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.web.BaseTestCase;

public class BinaryConversionTest extends BaseTestCase {

	// TODO tests for binary real, binary complex, when handled

	// TODO tests for binary strings - like normal strings?
	public void testMSBSigned() {
		Object[][] obj = new Object[][] {
				{
						new byte[] { (byte) 0x1D, (byte) 0xCD, (byte) 0x65,
								(byte) 0x07 }, 0x1DCD6507 }, // 50,0000,007
				{ new byte[] { 0 }, 0 },
				{ new byte[] { 0, 0 }, 0 },
				{ new byte[] { 0, 0, 0 }, 0 },
				{ new byte[] { 0, 0, 0, 0 }, 0 },
				{ new byte[] { 1 }, 1 },
				{ new byte[] { 0, 1 }, 1 },
				{ new byte[] { 0, 0, 1 }, 1 },
				{ new byte[] { 0, 0, 0, 1 }, 1 },
				{ new byte[] { (byte) 0xFF }, -1 },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF }, -1 },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, -1 },
				{
						new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
								(byte) 0xFF }, -1 },
				{ new byte[] { (byte) 0x80 }, 0xFFFFFF80 },
				{ new byte[] { (byte) 0xFF, (byte) 0x80 }, 0xFFFFFF80 },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0x80 },
						0xFFFFFF80 },
				{
						new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
								(byte) 0x80 }, 0xFFFFFF80 },
				{ new byte[] { (byte) 0x12 }, 0x12 },
				{ new byte[] { (byte) 0x12, (byte) 0x34 }, 0x1234 },
				{ new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x56 },
						0x123456 },
				{
						new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x56,
								(byte) 0x78 }, 0x12345678 }, };

		for (int i = 0; i < obj.length; i++) {
			byte[] byte1 = (byte[]) obj[i][0];
			assertEquals(BinaryConversionUtils.convertMSBSigned(byte1,
					byte1.length), obj[i][1]);
		}
	}

	public void testLSBSigned() {
		Object[][] obj = new Object[][] {
				{
						new byte[] { (byte) 0x07, (byte) 0x65, (byte) 0xCD,
								(byte) 0x1D }, 0x1DCD6507 }, // 50,0000,007
				{ new byte[] { 0 }, 0 }, //
				{ new byte[] { 0, 0 }, 0 },//
				{ new byte[] { 0, 0, 0 }, 0 },//
				{ new byte[] { 0, 0, 0, 0 }, 0 },//
				{ new byte[] { 1 }, 1 },//
				{ new byte[] { 1, 0 }, 1 }, //
				{ new byte[] { 1, 0, 0 }, 1 }, //
				{ new byte[] { 1, 0, 0, 0 }, 1 },
				{ new byte[] { (byte) 0xFF }, -1 },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF }, -1 },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, -1 },
				{
						new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
								(byte) 0xFF }, -1 },

				{ new byte[] { (byte) 0x80 }, 0xFFFFFF80 },
				{ new byte[] { (byte) 0x80, (byte) 0xFF }, 0xFFFFFF80 },
				{ new byte[] { (byte) 0x80, (byte) 0xFF, (byte) 0xFF },
						0xFFFFFF80 },
				{
						new byte[] { (byte) 0x80, (byte) 0xFF, (byte) 0xFF,
								(byte) 0xFF }, 0xFFFFFF80 },
				{ new byte[] { (byte) 0x12 }, 0x12 },
				{ new byte[] { (byte) 0x34, (byte) 0x12 }, 0x1234 },
				{ new byte[] { (byte) 0x56, (byte) 0x34, (byte) 0x12 },
						0x123456 },
				{
						new byte[] { (byte) 0x78, (byte) 0x56, (byte) 0x34,
								(byte) 0x12 }, 0x12345678 }, };

		for (int i = 0; i < obj.length; i++) {
			byte[] byte1 = (byte[]) obj[i][0];
			assertEquals(BinaryConversionUtils.convertLSBSigned(byte1,
					byte1.length), obj[i][1]);
		}

	}

	public void testMSBUnsigned() {
		// use ints to hold test data too large for java's signed bytes
		Object[][] ints = new Object[][] {
				{ new int[] { 0xFF, 0xFF, 0xFF, 0xFF }, 0xFFFFFFFFL }, // 42,294,967,295
				{ new int[] { 0xF3, 0xF2, 0xF1, 0xF0 }, 0xF3F2F1F0L } // 4,092,785,136

		};
		for (int i = 0; i < ints.length; i++) {
			int[] int1 = (int[]) ints[i][0];

			Long converted = BinaryConversionUtils.convertMSBUnsigned(int1,
					int1.length);
			Long expected = Long.valueOf(ints[i][1].toString());
			assertEquals(0, converted.compareTo(expected));

		}
		Object[][] obj = new Object[][] {
				{ new byte[] { (byte) 0xF5 }, 0xF5 }, // 0xF5 = 245

				{ new byte[] { (byte) 0xFF }, 255 },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF }, 65535 },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF },
						16777215 },

				{ new byte[] { 0 }, 0 },
				{ new byte[] { 0, 0 }, 0 },
				{ new byte[] { 0, 0, 0 }, 0 },
				{ new byte[] { 0, 0, 0, 0 }, 0 },
				{ new byte[] { 1 }, 1 },
				{ new byte[] { 0, 1 }, 1 },
				{ new byte[] { 0, 0, 1 }, 1 },
				{ new byte[] { 0, 0, 0, 1 }, 1 },

				{ new byte[] { (byte) 0x12 }, 0x12 },
				{ new byte[] { (byte) 0x12, (byte) 0x34 }, 0x1234 },
				{ new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x56 },
						0x123456 },
				{
						new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x56,
								(byte) 0x78 }, 0x12345678 },

		};

		for (int i = 0; i < obj.length; i++) {
			byte[] byte1 = (byte[]) obj[i][0];

			Long converted = BinaryConversionUtils.convertMSBUnsigned(byte1,
					byte1.length);
			Long expected = Long.valueOf(obj[i][1].toString());
			assertEquals(0, converted.compareTo(expected));

		}

	}

	public void testLSBUnsigned() {
		// use ints to hold test data too large for java's signed bytes
		Object[][] ints = new Object[][] {
				{ new int[] { 0xFF, 0xFF, 0xFF, 0xFF }, 0xFFFFFFFFL }, // 42,294,967,295
				{ new int[] { 0xF0, 0xF1, 0xF2, 0xF3 }, 0xF3F2F1F0L }, // 4,092,785,136
				{ new int[] { 0xF3, 0xF2, 0xF1, 0xF0 }, 4042388211L } // 4,042,388,211L

		};
		for (int i = 0; i < ints.length; i++) {
			int[] int1 = (int[]) ints[i][0];

			Long converted = BinaryConversionUtils.convertLSBUnsigned(int1,
					int1.length);
			Long expected = Long.valueOf(ints[i][1].toString());
			assertEquals(0, converted.compareTo(expected));

		}

		Object[][] obj = new Object[][] {
				{ new byte[] { 0 }, 0 }, //
				{ new byte[] { 0, 0 }, 0 },//
				{ new byte[] { 0, 0, 0 }, 0 },//
				{ new byte[] { 0, 0, 0, 0 }, 0 },//
				{ new byte[] { 1 }, 1 },//
				{ new byte[] { 1, 0 }, 1 }, //
				{ new byte[] { 1, 0, 0 }, 1 }, //
				{ new byte[] { 1, 0, 0, 0 }, 1 },
				{ new byte[] { (byte) 0x12 }, 0x12 },
				{ new byte[] { (byte) 0x34, (byte) 0x12 }, 0x1234 },
				{ new byte[] { (byte) 0x56, (byte) 0x34, (byte) 0x12 },
						0x123456 },
				{
						new byte[] { (byte) 0x78, (byte) 0x56, (byte) 0x34,
								(byte) 0x12 }, 0x12345678 },

		};

		for (int i = 0; i < obj.length; i++) {
			byte[] byte1 = (byte[]) obj[i][0];
			Long converted = BinaryConversionUtils.convertLSBUnsigned(byte1,
					byte1.length);
			Long expected = Long.valueOf(obj[i][1].toString());
			assertEquals(0, converted.compareTo(expected));
		}

	}

}

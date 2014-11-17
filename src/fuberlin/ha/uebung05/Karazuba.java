package fuberlin.ha.uebung05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Random;

class Tuple {
	String a;
	String b;
	
	public Tuple(String a, String b) {
		this.a = a;
		this.b = b;
	}
}

public class Karazuba {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BigInteger a, b, result = null;

		while (result == null || !result.equals(0)) {
			System.out.println("\nBitte gib zwei Zahlen ein (getrennt durch Enter):");
			
			a = new BigInteger(br.readLine());
			b = new BigInteger(br.readLine());

			result = new BigInteger(mul(a.toString(2), b.toString(2)), 2);

			System.out.println(a + " * " + b + " = " + result.toString(10));
		}
		br.close();
		
//		for (int i=1000; i <= 10000; i+=1000)
//			test(i);
	}
	
	public static void test(int n) {
		System.out.print("n=" + n +"\t [");
		
		// generate random numbers
		Random rnd = new Random();
		BigInteger delimiter = BigInteger.TEN.pow(n);
		Tuple[] entries = new Tuple[10];
		for(int i=0; i < entries.length; ++i) {
			BigInteger a = generateBigInteger(rnd, delimiter);
			BigInteger b = generateBigInteger(rnd, delimiter);
			entries[i] = new Tuple(a.toString(2),b.toString(2));
		}

		long startTime, endTime, currentTime, totalTime = 0;
		String[] results = new String[10];
		for(int i=0; i < entries.length; ++i) {
			// benchmark calculation
			startTime = System.currentTimeMillis();
			results[i] = mul(entries[i].a, entries[i].b);
			endTime = System.currentTimeMillis();
			
			// output
			currentTime = endTime-startTime;
			totalTime += currentTime;
			System.out.print(currentTime+ "ms, "); 
		}
		System.out.print("]"); 
		
		System.out.println("  Average: " + ((float) totalTime)/entries.length + "\n\n");
	}

	private static BigInteger generateBigInteger(Random rnd, BigInteger delimiter) {
		BigInteger b;
		do {
		    b = new BigInteger(delimiter.bitLength(), rnd);
		} while (b.compareTo(delimiter) >= 0);
		return b;
	}

	/**
	 * Calculates Karazuba's algorithm to efficiently multiply two numbers
	 * 
	 * @param x (binary string of number one)
	 * @param y (binary string of number two)
	 * @return x*y (in binary form)
	 */
	public static String mul(String x, String y) {
		// recursion anchor - do multiplication with native java int values
		// multiplication of two 15-bit ints will never exceed 32 bit (int max)
		if (isZero(x) || isZero(y))
			return "0";
		
		if (getLength(x) < 16 && getLength(y) < 16) {
//			System.out.println("native calculation:  "+Integer.parseInt(x, 2)+"*"+Integer.parseInt(y, 2) );
			return Integer.toBinaryString(Integer.parseInt(x, 2) * Integer.parseInt(y, 2));
		}

		// apply karazuba algorithm
		x = paddNumber(x, y);
		y = paddNumber(y, x);

		// split x and y into high and low parts
		int n = x.length() / 2;
		String Xh, Xl, Yh, Yl;
		Xh = x.substring(0, n);
		Xl = x.substring(n);
		Yh = y.substring(0, n);
		Yl = y.substring(n);

		// apply recursion formula
		String P1, P2, P3;
		P1 = mul(Xh, Yh);
		P2 = mul(Xl, Yl);
		P3 = mul(add(Xh, Xl), add(Yh, Yl));

//		BigInteger P1B = new BigInteger(Xh, 2).multiply(new BigInteger(Yh, 2));
//		BigInteger P2B = new BigInteger(Xl, 2).multiply(new BigInteger(Yl, 2));
//		BigInteger P3B = (new BigInteger(Xh, 2).add(new BigInteger(Xl, 2))).multiply(
//						  new BigInteger(Yh, 2).add(new BigInteger(Yl, 2)));
//		
//		System.out.println("Xh:"+Xh+"("+new BigInteger(Xh, 2).toString(10)+")");
//		System.out.println("Xl:"+Xl+"("+new BigInteger(Xl, 2).toString(10)+")");
//		System.out.println("Yh:"+Yh+"("+new BigInteger(Yh, 2).toString(10)+")");
//		System.out.println("Yl:"+Yl+"("+new BigInteger(Yl, 2).toString(10)+")");
//		System.out.println("P1: "+new BigInteger(P1, 2).toString(10)+", "+P1B.toString(10));
//		System.out.println("P2: "+new BigInteger(P2, 2).toString(10)+", "+P2B.toString(10));
//		System.out.println("P3: "+new BigInteger(P3, 2).toString(10)+", "+P3B.toString(10));
		
		return add(add(shiftLeft(P1, 2*n), shiftLeft(sub(P3, add(P1, P2)), n)),P2);
	}

	/**
	 * checks if given binary string represents the number zero
	 * 
	 * @param x
	 * @return
	 */
	private static boolean isZero(String x) {
		return getLength(x) == 0;
	}

	/**
	 * calucaltes the real length of a binary number. Prepended zeros will count.
	 * @param x
	 * @return
	 */
	private static int getLength(String x) {
		int i = 0;
		for (; i < x.length(); ++i) {
			if (x.charAt(i) != '0') {
				break;
			}
		}
		return x.length() - i;
	}

	/**
	 * subtracts x from y (be careful x has to be bigger than y)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private static String sub(String x, String y) {
		StringBuilder sb = new StringBuilder();
		boolean a, b, tmp = false;
		char token;
		
		x = paddNumber(x, y);
		y = paddNumber(y, x);
		
		for (int i = x.length() - 1; i >= 0; --i) {
			a = getBit(x.charAt(i));
			b = getBit(y.charAt(i));
			// System.out.println(a+","+b+","+tmp);

			if (a && b) {
				token = tmp ? '1' : '0';
			} else if (a && !b) {
				token = tmp ? '0' : '1';
				tmp = false;
			} else if (!a && b) {
				token = tmp ? '0' : '1';
				tmp = !tmp ? true : tmp;
			} else {
				token = (tmp) ? '1' : '0';
			}

			sb.append(token);
		}

		return sb.reverse().toString();
	}

	/**
	 * Adds two binary numbers
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private static String add(String x, String y) {
		StringBuilder sb = new StringBuilder();
		boolean a, b;
		int overflow = 0;
		char token;
				
		x = paddNumber(x, y);
		y = paddNumber(y, x);

		for (int i = x.length() - 1; i >= 0; --i) {
			a = getBit(x.charAt(i));
			b = getBit(y.charAt(i));

			if (a && b) {
				token = (overflow > 0) ? '1' : '0';
				overflow = (token == '1') ? overflow : overflow + 1;
			} else if (a || b) {
				token = (overflow > 0) ? '0' : '1';
			} else {
				token = ((overflow > 0)) ? '1' : '0';
				overflow = (overflow > 0) ? overflow - 1 : 0;
			}

			sb.append(token);
		}

		for (; overflow > 0; --overflow)
			sb.append('1');

		return sb.reverse().toString();
	}

	/**
	 * get char as boolean flag
	 * 
	 * @param c
	 * @return
	 */
	private static boolean getBit(char c) {
		return (c == '1') ? true : false;
	}

	/**
	 * shifts given binary number by length entries to the left
	 * 
	 * @param x
	 * @param length
	 * @return
	 */
	private static String shiftLeft(String x, int length) {
		StringBuilder prefix = new StringBuilder(x);
		for (int i=0; i < length; ++i)
			prefix.append('0');
		return prefix.toString();
	}

	/**
	 * Adjust two strings so that they have the same length 
	 * @param a
	 * @param b
	 * @return
	 */
	private static String paddNumber(String a, String b) {
		// extend to min even length of b
		while (a.length() < b.length() || a.length() % 2 != 0)
			a = '0' + a;

		// TODO: without sqrt?
		while(Math.round(Math.sqrt(a.length()))==Math.sqrt(a.length()))
			a = "00" + a;

		return a;
	}
}

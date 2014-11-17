
public class Karazuba {
	
	private static Integer base = 10;
	private static Integer anchor_length = 10;
	
	public static void main(String args[]) {
		String x = args[0];
		String y = args[1];

		System.out.println(k_multiply(x, y));
	}

	private static String k_multiply(String x,  String y) {
		// set induction-anchor xD
		// if numbers are short enough, simply use Integer operations
		if (x.length() < anchor_length && y.length() < anchor_length) {
			return new Integer(Integer.parseInt(x) * Integer.parseInt(y)).toString();
		}
		
		// calculate minimum power (power_length) of two that is both higher than x and y
		Integer max = Math.max(x.length(), y.length());
		Integer power_length = 1;
		do {
			power_length = power_length*2;
		} while(power_length < max);
		Integer n = power_length / 2;
		
		// pad the two strings (works since numbers don't contain spaces!)
		x = String.format("%"+power_length+"s",x).replace(' ', '0');
		y = String.format("%"+power_length+"s",y).replace(' ', '0');

		String x_h = x.substring(0, power_length/2-1); // power_length is even or 1
		String x_l = x.substring(power_length/2, power_length-1);
		String y_h = y.substring(0, power_length/2-1);
		String y_l = y.substring(power_length/2, power_length-1);
		
		// recursive call
		String p1 = k_multiply(x_h, y_h);
		String p2 = k_multiply(x_l, y_l);
		String p3 = k_multiply(add(x_h, x_l), add(x_h, y_l));
		// \recursive call
		
		String t1 = shift(p1, base^power_length);
		String t2 = shift(subtract(p3, add(p1, p2)), base^n);
		String t3 = p2;

		return add(t1, add(t2, t3));
	}
	
	/**
	 * Simply right-pads the passed String x with power zeros.
	 */
	private static String shift(String x, Integer power) {
		return String.format("%-"+power+"s", x).replace(' ', '0');
	}
	
	/**
	 * Adds the two given Strings as natural Numbers x and y base-transparent.
	 * The two Strings must be of the same length!
	 */
	private static String add(String x, String y) {
		if (x.length() != y.length()) {
			// pad the two strings (works since numbers don't contain spaces!)
			Integer max = Math.max(x.length(), y.length());
			x = String.format("%"+max+"s",x).replace(' ', '0');
			y = String.format("%"+max+"s",y).replace(' ', '0');
		}
        Integer size = Math.max(x.length(), y.length());
        StringBuffer buf = new StringBuffer(size);
        Integer carriage = 0;
        Integer res = 0;
		for(int i = 0; i < size; i++) {
			res = carriage + Integer.parseInt(x.substring(size-i-1,size-i)) + Integer.parseInt(y.substring(size-i-1, size-i));
			if (res > base) {
				res = res % base;
				carriage = 1;
			} else {
				carriage = 0;
			}
			buf.append(res.toString().charAt(0));
		}
		return buf.reverse().toString();
	}
	
	/**
	 * Subtracts y from x char-wise.
	 * The two Strings must be of the same length!
	 */
	private static String subtract(String x, String y) {
		if (x.length() != y.length()) {
			// pad the two strings (works since numbers don't contain spaces!)
			Integer max = Math.max(x.length(), y.length());
			x = String.format("%"+max+"s",x).replace(' ', '0');
			y = String.format("%"+max+"s",y).replace(' ', '0');
		}
        Integer size = Math.max(x.length(), y.length());
        StringBuffer buf = new StringBuffer(size);
        Integer carriage = 0;
        Integer res = 0;
        for(int i = 0; i < size; i++) {
			res = Integer.parseInt(x.substring(size-i-1,size-i)) - (Integer.parseInt(y.substring(size-i-1, size-i)) + carriage);
			if (res < 0) {
				res = res % base; // this will return a negative value, which is not the desired behavior;
				if (res < 0) { // the value will be negative, so this if-clause is actually unnecessary but provided due to illustratory purposes
					res += base;
				}
				carriage = 1;
			} else {
				carriage = 0;
			}
			buf.append(res.toString().charAt(0));
        }
		return buf.reverse().toString();
	}
}

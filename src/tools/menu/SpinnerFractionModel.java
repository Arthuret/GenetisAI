package tools.menu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An implementation of SipnnerModel that support fractions
 * This model only store and manage x in 1/x
 * @author Arthur France
 *
 */
public class SpinnerFractionModel implements SpinnerModel{

	int fraction;
	int max;
	boolean allow0;
	private List<ChangeListener> listeners = new ArrayList<>();
	
	/**
	 * Create a SpinnerFractionModel
	 * @param initialValue the initial value will be 1/initialvalue
	 * @param allow0 whether to allow the 0 value
	 * @param max the minimum fraction value = 1/max
	 */
	public SpinnerFractionModel(int initialValue,boolean allow0,int max){
		fraction = initialValue;
		this.allow0 = allow0;
		this.max = max;
	}
	@Override
	public Object getValue() {
		return (fraction == 1)?"1":(fraction == 0)?"0":"1/"+fraction;
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof Integer) {
			fraction = (Integer)value;
			listeners.forEach(l->l.stateChanged(new ChangeEvent(this)));
			return;
		}
		if(value instanceof String) {
			String s = (String) value;
			if(s.startsWith("1/")) {
				s = s.substring(2);
			}
			try {
				fraction = Integer.parseInt(s);
				limit();
				listeners.forEach(l->l.stateChanged(new ChangeEvent(this)));
			}catch(NumberFormatException e) {
				return;
			}
		}else
			System.err.println(value.getClass()+" "+value);
	}
	
	public int getDivider() {
		return fraction;
	}

	@Override
	public Object getNextValue() {
		if(fraction == max)
			return null;
		if(fraction == 0)
			return 1;
		int numberOfDigits = 0;
		int firstDigit = 0;
		int temp = fraction;
		while(temp != 0) {
			numberOfDigits++;
			firstDigit = temp%10;
			temp/=10;
		}
		firstDigit++;
		return (int)(firstDigit*Math.pow(10, numberOfDigits-1));
	}

	@Override
	public Object getPreviousValue() {
		if(allow0) {
			if(fraction == 0)
				return null;
		}else {
			if(fraction == 1)
				return null;
		}
		int numberOfDigits = 0;
		int firstDigit = 0;
		int temp = fraction;
		while(temp != 0) {
			numberOfDigits++;
			firstDigit = temp%10;
			temp/=10;
		}
		if(firstDigit == 1) {
			firstDigit = 9;
			numberOfDigits--;
		}else
			firstDigit--;
		return (int)(firstDigit*Math.pow(10, numberOfDigits-1));
	}

	@Override
	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}

	@Override
	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}
	
	private void limit() {
		if(fraction > max)
			fraction = max;
		if(allow0) {
			if(fraction < 0)
				fraction = 0;
		}else {
			if(fraction < 1)
				fraction = 1;
		}
	}
	
}

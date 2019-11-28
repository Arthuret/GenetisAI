package tools.menu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An implementation of SipnnerModel that support percentage
 * it will display it's value as x%
 * otherwise it's a normal SpinnerNumberModel of increment 1
 * @author Arthur France
 *
 */
public class SpinnerPercentModel implements SpinnerModel{
	int percent;
	int max;
	int min;
	private List<ChangeListener> listeners = new ArrayList<>();
	
	/**
	 * Create a SpinnerPercentModel
	 * @param initialValue the initial percentage
	 * @param min the minimum percentage
	 * @param max the maximum percentage
	 */
	public SpinnerPercentModel(int initialValue,int min,int max) {
		percent = initialValue;
		this.max = max;
		this.min = min;
	}
	
	/**
	 * Create a SpinnerPercentModel limited between 0% and 100%
	 * @param initialValue the initial percentage
	 */
	public SpinnerPercentModel(int initialValue) {
		this(initialValue,0,100);
	}

	@Override
	public Object getValue() {
		return Integer.toString(percent)+"%";
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof Integer) {
			percent = (Integer) value;
			listeners.forEach(l->l.stateChanged(new ChangeEvent(this)));
			return;
		}
		if(value instanceof String) {
			String s = (String)value;
			if(s.endsWith("%")) {
				s = s.substring(0, s.length()-1);
			}
			try {
				percent = Integer.parseInt(s);
				limit();
				listeners.forEach(l->l.stateChanged(new ChangeEvent(this)));
			}catch(NumberFormatException e) {
				return;
			}
		}else
			System.err.println(value.getClass()+" "+value);
	}
	
	public int getPercent() {
		return percent;
	}

	@Override
	public Object getNextValue() {
		return (percent == max)?null:percent+1;
	}

	@Override
	public Object getPreviousValue() {
		return (percent == min)?null:percent-1;
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
		if(percent < min)
			percent = min;
		if(percent > max)
			percent = max;
	}
}

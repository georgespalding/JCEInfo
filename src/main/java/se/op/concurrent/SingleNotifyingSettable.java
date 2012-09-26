package se.op.concurrent;


public class SingleNotifyingSettable<V> extends Setter<SettingDoneListener,V>{

	
	public SingleNotifyingSettable(SettingDoneListener bean, Class<V> type) throws SecurityException, NoSuchMethodException {
		this(bean,type,"set");
	}

	public SingleNotifyingSettable(SettingDoneListener bean, Class<V> type,String method) throws SecurityException, NoSuchMethodException {
		super(bean, type);
	}

	@Override
	public void set(V value) {
		super.set(value);
		bean.settingsDone();
	}

}

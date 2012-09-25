package se.op.concurrent;


public class PeerAwareNotifyingSettable<V> extends Setter<SettingDoneListener,V>{

	private SettingDoneNotifier notif;
	public PeerAwareNotifyingSettable(SettingDoneNotifier notif, Class<V> type) throws SecurityException, NoSuchMethodException {
		this(notif,type,"set");
	}

	public PeerAwareNotifyingSettable(SettingDoneNotifier notif, Class<V> type,String method) throws SecurityException, NoSuchMethodException {
		super(notif.bean, type, method);
		this.notif=notif;
		this.notif.addSettable(this);
	}

	@Override
	public void set(V value) {
		super.set(value);
		notif.settingDone(this);
	}

}

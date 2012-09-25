package se.op.concurrent;

public class SettableTargetGuard{

	private SettingDoneNotifier notif;
	public SettableTargetGuard(SettingDoneListener bean) {
		notif=new SettingDoneNotifier(bean);
	}

	public <V> Settable<V> addSettable(Class<V> type) throws SecurityException, NoSuchMethodException{
		return new PeerAwareNotifyingSettable<V>(notif, type);
	}

	public <V> Settable<V> addSettable(Class<V> type,String meth) throws SecurityException, NoSuchMethodException{
		return new PeerAwareNotifyingSettable<V>(notif, type, meth);
	}
}

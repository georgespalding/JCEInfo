package se.op.jce;

import java.security.Provider;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

public class ProviderInfo extends JComponent {
	private Provider provider;
	public ProviderInfo(Provider provider) {
		super();
		this.provider=provider;
		add(new JLabel(provider.getName()));
		add(new JLabel(provider.getVersion()+""));
		add(new JLabel(provider.getServices()+""));
		provider.getServices();
	}
	
	
}

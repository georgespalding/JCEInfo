package se.op.jce;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Provider;
import java.security.Security;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import se.op.jce.model.JCEInfoModel;
import se.op.jce.model.JCEInfoModel.ServiceHolder;
import se.op.jce.model.LoadProviderModel;

public class JCEInfoController {

	private JCEInfo gui;
	
	private JCEInfoModel model=new JCEInfoModel();

	public JCEInfoController(JCEInfo gui){
		this.gui=gui;
	}
	
	public JCEInfoModel getModel() {
		return model;
	}

	public void treeNodeSelected(TreePath tp){
		Object selected=tp.getLastPathComponent();
		if(selected instanceof DefaultMutableTreeNode){
			selected=((DefaultMutableTreeNode)selected).getUserObject();
			if(selected instanceof Provider){
				gui.ta.setText(((Provider)selected).getInfo());
			}else if(selected instanceof ServiceHolder){
				gui.ta.setText(((ServiceHolder)selected).getService().toString());
			}else{
				gui.ta.setText(selected.toString());
			}
		}else{
			gui.ta.setText(selected.toString());
		}
	}

	public void removeRuntimeProvider(LoadProviderModel providerModel) {
		if(!model.getLoadProviderListModel().remove(providerModel)){
			return;
		}

		Security.removeProvider(providerModel.getName());
		providerModel.setLoaded(false);
		model.reReadProviders();
	}

	public void updateRuntimeProvider(LoadProviderModel providerModel) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		removeRuntimeProvider(providerModel);
		addRuntimeProvider(providerModel);
	}

	public void addRuntimeProvider(LoadProviderModel providerModel) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		model.getLoadProviderListModel().add(providerModel);
		List<File> files=providerModel.getClassPath();
		URL[] urls=new URL[files.size()];
		int i=0;
		for (File file : files) {
			urls[i++]=file.toURI().toURL();
		}
		URLClassLoader loader= new URLClassLoader(urls,getClass().getClassLoader());
		Class<?> provClass=Class.forName(providerModel.getClassName(),true,loader);
		Provider provInst=(Provider)provClass.newInstance();
		providerModel.setName(provInst.getName());
		Security.addProvider(provInst);
		providerModel.setLoaded(true);
		model.reReadProviders();
	}

	public void startTest(String text, JLabel lbl) {
		// TODO Auto-generated method stub
		lbl.setText("hej");
	}

	public void stopTest(JLabel lbl) {
		lbl.setText("hejdå");
		
	}
	
}

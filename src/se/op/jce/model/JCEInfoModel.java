package se.op.jce.model;

import java.security.Provider;
import java.security.Security;
import java.security.Provider.Service;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import se.op.jce.Tuple;
import se.op.jce.swing.GenericComboBoxModel;
import se.op.jce.swing.GenericListModel;

public class JCEInfoModel {
	private DefaultTreeModel tm=new DefaultTreeModel(new DefaultMutableTreeNode("Not inited"));
	private GenericListModel<LoadProviderModel> rplm=new GenericListModel<LoadProviderModel>();
	private GenericComboBoxModel<Tuple<Provider,GenericComboBoxModel<Tuple<String,GenericComboBoxModel<String>>>>> provComboBoxModel=new GenericComboBoxModel<Tuple<Provider,GenericComboBoxModel<Tuple<String,GenericComboBoxModel<String>>>>>();
	private GenericComboBoxModel<Integer> sigKeyLens=new GenericComboBoxModel<Integer>(128,256,512,1024,2048,4096);
	private GenericComboBoxModel<Integer> cryptKeyLens=new GenericComboBoxModel<Integer>(128,256);
	
	public JCEInfoModel(){
		reReadProviders();
	}
	public DefaultTreeModel getProviderTreeModel(){
		return tm;
	}
	
	public GenericListModel<LoadProviderModel> getLoadProviderListModel(){
		return rplm;
	}
	
	public void reReadProviders(){
		DefaultMutableTreeNode newRoot=new DefaultMutableTreeNode("Providers");

		provComboBoxModel.clear();
		
		for(Provider prov:Security.getProviders()){
			Map<String,List<ServiceHolder>> type2ServiceHolders=new HashMap<String, List<ServiceHolder>>();
			for(Service serv:prov.getServices()){
				List<ServiceHolder> srvs=type2ServiceHolders.get(serv.getType());
				if(srvs==null){
					srvs=new LinkedList<ServiceHolder>();
					type2ServiceHolders.put(serv.getType(),srvs);
				}
				srvs.add(new ServiceHolder(serv));
			}

			DefaultMutableTreeNode providerNode=new DefaultMutableTreeNode(prov);
			Tuple<Provider,GenericComboBoxModel<Tuple<String,GenericComboBoxModel<String>>>> prov2Types=new Tuple<Provider, GenericComboBoxModel<Tuple<String,GenericComboBoxModel<String>>>>(prov,new GenericComboBoxModel<Tuple<String,GenericComboBoxModel<String>>>());
			for(Map.Entry<String, List<ServiceHolder>> type2serviceHolder:type2ServiceHolders.entrySet()){
				DefaultMutableTreeNode typeNode=new DefaultMutableTreeNode(type2serviceHolder.getKey());
				
				Tuple<String,GenericComboBoxModel<String>> type2algs=new Tuple<String,GenericComboBoxModel<String>>(type2serviceHolder.getKey(), new GenericComboBoxModel<String>());
				for (ServiceHolder alg:type2serviceHolder.getValue()) {
					DefaultMutableTreeNode algNode=new DefaultMutableTreeNode(alg,false);
					typeNode.add(algNode);
					type2algs.value.add(alg.toString());
					System.err.println("Add "+prov2Types.key+"->"+type2algs.key+" -> "+alg.toString());
				}
				providerNode.add(typeNode);
				prov2Types.value.add(type2algs);
			}
			newRoot.add(providerNode);

			tm.setRoot(newRoot);
			provComboBoxModel.add(prov2Types);
		}
	}
	
	public GenericComboBoxModel<Tuple<Provider,GenericComboBoxModel<Tuple<String,GenericComboBoxModel<String>>>>> getProviderListModel(){
		return provComboBoxModel;
	}

	public GenericComboBoxModel<Integer> getSignatureKeyLenModel(){
		return sigKeyLens;
	}

	public GenericComboBoxModel<Integer> getCipherKeyLenModel(){
		return cryptKeyLens;
	}

	public static class ServiceHolder{
		private Service serv;
		ServiceHolder(Service serv){
			this.serv=serv;
		}

		public Service getService(){
			return serv;
		}

		public String toString(){
			String ret=serv.getAlgorithm();//+" ("+serv.getProvider()+")";
			return ret;
		}
	}
	
}

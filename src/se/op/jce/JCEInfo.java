package se.op.jce;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.Provider;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import se.op.jce.model.JCEInfoModel;
import se.op.jce.model.LoadProviderModel;
import se.op.jce.swing.GenericComboBoxModel;

/*
org.bouncycastle.jce.provider.BouncyCastleProvider
/Users/geospa/workspace_helios/bouncycasle 1.4.5/lib/bcprov-jdk16-145.jar
*/
public class JCEInfo extends JFrame{
	private static final Logger log=Logger.getLogger(JCEInfo.class.getName());

	JTextArea ta=new JTextArea();
	private JCEInfoController controller=new JCEInfoController(this);
	private JCEInfoModel model=controller.getModel();
	private TreeModel treeModel=model.getProviderTreeModel();

	private JButton add=new JButton("Add...");
	private JButton edit=new JButton("Edit");
	private JButton rem=new JButton("Remove");
	private AddProviderDialog addProvDlg;
	JList runtimeProviders=new JList(model.getLoadProviderListModel());
	
	JComboBox providerSelection;
	JComboBox typeSelection;
	JComboBox algSelection;
	JComboBox keyLenSelection;
	JLabel perfStatLbl=new JLabel("Data");
	
	private JTextField numThreadsField;
	
	JCEInfo(){
		super("JCEInfo");
		addProvDlg=new AddProviderDialog(this,controller);
		ta.setColumns(50);
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		ta.setText("Nothing selected. Click a node in the tree to se details.");

		JTabbedPane provTab=new JTabbedPane();
		//		Tab 1
		{
			JPanel providers=new JPanel();
			providers.setLayout(new BorderLayout(5, 5));
			providers.setBorder(BorderFactory.createTitledBorder("JCE Providers"));

			JTree tree=new JTree(model.getProviderTreeModel());

			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					controller.treeNodeSelected(e.getPath());
				}
			});

			providers.add(
					new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
							new JScrollPane(tree),
							new JScrollPane(ta)),
					BorderLayout.CENTER);
			provTab.add("JCE Providers",providers);
		}
		//		Tab 2
		{
			JPanel addProv=new JPanel(new BorderLayout(10,10));
			addProv.setBorder(BorderFactory.createTitledBorder("JCE Provider runtime extensions"));

			runtimeProviders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			runtimeProviders.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() == false) {
						if (runtimeProviders.getSelectedIndex() == -1) {
							//No selection, disable fire button.
							rem.setEnabled(false);
							edit.setEnabled(false);
				        } else {
				        	//Selection, enable the fire button.
				        	rem.setEnabled(true);
							edit.setEnabled(true);
				        }
				    }
				}
			});
			addProv.add(runtimeProviders,BorderLayout.CENTER);

			//Lay out the buttons from left to right.
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
			buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
			buttonPane.add(Box.createHorizontalGlue());
			rem.setEnabled(false);
			rem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					controller.removeRuntimeProvider((LoadProviderModel)runtimeProviders.getSelectedValue());
				}
			});
			buttonPane.add(rem);
			buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

			edit.setEnabled(false);
			edit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addProvDlg.setLoadProviderModel((LoadProviderModel)runtimeProviders.getSelectedValue());
				}
			});
			buttonPane.add(edit);

			add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					LoadProviderModel providerModel=new LoadProviderModel();
					addProvDlg.setLoadProviderModel(providerModel);
				}
			});
			buttonPane.add(add);

			addProv.add(buttonPane,BorderLayout.PAGE_END);
			provTab.add("Add JCE Provider",addProv);
		}
		
		// Tab 3
		{
			providerSelection=new JComboBox(model.getProviderListModel());
			providerSelection.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.err.println("Selected:"+e);
					Tuple<Provider, GenericComboBoxModel<?>> selectedItem = (Tuple<Provider, GenericComboBoxModel<?>>)providerSelection.getSelectedItem();
					if(null!=selectedItem){
						typeSelection.setModel(selectedItem.value);
						typeSelection.setEnabled(true);
						keyLenSelection.setEnabled(false);
						algSelection.setEnabled(false);
						System.err.println("Selected:"+selectedItem.key+"->"+selectedItem.value.items());
					}else{
						System.err.println("Selected:"+selectedItem);
					}
				}
			});
			typeSelection=new JComboBox();
			typeSelection.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					Tuple<String, GenericComboBoxModel<?>> selectedItem = (Tuple<String, GenericComboBoxModel<?>>)typeSelection.getSelectedItem();
					if(null!=selectedItem){
						algSelection.setModel(selectedItem.value);
						algSelection.setEnabled(true);
						if("Cipher".equals(selectedItem.key)){
							keyLenSelection.setEnabled(true);
							keyLenSelection.setModel(model.getCipherKeyLenModel());
						}else if("Signature".equals(selectedItem.key)){
							keyLenSelection.setEnabled(true);
							keyLenSelection.setModel(model.getSignatureKeyLenModel());
						}else{
							keyLenSelection.setEnabled(false);
							keyLenSelection.setModel(new GenericComboBoxModel<Integer>());
						}
					}
				}
			});
			algSelection=new JComboBox();
			keyLenSelection=new JComboBox();
			providerSelection.setBorder(BorderFactory.createTitledBorder("Provider"));
			typeSelection.setBorder(BorderFactory.createTitledBorder("Type"));
			keyLenSelection.setBorder(BorderFactory.createTitledBorder("KeyLen"));
			algSelection.setBorder(BorderFactory.createTitledBorder("Algorithm"));
			
			JPanel selectBoxPane = new JPanel();
			selectBoxPane.setLayout(new BoxLayout(selectBoxPane, BoxLayout.LINE_AXIS));
			
			selectBoxPane.add(Box.createHorizontalGlue());
			selectBoxPane.add(providerSelection);
			selectBoxPane.add(Box.createHorizontalGlue());
			selectBoxPane.add(typeSelection);
			selectBoxPane.add(Box.createHorizontalGlue());
			selectBoxPane.add(keyLenSelection);
			selectBoxPane.add(Box.createHorizontalGlue());
			selectBoxPane.add(algSelection);
			selectBoxPane.add(Box.createHorizontalStrut(5));
			
			JPanel perfProv=new JPanel(new BorderLayout(10,10));
			perfProv.setBorder(BorderFactory.createTitledBorder("JCE Provider performance test"));
			perfProv.add(selectBoxPane,BorderLayout.NORTH);

			
			
			JPanel runSettingsPane = new JPanel();
			numThreadsField=new JTextField(5);
			numThreadsField.setInputVerifier(new InputVerifier() {
				@Override
				public boolean verify(JComponent comp) {
					return ((JTextField)comp).getText().matches("\\d+");
				}
			});
			
			JButton btnDoTest=new JButton("Start");
			btnDoTest.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton btn=(JButton)e.getSource();
					if(btn.getText().equals("Start")){
						btn.setText("Stop");
						controller.startTest(numThreadsField.getText(),perfStatLbl);
					}else{
						btn.setText("Start");
						controller.stopTest(perfStatLbl);
					}
				}
			});
			runSettingsPane.setLayout(new BoxLayout(runSettingsPane, BoxLayout.LINE_AXIS));
			runSettingsPane.add(Box.createGlue());
			runSettingsPane.add(numThreadsField);
			runSettingsPane.add(Box.createGlue());
			runSettingsPane.add(btnDoTest);
			perfProv.add(runSettingsPane,BorderLayout.SOUTH);
			
			perfStatLbl.setBorder(BorderFactory.createTitledBorder("Calls per Second"));
			perfProv.add(perfStatLbl,BorderLayout.CENTER);
			provTab.add("Test JCE Provider performance",perfProv);
		}
		provTab.setSelectedIndex(2);
		add(provTab);
		pack();
	}
	
	private static void addProvidersFromSysProps() {
		String providersToAdd=System.getProperty("ProvidersToAdd");
		if(providersToAdd!=null){
			for(String providerToAdd:providersToAdd.split(",")){
				try {
					Class<? extends Provider> classToAdd=(Class<? extends Provider>)Class.forName(providerToAdd);
					Provider prov=classToAdd.newInstance();
					Security.addProvider(prov);
					log.info("Added provider "+prov.getClass().getName()+" ("+prov.getName()+" v"+prov.getVersion()+") to "+Security.class.getName()+".");
				} catch (ClassNotFoundException e) {
					log.log(Level.WARNING,"Class '"+providerToAdd+" was not found.",e);
				} catch (InstantiationException e) {
					log.log(Level.WARNING,"Class '"+providerToAdd+" could not be instantiated.",e);
				} catch (IllegalAccessException e) {
					log.log(Level.WARNING,"Class '"+providerToAdd+" could not be instantiated.",e);
				} catch (ClassCastException e){
					log.log(Level.WARNING,"Class '"+providerToAdd+" does not appear to be of type "+Provider.class.getName()+".",e);
				}
			}
		}
	}

	public static void main(String[] args) {
		addProvidersFromSysProps();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JCEInfo inst=new JCEInfo();
				inst.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        inst.setVisible(true);
			}
		});
	}
}

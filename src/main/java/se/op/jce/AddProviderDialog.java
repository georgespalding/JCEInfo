package se.op.jce;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import se.op.jce.model.LoadProviderModel;

public class AddProviderDialog extends JDialog {
	private JCEInfoController controller;
	private LoadProviderModel providerModel;

	private JTextField tfClassName;
	private JButton btnExtendClassPath;
	private JButton btnRemoveClassPathEntry;
	private JFileChooser selectClassPathExpansion;
	private JList classPathElems;
	public AddProviderDialog(JCEInfo pwn,JCEInfoController agurk) {
		super(pwn);
		this.controller=agurk;
		
		JPanel contents=new JPanel();
		contents.setLayout(new BorderLayout(10,10));
		contents.setBorder(BorderFactory.createTitledBorder("Add Provider"));
		setContentPane(contents);

		JPanel classPane=new JPanel();
		classPane.setLayout(new BoxLayout(classPane,BoxLayout.LINE_AXIS));
		classPane.add(new JLabel("Provider class"));
		tfClassName=new JTextField();
		tfClassName.setColumns(37);
		classPane.add(tfClassName,BorderLayout.LINE_END);
		btnExtendClassPath=new JButton("Extend Classpath...");
		btnExtendClassPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectClassPathExpansion.showOpenDialog(AddProviderDialog.this);
			}
		});
		classPane.add(btnExtendClassPath);

		btnRemoveClassPathEntry=new JButton("Remove selected from classpath");
		btnRemoveClassPathEntry.setEnabled(false);
		btnRemoveClassPathEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				providerModel.getClassPathModel().removeAll(classPathElems.getSelectedIndices());
			}
		});
		classPane.add(btnRemoveClassPathEntry);
		add(classPane,BorderLayout.PAGE_START);

		JPanel classPathPant=new JPanel();
		classPathPant.setBorder(BorderFactory.createTitledBorder("ClassPath"));
		
		classPathElems=new JList();
		classPathElems.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent listselectionevent) {
				if(!listselectionevent.getValueIsAdjusting()){
					btnRemoveClassPathEntry.setEnabled(classPathElems.getSelectedIndices().length>0);
				}else{
					btnRemoveClassPathEntry.setEnabled(providerModel.getClassPathModel().isEmpty());
				}
			}
		});
		classPathElems.setVisibleRowCount(7);
		add(new JScrollPane(classPathElems),BorderLayout.CENTER);

		selectClassPathExpansion=new JFileChooser();
		selectClassPathExpansion.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		selectClassPathExpansion.setDialogTitle("Select classpath extension entry");
		selectClassPathExpansion.setMultiSelectionEnabled(true);
		selectClassPathExpansion.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Java Class Archives .zip .jar";
			}
			
			@Override
			public boolean accept(File f) {
			    if (f.isDirectory()) {
			    	return true;
			    }

			    String extension = getExtension(f);
			    if (extension != null) {
			    	for(String ext:extensions){
			    		if (extension.equals(ext))
			    			return true;
			        }
			    }
			    return false;
			}

			private String [] extensions={"jar","zip"};
			private String getExtension(File f) {
		        String ext = null;
		        String s = f.getName();
		        int i = s.lastIndexOf('.');

		        if (i > 0 &&  i < s.length() - 1) {
		            ext = s.substring(i+1).toLowerCase();
		        }
		        return ext;
		    }
		});
		selectClassPathExpansion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				providerModel.getClassPathModel().addAll(selectClassPathExpansion.getSelectedFiles());
			}
		});

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
			buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
			buttonPane.add(Box.createHorizontalGlue());
			JButton btnOk=new JButton("Okay");
			btnOk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					//TODO validering
					providerModel.setClassName(tfClassName.getText().trim());
					try{
						if(!providerModel.isLoaded()){
							controller.addRuntimeProvider(providerModel);
						}else{
							controller.updateRuntimeProvider(providerModel);
						}
					}catch(Exception e){
						JOptionPane.showMessageDialog(AddProviderDialog.this, "Exception:"+e.getClass().getName()+"\nMessage:'"+e.getMessage()+"'",
								"Error adding provider",
							    JOptionPane.ERROR_MESSAGE);
					}
					AddProviderDialog.this.setVisible(false);
				}
			});
			buttonPane.add(btnOk);
			buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton btnCancel=new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					AddProviderDialog.this.setVisible(false);
				}
			});
			
			buttonPane.add(btnCancel);
			add(buttonPane,BorderLayout.PAGE_END);
		}
		pack();
	}
	
	public void setLoadProviderModel(LoadProviderModel loadProviderModel){
		providerModel=loadProviderModel;
		tfClassName.setText(providerModel.getClassName());
		classPathElems.setModel(providerModel.getClassPathModel());
		setVisible(true);
	}
}

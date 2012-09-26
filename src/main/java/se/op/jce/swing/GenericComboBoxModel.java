package se.op.jce.swing;

import javax.swing.ComboBoxModel;

public class GenericComboBoxModel<T> extends GenericListModel<T> implements ComboBoxModel{
	private T selectedItem;
	
	public GenericComboBoxModel(T... initial) {
		super(initial);
	}
	
	@Override
	public void setSelectedItem(Object paramObject) {
		selectedItem=(T)paramObject;
	}

	@Override
	public T getSelectedItem() {
		return selectedItem;
	}

}

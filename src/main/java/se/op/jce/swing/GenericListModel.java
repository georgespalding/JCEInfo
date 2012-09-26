package se.op.jce.swing;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;

public class GenericListModel<T> extends AbstractListModel implements List<T>{
	private List<T> items;
	
	public GenericListModel(boolean synchronize){
		List<T> tobe=new LinkedList<T>();
		if(synchronize)
			tobe=Collections.synchronizedList(tobe);
		items=tobe;
	}
	public GenericListModel(){
		this(false);
	}
	
	public GenericListModel(T... initial) {
		this();
		for (T each: initial) {
			add(each);
		}
	}
	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public T getElementAt(int i) {
		return items.get(i);
	}

	public List<T> items(){
		return Collections.unmodifiableList(items);
	}
	
	public boolean add(T obj){
		boolean ret=items.add(obj);
		fireIntervalAdded(this, items.size()-1, items.size()-1);
		return ret;
	}

	public T remove(int i){
		T ret=items.remove(i);
		fireIntervalRemoved(this, i,i);
		return ret;
	}

	public boolean remove(Object elm){
		int pos=items.indexOf(elm);
		boolean ret=items.remove(elm);
		if(ret){
			fireIntervalRemoved(this, pos, pos);
			System.err.println("REmoved from pos:"+pos);
		}
			return ret;
	}
	@Override
	public void add(int index, T element) {
		items.add(index,element);
		fireIntervalAdded(this, index,index);
	}
	@Override
	public boolean addAll(Collection<? extends T> c) {
		int index0=items.size();
		boolean ret=items.addAll(c);
		int index1=items.size();
		fireIntervalAdded(this, index0,index1);
		return ret;
	}
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean ret=items.addAll(c);
		int index1=index+c.size();
		fireIntervalAdded(this, index,index1);
		return ret;
	}
	@Override
	public boolean contains(Object obj) {
		return items.contains(obj);
	}
	@Override
	public void clear() {
		int siz=items.size();
		items.clear();
		fireIntervalRemoved(this, 0, siz);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return items.containsAll(c);
	}
	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}
	@Override
	public Iterator<T> iterator() {
//FIXME caller may remove stuff from items using iterator
		return items.iterator();
	}
	@Override
	public T get(int i) {
		return items.get(i);
	}
	@Override
	public int indexOf(Object obj) {
		return items.indexOf(obj);
	}
	@Override
	public int lastIndexOf(Object obj) {
		return items.lastIndexOf(obj);
	}
	@Override
	public ListIterator<T> listIterator() {
		//FIXME caller may remove stuff from items using iterator
		return items.listIterator();
	}
	@Override
	public ListIterator<T> listIterator(int i) {
		//FIXME caller may remove stuff from items using iterator
		return items.listIterator(i);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		int siz=items.size();
		boolean ret=items.removeAll(c);
		if(ret){
			fireContentsChanged(this,0, siz);
		}
		return ret;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		int siz=items.size();
		boolean ret=items.retainAll(c);
		if(ret){
			fireContentsChanged(this,0, siz);
		}
		return ret;
	}
	@Override
	public T set(int index, T element) {
		T ret=items.set(index, element);
		fireContentsChanged(this, index, index);
		return ret;
	}
	@Override
	public int size() {
		return items.size();
	}
	@Override
	public Object[] toArray() {
		return items.toArray();
	}
	@Override
	public List<T> subList(int i, int j) {
		return items.subList(i, j);
	}
	@Override
	public <E> E[] toArray(E[] a) {
		return items.toArray(a);
	}
	public void addAll(T[] tarr) {
		int index0=items.size();
		for (T t : tarr) {
			items.add(t);
		}
		fireIntervalAdded(this,index0,items.size());
	}
	public boolean removeAll(int[] indices) {
		int low=items.size();
		int high=-1;
		boolean ret=false;
		for (int i = 0; i < indices.length; i++) {
			if(i>=items.size())
				continue;
			if(i<low)
				low=i;
			if(i>high)
				high=i;
			items.remove(i);
			ret=true;
		}
		if(ret){
			fireContentsChanged(this,low, high);
		}
		return ret;
	}
}

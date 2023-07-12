package com.s8.stack.servers.xenon.flow;

public class XeFlowChain<T> {

	private Node head;
	
	private Node tail;
	
	private Node active;
	
	
	
	/**
	 * Retrieve but not removed the head
	 * @return
	 */
	public T getHead(){
		if(head != null) { 
			active = head; 
			return head.value;
		}
		else {
			return null;
		}
	}
	
	
	public T getTail(){
		if(tail != null) { 
			active = tail; 
			return tail.value;
		}
		else {
			return null;
		}
	}
	
	
	public void popHead(){
		head.remove();
		active = head;
	}
	
	
	/**
	 * 
	 */
	public void insertAfterActive(T value) {
		Node insertedNode = new Node(value);
		if(active != null) {
			active.insertAfter(insertedNode);
			active = insertedNode;
		}
		else {
			head = insertedNode;
			tail = insertedNode;
			active = insertedNode;
		}
	}
	
	
	public boolean isEmpty() {
		return head == null;
	}
	
	
	
	class Node {
		
		private Node next;
		
		private Node previous;
		
		public final T value;
		
		public Node(T value) {
			super();
			this.value = value;
		}
		
		public void insertAfter(Node node) {
			
			/* handles tail case */
			if(next == null) { tail = node; }
			
			/* Holds true even if tail (next == null) */
			node.next = next;
			
			/* handles case of tail (next == null) */
			if(next!=null) { next.previous = node; }
			
			/**  */
			node.previous = this;
			
			/** */
			next = node;
		}
		
		
		public void remove() {
			
			/* head case */
			if(previous == null) { 
				/* holds true even if no more element afterwards */
				head = next; 
			}
			/* previous != null : handles the case where this != head; */
			else {
				previous.next = next;
			}
			
			/* tail case */
			if(next == null) { 
				/* holds true even if no more element afterwards */
				tail = previous; 
			}
			/* next != null : handles the case where this != tail; */
			else {
				next.previous = previous;
			}
		}
	}
	

	
	
}

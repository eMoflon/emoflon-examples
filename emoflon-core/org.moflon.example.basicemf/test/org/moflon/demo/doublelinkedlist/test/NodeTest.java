package org.moflon.demo.doublelinkedlist.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.moflon.demo.doublelinkedlist.DoublelinkedlistFactory;
import org.moflon.demo.doublelinkedlist.List;
import org.moflon.demo.doublelinkedlist.Node;

public class NodeTest
{
   private static DoublelinkedlistFactory factory = DoublelinkedlistFactory.eINSTANCE;

   @Test
   public void testInsertNodeAfter(){
      List list = createCyclicList(new String[]{"1", "5"});
      
      // Get handles on nodes
      Node one = list.getElements().get(0);
      assertEquals("1", one.getLabel());
      
      // Check creation
      assertEquals("15", listToString(one));
      
      // Check adding nodes to the list
      Node two = createNodeInList("2", list);
      one.insertNodeAfter(two);
      assertEquals("125", listToString(one));
      
      Node three = createNodeInList("3", list);
      two.insertNodeAfter(three);
      assertEquals("1235", listToString(one));
      
      Node four = createNodeInList("4", list);
      three.insertNodeAfter(four);
      assertEquals("12345", listToString(one));
      
      Node six = createNodeInList("6", list);
      four.getNext().insertNodeAfter(six);
      assertEquals("123456", listToString(one));
   }
   
   @Test
   public void testInsertNodeBefore(){
      List list = createCyclicList(new String[]{"Monday", "Sunday"});
      
      // Get handles on nodes
      Node sunday = list.getElements().get(1);
      assertEquals("Sunday", sunday.getLabel());
      
      // Check creation
      assertEquals("SundayMonday", listToString(sunday));
      
      // Check adding nodes to list
      Node tuesday = createNodeInList("Tuesday", list);
      sunday.insertNodeBefore(tuesday);
      assertEquals("TuesdaySundayMonday", listToString(tuesday));
      
      Node thursday = createNodeInList("Thursday", list);
      sunday.insertNodeBefore(thursday);
      assertEquals("TuesdayThursdaySundayMonday", listToString(tuesday));
      
      Node wednesday = createNodeInList("Wednesday", list);
      thursday.insertNodeBefore(wednesday);
      assertEquals("SundayMondayTuesdayWednesdayThursday", listToString(sunday));
   }
   
   @Test
   public void testDeleteNode(){
      List list = createCyclicList(new String[] {"0", "1", "2", "9", "3", "5"});
      
      // Get handle
      Node one = list.getElements().get(1);
      assertEquals("1", one.getLabel());
      
      // Test creation
      assertEquals("129350", listToString(one));
      
      // Test deletion
      one.deleteNode();
      assertEquals("19350", listToString(one));
      
      one.getNext().getNext().deleteNode();
      assertEquals("1930", listToString(one));
      
      one.deleteNode();
      assertEquals("130", listToString(one));
   }
   
   public static Node createNodeInList(String label, List list)
   {
      Node node = factory.createNode();
      node.setLabel(label);
      list.getElements().add(node);
      
      return node;
   }

   private List createCyclicList(String[] labels){
      List list = factory.createList();
      
      // Add isolated nodes
      for (String label : labels)
      {
         Node node = factory.createNode();
         node.setLabel(label);
         list.getElements().add(node);
      }
      
      // Create links
      Node previous = null;
      for (Node node : list.getElements())
      {
         if(previous != null)
            previous.setNext(node);
         
         previous = node;
      }
      
      // Make cyclic
      list.getElements().get(0).setPrevious(list.getElements().get(list.getElements().size() - 1));
      
      return list;
   }
   
   public static String listToString(Node node){
      String concatenatedLabels = "";
      
      for(int i = 0; i < node.getContainer().getElements().size(); i++){
         concatenatedLabels += node.getLabel();
         node = node.getNext();
      }
      
      return concatenatedLabels;
   }
}

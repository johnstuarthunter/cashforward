/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward;

import org.cashforward.Label;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bill
 */
public class LabelTest {

    public LabelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getId method, of class Label.
     */
    @Test
    public void getId() {
        System.out.println("getId");
        Label instance = new Label();
        long expResult = 0L;
        long result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setId method, of class Label.
     */
    @Test
    public void setId() {
        System.out.println("setId");
        long id = 0L;
        Label instance = new Label();
        instance.setId(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class Label.
     */
    @Test
    public void getName() {
        System.out.println("getName");
        Label instance = new Label();
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setName method, of class Label.
     */
    @Test
    public void setName() {
        System.out.println("setName");
        String name = "";
        Label instance = new Label();
        instance.setName(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Label.
     */
    @Test
    public void equals() {
        System.out.println("equals");
        Object object = null;
        Label instance = new Label();
        boolean expResult = false;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
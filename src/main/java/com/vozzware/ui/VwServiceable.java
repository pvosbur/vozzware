/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwServicable.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

/**
 * This interface defines the methods that all VwJXxx components implement to support
 * the ability to automatically load themselves from httpServices as a result of dynamic data
 * events. Any VwServiceable component can register itself as a listener for data
 * change events from other VwServiceable components and change their data content
 * accordingly. For example: You have a form that has a combobox that displays a list
 * of all companies that you can place an order with. When you select a company from the
 * company combobox, you want to display the available products that you can order
 * in the products combo box. When you select a product from the products combobox the order
 * form's detail fields are then loaded. Using the bean property sheets, the company combobox
 * can be told to execute a service that returns the company data at form initialization time
 * using the loadOnInitService property. The products combobox can be instructed to listen for
 * a selection (DataChangeEvent) on the company combobox. Using the primaryDataChangeService,
 * you define the service to be run anytime a different selection is made in the company
 * combobox. The product's combobox automaticially fills itself with the product
 * list for company selected in the company combobox. Likewise the VwSmartPanel listens
 * for data change events on the product's combobox and executes a service to load the form
 * with product detail data. Using the VwJXxx components, the above mentioned scenario can
 * be accomplished without writing a single line of code. All runtime behaviour is defined
 * visually using the property sheets.
 *
 * @version 2.5
 */
public interface VwServiceable
{

  /**
   * Sets the symbolic name used by httpServices or other components to get and set data
   * values. I.E if this component is a VwTextField that represents a person's
   * first name and you have a data object with a key of FirstName then the component
   * name should be set to "FirstName" to enable automatic interaction with the
   * VwDataObject.
   *
   * @param strName The user assigned symbolic name for this component.
   *
   * @exception Exception if this name is already used by another component on the
   * same smart panel.
   */
  public void setComponentName( String strName ) throws Exception;


  /**
   * Gets the symbolic name for this component.
   *
   * @return The symbolic name for this component.
   *
   */
  public String getComponentName();



  /**
   * Sets the name of the properties file defining the list of initialization
   * actions to take.
   *
   * @param strPopertiesFile The name of the properties file that holds the init actions
   */
  //public void setInitActions( boolean hasActions );


  /**
   * Gets the name name of the properties file defining the initialization actions
   *
   * @return - A string with the name of the properties file
   * defining the initialization actions
   */
  //public boolean getInitActions();


  /**
   * Sets the set of parameter values corrresponding to the parameter names
   *
   * @param strParamValues - A comma delimited string of parameter values
   */
  public void setLoadOnInitParamValues( String strParamValues );


  /**
   * Gets the set of parameter values corrresponding to the parameter names
   *
   * @return - A comma delimited string of parameter values
   */
  public String getLoadOnInitParamValues();


  /**
   * Sets an array of VwEdit classes used to format data items displayed in a compomnent.
   * Each data element will have its own VwEdit class that contains the format mask of the
   * data to be displayed. This property is most usefull in multi-column list and combo boxes.
   *
   * @param astrFormatMasks  An array of String format masks as defined by the com.vozzware.util.VwEdit
   * class. Each entry in the array represents
   */
  //public  void setDisplayFormats( String[] astrFormats );


  /**
   * Gets the display format array
   *
   * @return - An array of VwEdit classes that edit/format a data item in the component
   */
  //public VwEdit[] getDisplayFormats();


  /**
   * Sets the name of the properties file defining the list of actions to take.
   *
   * @param strPopertiesFile The name of the properties file that holds the action list
   */
  public void setDataChangeActions( boolean hasActions );


  /**
   * Gets the name name of the properties file defining the initialization actions
   *
   * @return - A string with the name of the properties file
   * defining the initialization actions
   */
  public boolean getDataChangeActions();


  /**
   * Initialize the component by executing the defined actions
   *
   */
  public void init();


  /**
   * Registers an array of component names that are interested the VwDataChanged
   * events for this component.
   *
   * @param astrCompNames an array of VwServiceable component names to
   * register as data change  event listeners
   */
  public void setDataChangedListeners( String[] astrCompNames );


  /**
   * Returns a String array of VwServiceable component names that are registered dataChange
   * event listeners of this control
   *
   * @return An array of registered VwServiceable component names or null if nothing was registered.
  */
  public String[] getDataChangedListeners();



  /**
   * Registers the VwServiceable control as listener of data change events for this control
   *
   * @param serviceable The VwServiceable control to receive the VwDataChanged event
   * any time data changes in the context of this control. This could be a selection change
   * in a list or combo box, a radio or checkbox state change, TextField data change ectc ...
   */
  public void addDataChangedListener( VwServiceable serviceable );


  /**
   * Removes the VwServiceable control as a dataChanged event listener.
   *
   * @param serviceSupport The VwServiceable control to remove from the listener list
  */
  public void removeDataChangedListener( VwServiceable serviceable );


  /**
   * This method is called when data has changed in an VwServiceable implemented control.
   * Any listeners registered with that control get this event. The object that processes
   * this event will be responsible for updating its content for its context.
   *
   * @param dataChangedEvent The VwDataChangedEvent containing the VwDataObject(s)
   * that represent the changed data.
   */
  public void dataChanged( VwDataChangedEvent dataChangedEvent );

} // end interface VwServiceable {}


// *** End of VwServiceable.java ***


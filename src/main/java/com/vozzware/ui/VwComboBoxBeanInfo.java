/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwComboBoxBeanInfo.java

Create Date: Apr 11, 2003
============================================================================================
*/
package  com.vozzware.ui;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
 // end class VwComboBoxBeanInfo


// *** End of VwComboBoxBeanInfo.java ***

public class VwComboBoxBeanInfo extends SimpleBeanInfo
{
  Class beanClass = VwComboBox.class;
  String iconColor16x16Filename = "itccombobox_color16.gif";
  String iconColor32x32Filename = "itccombobox.gif";
  String iconMono16x16Filename;
  String iconMono32x32Filename;

  public VwComboBoxBeanInfo()
  {
  }

  public PropertyDescriptor[] getPropertyDescriptors()
  {
    try
    {
      PropertyDescriptor _clientServiceMgr = new PropertyDescriptor("clientServiceMgr", beanClass, "getClientServiceMgr", "setClientServiceMgr");
      PropertyDescriptor _componentName = new PropertyDescriptor("componentName", beanClass, "getComponentName", "setComponentName");
      PropertyDescriptor _dataChangeActions = new PropertyDescriptor("dataChangeActions", beanClass, "getDataChangeActions", "setDataChangeActions");
      PropertyDescriptor _dataChangedListeners = new PropertyDescriptor("dataChangedListeners", beanClass, "getDataChangedListeners", "setDataChangedListeners");
      PropertyDescriptor _firstRowData = new PropertyDescriptor("firstRowData", beanClass, "getFirstRowData", null);
      PropertyDescriptor _initActions = new PropertyDescriptor("initActions", beanClass, "getInitActions", "setInitActions");
      PropertyDescriptor _loadOnInitParamValues = new PropertyDescriptor("loadOnInitParamValues", beanClass, "getLoadOnInitParamValues", "setLoadOnInitParamValues");
      PropertyDescriptor _nextRowData = new PropertyDescriptor("nextRowData", beanClass, "getNextRowData", null);
      PropertyDescriptor _saveDataObjectOnLoad = new PropertyDescriptor("saveDataObjectOnLoad", beanClass, null, "setSaveDataObjectOnLoad");
      PropertyDescriptor _selectedIndex = new PropertyDescriptor("selectedIndex", beanClass, "getSelectedIndex", null);

      _dataChangedListeners.setPropertyEditorClass( VwDataChangeListenersPropertyEditor.class );
      _componentName.setPropertyEditorClass( VwCompNamePropertyEditor.class );

      PropertyDescriptor[] pds = new PropertyDescriptor[] {
	      _clientServiceMgr,
	      _componentName,
	      _dataChangeActions,
	      _dataChangedListeners,
	      _firstRowData,
	      _initActions,
	      _loadOnInitParamValues,
	      _nextRowData,
	      _saveDataObjectOnLoad,
	      _selectedIndex,};
      return pds;










    }
    catch(IntrospectionException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

  public java.awt.Image getIcon(int iconKind)
  {
    switch (iconKind) {
	    case BeanInfo.ICON_COLOR_16x16:
	      return iconColor16x16Filename != null ? loadImage(iconColor16x16Filename) : null;
	    case BeanInfo.ICON_COLOR_32x32:
	      return iconColor32x32Filename != null ? loadImage(iconColor32x32Filename) : null;
	    case BeanInfo.ICON_MONO_16x16:
	      return iconMono16x16Filename != null ? loadImage(iconMono16x16Filename) : null;
	    case BeanInfo.ICON_MONO_32x32:
	      return iconMono32x32Filename != null ? loadImage(iconMono32x32Filename) : null;
	  }
    return null;
  }

  public BeanInfo[] getAdditionalBeanInfo()
  {
    Class superclass = beanClass.getSuperclass();
    try
    {
      BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
      return new BeanInfo[] { superBeanInfo };
    }
    catch(IntrospectionException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
} // end class VwComboBoxBeanInfo{}

// *** end of VwComboBoxBeanInfo.java ***



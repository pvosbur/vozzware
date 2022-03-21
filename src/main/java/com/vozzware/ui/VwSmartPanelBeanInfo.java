/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwSmartPanelBeanInfo.java

Create Date: Apr 11, 2003
============================================================================================
*/

package com.vozzware.ui;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class VwSmartPanelBeanInfo extends SimpleBeanInfo
{
  Class beanClass = VwSmartPanel.class;
  String iconColor16x16Filename = "itcpanel_color_16.gif";
  String iconColor32x32Filename = "itcpanel.gif";
  String iconMono16x16Filename;
  String iconMono32x32Filename;

  public VwSmartPanelBeanInfo()
  {
  }

  public PropertyDescriptor[] getPropertyDescriptors()
  {
    try
    {
      PropertyDescriptor _buttonState = new PropertyDescriptor("buttonState", beanClass, null, "setButtonState");
      PropertyDescriptor _propertyFile = new PropertyDescriptor("propertyFile", beanClass, "getPropertyFile", "setPropertyFile");
      PropertyDescriptor _clientServiceMgr = new PropertyDescriptor("clientServiceMgr", beanClass, "getClientServiceMgr", "setClientServiceMgr");
      PropertyDescriptor _componentName = new PropertyDescriptor("componentName", beanClass, "getComponentName", "setComponentName");
      PropertyDescriptor _dataChangeActions = new PropertyDescriptor("dataChangeActions", beanClass, "getDataChangeActions", "setDataChangeActions");
      PropertyDescriptor _dataChangedListeners = new PropertyDescriptor("dataChangedListeners", beanClass, "getDataChangedListeners", "setDataChangedListeners");
      PropertyDescriptor _dataObjBtnCheckText = new PropertyDescriptor("dataObjBtnCheckText", beanClass, "getDataObjBtnCheckText", "setDataObjBtnCheckText");
      PropertyDescriptor _dataObjBtnUnCheckText = new PropertyDescriptor("dataObjBtnUnCheckText", beanClass, "getDataObjBtnUnCheckText", "setDataObjBtnUnCheckText");
      PropertyDescriptor _dataObject = new PropertyDescriptor("dataObject", beanClass, null, "setDataObject");
      //PropertyDescriptor _displayFormats = new PropertyDescriptor("displayFormats", beanClass, "getDisplayFormats", "setDisplayFormats");
      PropertyDescriptor _initActions = new PropertyDescriptor("initActions", beanClass, "getInitActions", "setInitActions");
      PropertyDescriptor _loadOnInitParamValues = new PropertyDescriptor("loadOnInitParamValues", beanClass, "getLoadOnInitParamValues", "setLoadOnInitParamValues");
      PropertyDescriptor _validateOnLostFocus = new PropertyDescriptor("validateOnLostFocus", beanClass, "getValidateOnLostFocus", "setValidateOnLostFocus");

      // *** Setup property editors
      _componentName.setPropertyEditorClass( VwCompNamePropertyEditor.class );
      _propertyFile.setPropertyEditorClass( VwComponentPropPropertyEditor.class );
      _dataChangedListeners.setPropertyEditorClass( VwDataChangeListenersPropertyEditor.class );

      PropertyDescriptor[] pds = new PropertyDescriptor[] {
	      _buttonState,
	      _propertyFile,
	      _clientServiceMgr,
	      _componentName,
	      _dataChangeActions,
	      _dataChangedListeners,
	      _dataObjBtnCheckText,
	      _dataObjBtnUnCheckText,
	      _dataObject,
	      //_displayFormats,
	      _initActions,
	      _loadOnInitParamValues,
	      _validateOnLostFocus,};
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
} // end class VwSmartPanelBeanInfo{}

// end of VwSmartPanelBeanInfo.java ***






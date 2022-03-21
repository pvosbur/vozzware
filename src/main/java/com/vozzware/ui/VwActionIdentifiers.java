package com.vozzware.ui;

import java.util.ResourceBundle;

/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwActionIdentifiers.java

Create Date: Apr 11, 2005
============================================================================================
*/
/**
 * This class defines the static menu and toolbar constants used to register actions with the menu
 * and toolbar managers
 */
public class VwActionIdentifiers
{
  public static final String FILE;
  public static final String EDIT;
  public static final String SEARCH;
  public static final String WINDOW;
  public static final String RUN;
  public static final String HELP;
  public static final String TOOLS;
  public static final String FILE_EXIT;
  public static final String FILE_NEW_SESSION;
  public static final String FILE_CLOSE_SESSION;
  public static final String FILE_SAVE_SERVICE;
  public static final String FILE_SAVE_SERVICE_AS;

  public static final String EDIT_UNDO;
  public static final String EDIT_REDO;
  public static final String EDIT_CUT;
  public static final String EDIT_COPY;
  public static final String EDIT_PASTE;
  public static final String EDIT_SELECTALL;
  public static final String EDIT_DELETE;

  public static final String SEARCH_FIND;
  public static final String SEARCH_FIND_NEXT;
  public static final String SEARCH_FIND_PREV;
  public static final String SEARCH_REPLACE;

  public static final String TOOLBAR_FILE = "FILE";
  public static final String TOOLBAR_EDIT = "EDIT";
  public static final String TOOLBAR_SEARCH = "SEARCH";
  public static final String TOOLBAR_RUN = "RUN";
  public static final String TOOLBAR_TOOLS = "TOOLS";
  public static final String TOOLBAR_HELP = "HELP";


  static
  {
    ResourceBundle rbUi = ResourceBundle.getBundle( "com.vozzware.ui.uimsgs");

    FILE  = rbUi.getString( "VwAdmin.Menu.File" );
    TOOLS  = rbUi.getString( "VwAdmin.Menu.Tools" );
    WINDOW  = rbUi.getString( "VwAdmin.Menu.Window" );
    RUN  = rbUi.getString( "VwAdmin.Menu.Run" );
    HELP  = rbUi.getString( "VwAdmin.Menu.Help" );
    FILE_EXIT  = rbUi.getString( "VwAdmin.Menu.File.Exit" );
    FILE_NEW_SESSION = rbUi.getString( "VwAdmin.Menu.File.NewSession" );
    FILE_CLOSE_SESSION = rbUi.getString( "VwAdmin.Menu.File.CloseSession" );
    FILE_SAVE_SERVICE = rbUi.getString( "VwAdmin.Menu.File.SaveService" );
    FILE_SAVE_SERVICE_AS = rbUi.getString( "VwAdmin.Menu.File.SaveServiceAs" );

    EDIT  = rbUi.getString( "VwUi.Menu.Edit.Id" );
    SEARCH  = rbUi.getString( "VwUi.Menu.Search.Id" );
    EDIT_UNDO = rbUi.getString( "VwUi.Menu.Edit.Undo.Id" );
    EDIT_REDO = rbUi.getString( "VwUi.Menu.Edit.Redo.Id" );
    EDIT_CUT = rbUi.getString( "VwUi.Menu.Edit.Cut.Id" );
    EDIT_COPY = rbUi.getString( "VwUi.Menu.Edit.Copy.Id" );
    EDIT_PASTE = rbUi.getString( "VwUi.Menu.Edit.Paste.Id" );
    EDIT_SELECTALL = rbUi.getString( "VwUi.Menu.Edit.SelectAll.Id" );
    EDIT_DELETE = rbUi.getString( "VwUi.Menu.Edit.Delete.Id" );

    SEARCH_FIND = rbUi.getString( "VwUi.Menu.Search.Find.Id" );
    SEARCH_FIND_NEXT = rbUi.getString( "VwUi.Menu.Search.FindNext.Id" );
    SEARCH_FIND_PREV = rbUi.getString( "VwUi.Menu.Search.FindPrev.Id" );
    SEARCH_REPLACE = rbUi.getString( "VwUi.Menu.Search.Replace.Id" );

  } 

} // end class VwActionIdentifiers{}

// *** VwActionIdentifiers.java ***
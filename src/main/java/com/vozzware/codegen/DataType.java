package com.vozzware.codegen;

public enum DataType
{
  BYTE( "byte", "b", "B"),
  BYTE_OBJ("Byte", "ob", "OB" ),
  BOOLEAN("boolean", "f", "Z"),
  BOOLEAN_OBJ( "Boolean", "of", "OZ"),
  CHAR( "char", "ch", "C" ),
  CHAR_OBJ( "Character", "och", "OC"),
  SHORT( "short", "s", "S" ),
  SHORT_OBJ( "Short", "os", "OS"),
  INT( "int", "n", "I" ),
  INT_OBJ( "Integer", "on", "OI" ),
  LONG( "long", "l", "J"),
  LONG_OBJ( "Long", "ol", "OJ"),
  FLOAT( "float", "flt", "F" ),
  FLOAT_OBJ( "Float", "oflt", "OF" ),
  DOUBLE( "double", "dbl", "D" ),
  DOUBLE_OBJ( "Double", "odbl", "OD" ),
  VOID( "void", null, "V" ),
  STRING( "String", "str", "String" ),
  LIST( "List", "list", "List" ),
  GT_LIST( "List", "list", "GLIST" ),
  LINKED_LIST( "LinkedList", "list", "LList" ),
  ARRAY_LIST( "ArrayList", "list", "AList" ),
  MAP( "Map", "map", "Map" ),
  GT_MAP( "Map", "map", "GMap"),
  HASH_MAP( "HashMap", "map", "HMap" ),
  TREE_MAP( "TreeMap", "map", "TMap" ),
  TIMESTAMP( "Timestamp", "ts", "JTimestamp"),
  DATE( "Date", "dt", "JDate"),
  VW_DATE( "VwDate", "dt", "IDate"),
  OBJECT( "Object", "obj", "OBJ"),
  USERDEF( "UserDef", null, null );
  

  private String m_strText;
  private String m_strMangledName;
  private String m_strHungarianName;

  private DataType( String strText, String strHungarianName, String strMangeldName )
  { 
    m_strText = strText;
    m_strMangledName = strMangeldName;
    m_strHungarianName = strHungarianName;
    
  }
  
  public String javaType()
  { return m_strText; }

  public String mangledName()
  { return m_strMangledName; }

  public String hungarianName()
  { return m_strHungarianName; }
  
} // end enum DataTypeP{}

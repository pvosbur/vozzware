/*
============================================================================================
 
                                Copyright(c) 2000 - 2006 by

                       V o z z W a r e   L L C (Vw)

                                   All Rights Reserved

THIS PROGRAM IS PROVIDED UNDER THE TERMS OF THE Vozzware LLC PUBLIC LICENSE VER 1.0 (�AGREEMENT�),
PROVIDED WITH THIS PROGRAM. ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
CONSTITUTES RECEIPIENTS ACCEPTANCE OF THIS AGREEMENT.

Source Name: VwLocale.java

============================================================================================
*/

package com.vozzware.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VwLocale
{
  public static Map s_mapCountryCodes = Collections.synchronizedMap( new HashMap() );
  public static Map s_mapLangCodes = Collections.synchronizedMap( new HashMap() );
  
  static
  {
    loadLangCodes();
    loadCountryCodes();
  }
  
  /**
   * Gets the locale for the language associated with the country code
   * @param strCountryCode
   * @return
   */
  public static Locale getLocale( String strLang )
  { 
    int nPos = strLang.indexOf( '_' );
    String strCountryCode = null;
    if ( nPos > 0 )
    {
      strCountryCode = strLang.substring( nPos + 1 ).toUpperCase();
      strLang = strLang.substring( 0, nPos );
    }
    
    Locale locale = null;
    if ( strCountryCode != null )
      locale = new Locale( strLang, strCountryCode );
    else
      locale = new Locale( strLang );
      
    return locale; 
    
  }
  
  public static Locale getLocaleForCountryCd( String strCountryCd )
  { return new Locale( (String)s_mapLangCodes.get( strCountryCd ) ); }
  
  private static void loadLangCodes()
  {
    s_mapLangCodes.put( "US", "en_US");
    s_mapLangCodes.put( "PL", "pl_PL");
    s_mapLangCodes.put( "FR", "fr_FR");
    s_mapLangCodes.put( "ES", "es_ES");
    
  }
  private static void loadCountryCodes()
  {
    s_mapCountryCodes.put ("AFGHANISTAN", "AF" );
    s_mapCountryCodes.put ("�LAND ISLANDS", "AX" );
    s_mapCountryCodes.put ("ALBANIA", "AL" );
    s_mapCountryCodes.put ("ALGERIA", "DZ" );
    s_mapCountryCodes.put ("AMERICAN SAMOA", "AS" );
    s_mapCountryCodes.put ("ANDORRA", "AD" );
    s_mapCountryCodes.put ("ANGOLA", "AO" );
    s_mapCountryCodes.put ("ANGUILLA", "AI" );
    s_mapCountryCodes.put ("ANTARCTICA", "AQ" );
    s_mapCountryCodes.put ("ANTIGUA AND BARBUDA", "AG" );
    s_mapCountryCodes.put ("ARGENTINA", "AR" );
    s_mapCountryCodes.put ("ARMENIA", "AM" );
    s_mapCountryCodes.put ("ARUBA", "AW" );
    s_mapCountryCodes.put ("AUSTRALIA", "AU" );
    s_mapCountryCodes.put ("AUSTRIA", "AT" );
    s_mapCountryCodes.put ("AZERBAIJAN", "AZ" );
    s_mapCountryCodes.put ("BAHAMAS", "BS" );
    s_mapCountryCodes.put ("BAHRAIN", "BH" );
    s_mapCountryCodes.put ("BANGLADESH", "BD" );
    s_mapCountryCodes.put ("BARBADOS", "BB" );
    s_mapCountryCodes.put ("BELARUS", "BY" );
    s_mapCountryCodes.put ("BELGIUM", "BE" );
    s_mapCountryCodes.put ("BELIZE", "BZ" );
    s_mapCountryCodes.put ("BENIN", "BJ" );
    s_mapCountryCodes.put ("BERMUDA", "BM" );
    s_mapCountryCodes.put ("BHUTAN", "BT" );
    s_mapCountryCodes.put ("BOLIVIA", "BO" );
    s_mapCountryCodes.put ("BOSNIA AND HERZEGOVINA", "BA" );
    s_mapCountryCodes.put ("BOTSWANA", "BW" );
    s_mapCountryCodes.put ("BOUVET ISLAND", "BV" );
    s_mapCountryCodes.put ("BRAZIL", "BR" );
    s_mapCountryCodes.put ("BRITISH INDIAN OCEAN TERRITORY", "IO" );
    s_mapCountryCodes.put ("BRUNEI DARUSSALAM", "BN" );
    s_mapCountryCodes.put ("BULGARIA", "BG" );
    s_mapCountryCodes.put ("BURKINA FASO", "BF" );
    s_mapCountryCodes.put ("BURUNDI", "BI" );
    s_mapCountryCodes.put ("CAMBODIA", "KH" );
    s_mapCountryCodes.put ("CAMEROON", "CM" );
    s_mapCountryCodes.put ("CANADA", "CA" );
    s_mapCountryCodes.put ("CAPE VERDE", "CV" );
    s_mapCountryCodes.put ("CAYMAN ISLANDS", "KY" );
    s_mapCountryCodes.put ("CENTRAL AFRICAN REPUBLIC", "CF" );
    s_mapCountryCodes.put ("CHAD", "TD" );
    s_mapCountryCodes.put ("CHILE", "CL" );
    s_mapCountryCodes.put ("CHINA", "CN" );
    s_mapCountryCodes.put ("CHRISTMAS ISLAND", "CX" );
    s_mapCountryCodes.put ("COCOS (KEELING) ISLANDS", "CC" );
    s_mapCountryCodes.put ("COLOMBIA", "CO" );
    s_mapCountryCodes.put ("COMOROS", "KM" );
    s_mapCountryCodes.put ("CONGO", "CG" );
    s_mapCountryCodes.put ("CONGO, THE DEMOCRATIC REPUBLIC OF THE", "CD" );
    s_mapCountryCodes.put ("COOK ISLANDS", "CK" );
    s_mapCountryCodes.put ("COSTA RICA", "CR" );
    s_mapCountryCodes.put ("COTE D'IVOIRE", "CI" );
    s_mapCountryCodes.put ("CROATIA", "HR" );
    s_mapCountryCodes.put ("CUBA", "CU" );
    s_mapCountryCodes.put ("CYPRUS", "CY" );
    s_mapCountryCodes.put ("CZECH REPUBLIC", "CZ" );
    s_mapCountryCodes.put ("DENMARK", "DK" );
    s_mapCountryCodes.put ("DJIBOUTI", "DJ" );
    s_mapCountryCodes.put ("DOMINICA", "DM" );
    s_mapCountryCodes.put ("DOMINICAN REPUBLIC", "DO" );
    s_mapCountryCodes.put ("ECUADOR", "EC" );
    s_mapCountryCodes.put ("EGYPT", "EG" );
    s_mapCountryCodes.put ("EL SALVADOR", "SV" );
    s_mapCountryCodes.put ("EQUATORIAL GUINEA", "GQ" );
    s_mapCountryCodes.put ("ERITREA", "ER" );
    s_mapCountryCodes.put ("ESTONIA", "EE" );
    s_mapCountryCodes.put ("ETHIOPIA", "ET" );
    s_mapCountryCodes.put ("FALKLAND ISLANDS (MALVINAS)", "FK" );
    s_mapCountryCodes.put ("FAROE ISLANDS", "FO" );
    s_mapCountryCodes.put ("FIJI", "FJ" );
    s_mapCountryCodes.put ("FINLAND", "FI" );
    s_mapCountryCodes.put ("FRANCE", "FR" );
    s_mapCountryCodes.put ("FRENCH GUIANA", "GF" );
    s_mapCountryCodes.put ("FRENCH POLYNESIA", "PF" );
    s_mapCountryCodes.put ("FRENCH SOUTHERN TERRITORIES", "TF" );
    s_mapCountryCodes.put ("GABON", "GA" );
    s_mapCountryCodes.put ("GAMBIA", "GM" );
    s_mapCountryCodes.put ("GEORGIA", "GE" );
    s_mapCountryCodes.put ("GERMANY", "DE" );
    s_mapCountryCodes.put ("GHANA", "GH" );
    s_mapCountryCodes.put ("GIBRALTAR", "GI" );
    s_mapCountryCodes.put ("GREECE", "GR" );
    s_mapCountryCodes.put ("GREENLAND", "GL" );
    s_mapCountryCodes.put ("GRENADA", "GD" );
    s_mapCountryCodes.put ("GUADELOUPE", "GP" );
    s_mapCountryCodes.put ("GUAM", "GU" );
    s_mapCountryCodes.put ("GUATEMALA", "GT" );
    s_mapCountryCodes.put ("GUINEA", "GN" );
    s_mapCountryCodes.put ("GUINEA-BISSAU", "GW" );
    s_mapCountryCodes.put ("GUYANA", "GY" );
    s_mapCountryCodes.put ("HAITI", "HT" );
    s_mapCountryCodes.put ("HEARD ISLAND AND MCDONALD ISLANDS", "HM" );
    s_mapCountryCodes.put ("HOLY SEE (VATICAN CITY STATE)", "VA" );
    s_mapCountryCodes.put ("HONDURAS", "HN" );
    s_mapCountryCodes.put ("HONG KONG", "HK" );
    s_mapCountryCodes.put ("HUNGARY", "HU" );
    s_mapCountryCodes.put ("ICELAND", "IS" );
    s_mapCountryCodes.put ("INDIA", "IN" );
    s_mapCountryCodes.put ("INDONESIA", "ID" );
    s_mapCountryCodes.put ("IRAN, ISLAMIC REPUBLIC OF", "IR" );
    s_mapCountryCodes.put ("IRAQ", "IQ" );
    s_mapCountryCodes.put ("IRELAND", "IE" );
    s_mapCountryCodes.put ("ISRAEL", "IL" );
    s_mapCountryCodes.put ("ITALY", "IT" );
    s_mapCountryCodes.put ("JAMAICA", "JM" );
    s_mapCountryCodes.put ("JAPAN", "JP" );
    s_mapCountryCodes.put ("JORDAN", "JO" );
    s_mapCountryCodes.put ("KAZAKHSTAN", "KZ" );
    s_mapCountryCodes.put ("KENYA", "KE" );
    s_mapCountryCodes.put ("KIRIBATI", "KI" );
    s_mapCountryCodes.put ("KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF", "KP" );
    s_mapCountryCodes.put ("KOREA, REPUBLIC OF", "KR" );
    s_mapCountryCodes.put ("KUWAIT", "KW" );
    s_mapCountryCodes.put ("KYRGYZSTAN", "KG" );
    s_mapCountryCodes.put ("LAO PEOPLE'S DEMOCRATIC REPUBLIC", "LA" );
    s_mapCountryCodes.put ("LATVIA", "LV" );
    s_mapCountryCodes.put ("LEBANON", "LB" );
    s_mapCountryCodes.put ("LESOTHO", "LS" );
    s_mapCountryCodes.put ("LIBERIA", "LR" );
    s_mapCountryCodes.put ("LIBYAN ARAB JAMAHIRIYA", "LY" );
    s_mapCountryCodes.put ("LIECHTENSTEIN", "LI" );
    s_mapCountryCodes.put ("LITHUANIA", "LT" );
    s_mapCountryCodes.put ("LUXEMBOURG", "LU" );
    s_mapCountryCodes.put ("MACAO", "MO" );
    s_mapCountryCodes.put ("MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF", "MK" );
    s_mapCountryCodes.put ("MADAGASCAR", "MG" );
    s_mapCountryCodes.put ("MALAWI", "MW" );
    s_mapCountryCodes.put ("MALAYSIA", "MY" );
    s_mapCountryCodes.put ("MALDIVES", "MV" );
    s_mapCountryCodes.put ("MALI", "ML" );
    s_mapCountryCodes.put ("MALTA", "MT" );
    s_mapCountryCodes.put ("MARSHALL ISLANDS", "MH" );
    s_mapCountryCodes.put ("MARTINIQUE", "MQ" );
    s_mapCountryCodes.put ("MAURITANIA", "MR" );
    s_mapCountryCodes.put ("MAURITIUS", "MU" );
    s_mapCountryCodes.put ("MAYOTTE", "YT" );
    s_mapCountryCodes.put ("MEXICO", "MX" );
    s_mapCountryCodes.put ("MICRONESIA, FEDERATED STATES OF", "FM" );
    s_mapCountryCodes.put ("MOLDOVA, REPUBLIC OF", "MD" );
    s_mapCountryCodes.put ("MONACO", "MC" );
    s_mapCountryCodes.put ("MONGOLIA", "MN" );
    s_mapCountryCodes.put ("MONTSERRAT", "MS" );
    s_mapCountryCodes.put ("MOROCCO", "MA" );
    s_mapCountryCodes.put ("MOZAMBIQUE", "MZ" );
    s_mapCountryCodes.put ("MYANMAR", "MM" );
    s_mapCountryCodes.put ("NAMIBIA", "NA" );
    s_mapCountryCodes.put ("NAURU", "NR" );
    s_mapCountryCodes.put ("NEPAL", "NP" );
    s_mapCountryCodes.put ("NETHERLANDS", "NL" );
    s_mapCountryCodes.put ("NETHERLANDS ANTILLES", "AN" );
    s_mapCountryCodes.put ("NEW CALEDONIA", "NC" );
    s_mapCountryCodes.put ("NEW ZEALAND", "NZ" );
    s_mapCountryCodes.put ("NICARAGUA", "NI" );
    s_mapCountryCodes.put ("NIGER", "NE" );
    s_mapCountryCodes.put ("NIGERIA", "NG" );
    s_mapCountryCodes.put ("NIUE", "NU" );
    s_mapCountryCodes.put ("NORFOLK ISLAND", "NF" );
    s_mapCountryCodes.put ("NORTHERN MARIANA ISLANDS", "MP" );
    s_mapCountryCodes.put ("NORWAY", "NO" );
    s_mapCountryCodes.put ("OMAN", "OM" );
    s_mapCountryCodes.put ("PAKISTAN", "PK" );
    s_mapCountryCodes.put ("PALAU", "PW" );
    s_mapCountryCodes.put ("PALESTINIAN TERRITORY, OCCUPIED", "PS" );
    s_mapCountryCodes.put ("PANAMA", "PA" );
    s_mapCountryCodes.put ("PAPUA NEW GUINEA", "PG" );
    s_mapCountryCodes.put ("PARAGUAY", "PY" );
    s_mapCountryCodes.put ("PERU", "PE" );
    s_mapCountryCodes.put ("PHILIPPINES", "PH" );
    s_mapCountryCodes.put ("PITCAIRN", "PN" );
    s_mapCountryCodes.put ("POLAND", "PL" );
    s_mapCountryCodes.put ("PORTUGAL", "PT" );
    s_mapCountryCodes.put ("PUERTO RICO", "PR" );
    s_mapCountryCodes.put ("QATAR", "QA" );
    s_mapCountryCodes.put ("REUNION", "RE" );
    s_mapCountryCodes.put ("ROMANIA", "RO" );
    s_mapCountryCodes.put ("RUSSIAN FEDERATION", "RU" );
    s_mapCountryCodes.put ("RWANDA", "RW" );
    s_mapCountryCodes.put ("SAINT HELENA", "SH" );
    s_mapCountryCodes.put ("SAINT KITTS AND NEVIS", "KN" );
    s_mapCountryCodes.put ("SAINT LUCIA", "LC" );
    s_mapCountryCodes.put ("SAINT PIERRE AND MIQUELON", "PM" );
    s_mapCountryCodes.put ("SAINT VINCENT AND THE GRENADINES", "VC" );
    s_mapCountryCodes.put ("SAMOA", "WS" );
    s_mapCountryCodes.put ("SAN MARINO", "SM" );
    s_mapCountryCodes.put ("SAO TOME AND PRINCIPE", "ST" );
    s_mapCountryCodes.put ("SAUDI ARABIA", "SA" );
    s_mapCountryCodes.put ("SENEGAL", "SN" );
    s_mapCountryCodes.put ("SERBIA AND MONTENEGRO", "CS" );
    s_mapCountryCodes.put ("SEYCHELLES", "SC" );
    s_mapCountryCodes.put ("SIERRA LEONE", "SL" );
    s_mapCountryCodes.put ("SINGAPORE", "SG" );
    s_mapCountryCodes.put ("SLOVAKIA", "SK" );
    s_mapCountryCodes.put ("SLOVENIA", "SI" );
    s_mapCountryCodes.put ("SOLOMON ISLANDS", "SB" );
    s_mapCountryCodes.put ("SOMALIA", "SO" );
    s_mapCountryCodes.put ("SOUTH AFRICA", "ZA" );
    s_mapCountryCodes.put ("SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS", "GS" );
    s_mapCountryCodes.put ("SPAIN", "ES" );
    s_mapCountryCodes.put ("SRI LANKA", "LK" );
    s_mapCountryCodes.put ("SUDAN", "SD" );
    s_mapCountryCodes.put ("SURINAME", "SR" );
    s_mapCountryCodes.put ("SVALBARD AND JAN MAYEN", "SJ" );
    s_mapCountryCodes.put ("SWAZILAND", "SZ" );
    s_mapCountryCodes.put ("SWEDEN", "SE" );
    s_mapCountryCodes.put ("SWITZERLAND", "CH" );
    s_mapCountryCodes.put ("SYRIAN ARAB REPUBLIC", "SY" );
    s_mapCountryCodes.put ("TAIWAN, PROVINCE OF CHINA", "TW" );
    s_mapCountryCodes.put ("TAJIKISTAN", "TJ" );
    s_mapCountryCodes.put ("TANZANIA, UNITED REPUBLIC OF", "TZ" );
    s_mapCountryCodes.put ("THAILAND", "TH" );
    s_mapCountryCodes.put ("TIMOR-LESTE", "TL" );
    s_mapCountryCodes.put ("TOGO", "TG" );
    s_mapCountryCodes.put ("TOKELAU", "TK" );
    s_mapCountryCodes.put ("TONGA", "TO" );
    s_mapCountryCodes.put ("TRINIDAD AND TOBAGO", "TT" );
    s_mapCountryCodes.put ("TUNISIA", "TN" );
    s_mapCountryCodes.put ("TURKEY", "TR" );
    s_mapCountryCodes.put ("TURKMENISTAN", "TM" );
    s_mapCountryCodes.put ("TURKS AND CAICOS ISLANDS", "TC" );
    s_mapCountryCodes.put ("TUVALU", "TV" );
    s_mapCountryCodes.put ("UGANDA", "UG" );
    s_mapCountryCodes.put ("UKRAINE", "UA" );
    s_mapCountryCodes.put ("UNITED ARAB EMIRATES", "AE" );
    s_mapCountryCodes.put ("UNITED KINGDOM", "GB" );
    s_mapCountryCodes.put ("UNITED STATES", "US" );
    s_mapCountryCodes.put ("UNITED STATES MINOR OUTLYING ISLANDS", "UM" );
    s_mapCountryCodes.put ("URUGUAY", "UY" );
    s_mapCountryCodes.put ("UZBEKISTAN", "UZ" );
    s_mapCountryCodes.put ("VANUATU", "VU" );
    s_mapCountryCodes.put ("VENEZUELA", "VE" );
    s_mapCountryCodes.put ("VIET NAM", "VN" );
    s_mapCountryCodes.put ("VIRGIN ISLANDS, BRITISH", "VG" );
    s_mapCountryCodes.put ("VIRGIN ISLANDS, U.S.", "VI" );
    s_mapCountryCodes.put ("WALLIS AND FUTUNA", "WF" );
    s_mapCountryCodes.put ("WESTERN SAHARA", "EH" );
    s_mapCountryCodes.put ("YEMEN", "YE" );
    s_mapCountryCodes.put ("ZAMBIA", "ZM" );
    s_mapCountryCodes.put ("ZIMBABWE", "ZW" );
  
  } // end loadCountryMap

} // end class VwLocale{}
// *** End of VwLocale.java ***


package com.vozzware.ui.beans;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.TreeMap;


public class VwFontChooserPanel extends JPanel

{

  private JList     m_jlFontNames;
  private JList     m_jlFontStyles;
  private JList     m_jlFontSizes;
  
  private JLabel    m_lblFont = new JLabel( "Font:");
  private JLabel    m_lblFontStyle = new JLabel( "Font Style:");
  private JLabel    m_lblFontSize = new JLabel( "Size:");

  private JLabel    m_lblFontName = new JLabel();
  private JLabel    m_lblFontStyleName = new JLabel();
  private JLabel    m_lblFontSizeName = new JLabel();

  private DefaultListModel m_modelFontNames = new DefaultListModel();
  private DefaultListModel m_modelFontStyles = new DefaultListModel();
  private DefaultListModel m_modelFontSizes = new DefaultListModel();
  
  
  private JLabel    m_lblPreview;

  private Font      m_fontChosen;
  
  /**
   * This class presents a panel that displays all available fonts for the underling os and allows
   * the user to chhose a font, style and size (with preview). If the font is accepted the api returns
   * a The Font intsnce chosen
   * @param parent
   * @param fontInitial
   */ 
  public VwFontChooserPanel( Font fontInitial  )

  {

    buildFontModels();
    
    initGUI( fontInitial );
     
   }

  private void initGUI( Font fontInitial )
  {
    
    m_lblFontName = new JLabel();
    m_lblFontStyleName = new JLabel();
    m_lblFontSizeName = new JLabel();
    
    Border borderLine = BorderFactory.createLoweredBevelBorder();
    
    m_lblFontName.setBackground( Color.white );
    m_lblFontStyleName.setBackground( Color.white );
    m_lblFontSizeName.setBackground( Color.white );
    
    m_lblFontName.setBorder( borderLine );
    m_lblFontStyleName.setBorder( borderLine );
    m_lblFontSizeName.setBorder( borderLine );
    
    m_lblFontName.setText( "Arial");
    
    FontMetrics fm = m_lblFontName.getFontMetrics( m_lblFontName.getFont() );
    int nHeight = fm.getHeight();
    Dimension dimPrefSize = new Dimension( 20 * fm.charWidth( 'W' ), nHeight );

    m_lblFontName.setPreferredSize( dimPrefSize );
    m_lblFontStyleName.setPreferredSize( dimPrefSize );
    m_lblFontSizeName.setPreferredSize( dimPrefSize );

    m_lblFontName.setMinimumSize( dimPrefSize );
    m_lblFontStyleName.setMinimumSize( dimPrefSize );
    m_lblFontSizeName.setMinimumSize( dimPrefSize );

    setBorder( new LineBorder( Color.black ) );

    setLayout( new GridBagLayout() );
    GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = gc.gridy = 0;
    gc.insets = new Insets( 5,5,5,5 );
    gc.anchor = GridBagConstraints.NORTHWEST;
    gc.fill = GridBagConstraints.NONE;
    
    gc.weighty = 0;
    this.add(  m_lblFont, gc );
    
    ++gc.gridx;
    this.add(  m_lblFontStyle, gc );

    ++gc.gridx;
    this.add(  m_lblFontSize, gc );
    
    gc.gridx = 0;
    gc.gridy = 1;

    gc.gridheight = 1;
    
    this.add(  m_lblFontName, gc );
    ++gc.gridx;   
    this.add(  m_lblFontStyleName, gc );

    ++gc.gridx;   
    this.add(  m_lblFontSizeName, gc );

    m_jlFontNames = new JList( m_modelFontNames );
    m_jlFontStyles = new JList( m_modelFontStyles );
    m_jlFontSizes = new JList( m_modelFontSizes );


    JScrollPane spFontNames = new JScrollPane( m_jlFontNames );
    JScrollPane spFontStyles = new JScrollPane( m_jlFontStyles );
    JScrollPane spFontSizes = new JScrollPane( m_jlFontSizes );

    Dimension dimListSize = new Dimension( dimPrefSize.width, 6 * dimPrefSize.height );
    spFontNames.setPreferredSize( dimListSize );
    spFontStyles.setPreferredSize( dimListSize );
    spFontSizes.setPreferredSize( dimListSize );

    spFontNames.setMinimumSize( dimListSize );
    spFontStyles.setMinimumSize( dimListSize );
    spFontSizes.setMinimumSize( dimListSize );

    gc.gridy = 2;
    gc.weightx = 1.0;
    gc.gridx = 0;
    this.add(  spFontNames, gc );

    ++gc.gridx;   
    this.add(  spFontStyles, gc );

    ++gc.gridx;   
    this.add(  spFontSizes, gc );
 
    JLabel lblPreviewTitle = new JLabel( "Preview", JLabel.CENTER);
 
    Font fontOrigLabel = lblPreviewTitle.getFont();
    Font fontNewPreview = new Font( fontOrigLabel.getName(), fontOrigLabel.getStyle(), 18 );
    lblPreviewTitle.setFont( fontNewPreview );
    
    ++gc.gridy;
    gc.anchor = GridBagConstraints.CENTER;
    gc.gridx = 0;
    gc.gridwidth = 3;
    this.add( lblPreviewTitle, gc );
    
    m_lblPreview = new JLabel( );
    m_lblPreview.setHorizontalAlignment( JLabel.CENTER );
    m_lblPreview.setBorder( BorderFactory.createRaisedBevelBorder()  );
    ++gc.gridy;
    gc.anchor = GridBagConstraints.WEST;
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridx = 0;
    gc.gridwidth = 3;
    gc.weightx = 1.0;
    this.add( m_lblPreview, gc );
    
    m_jlFontNames.setSelectedValue( fontInitial.getFamily(), true );
    m_jlFontStyles.setSelectedValue( convertStyleToText( fontInitial.getStyle() ), false );
    m_jlFontSizes.setSelectedValue( String.valueOf( fontInitial.getSize() ), true );
    
    updatePreview();    
    installListeners();
    
    // claculate the preferred size based on the inital font preview window and control widths and heightts
    this.setPreferredSize( new Dimension( (dimListSize.width * 3 )  + gc.insets.left * 10,
        (dimPrefSize.height * 4) + dimListSize.height + (gc.insets.top * 10 ) + m_lblPreview.getPreferredSize().height ) );
    
  }

  

  /**
   * Setup the three listbox select listeners
   *
   */
  private void installListeners()
  {
   m_jlFontNames.addListSelectionListener( new ListSelectionListener()
   {
     public void valueChanged( ListSelectionEvent lse )
     {
       if ( lse.getValueIsAdjusting() )
         return;
       
       updatePreview();
       
    }
   });

   m_jlFontStyles.addListSelectionListener( new ListSelectionListener()
   {
     public void valueChanged( ListSelectionEvent lse )
     {
       if ( lse.getValueIsAdjusting() )
         return;
       
       updatePreview();
       
    }
   });
   
   m_jlFontSizes.addListSelectionListener( new ListSelectionListener()
   {
     public void valueChanged( ListSelectionEvent lse )
     {
       if ( lse.getValueIsAdjusting() )
         return;
       
       updatePreview();
       
    }
   });
   
   
  }

  /**
   * Upfate the preview lable each time one of the listbox selection changes
   *
   */
  protected void updatePreview()
  {
    String strFontName = (String)m_jlFontNames.getSelectedValue();
    String strFontStyle = (String)m_jlFontStyles.getSelectedValue();
    String strFontSize = (String)m_jlFontSizes.getSelectedValue();
    
    m_lblFontName.setText( strFontName );
    m_lblFontStyleName.setText( strFontStyle );
    m_lblFontSizeName.setText( strFontSize );
    
    int nStyle = convertStyleTextToInt( strFontStyle );
    
       
    m_fontChosen = new Font( strFontName, nStyle, Integer.parseInt( strFontSize ) );
    m_lblPreview.setFont( m_fontChosen );
    m_lblPreview.setText( getPreviewText( m_fontChosen ) );  
    
  }

  
  /**
   * Converts a test repsestation of the font style to its style bits
   * @param strFontStyle The font style as a string
   * @return
   */
  public static int convertStyleTextToInt( String strFontStyle )
  {
    int nStyle = 0;
    
    if ( strFontStyle.equalsIgnoreCase( "bold italic" ))
      nStyle = Font.BOLD | Font.ITALIC;
    else
    if ( strFontStyle.equalsIgnoreCase( "bold" ))
      nStyle = Font.BOLD;
    else
    if ( strFontStyle.equalsIgnoreCase( "italic" ))
      nStyle = Font.ITALIC;
    else
      nStyle = Font.PLAIN;
    
    return nStyle;
    
  } // end convertStyleTextToInt( )

  /**
   * Return the Font object as chosen by the user
   * @return
   */
  public Font getChosenFont()
  { return m_fontChosen; }
  
  /**
   * Create the preview label text based on the font name, style and size
   * @param font The font that represents the user selections
   * @return
   */
  private String getPreviewText( Font font )
  {
    StringBuffer sbSampleText = new StringBuffer( font.getFamily() + " " + convertStyleToText( font.getStyle() ) + " " + font.getSize() );
    return sbSampleText.toString();
  }

  
  /**
   * Converts the Font style bits to text
   * @param nFontStyle The font style from the Font instance
   * @return
   */
  public static String convertStyleToText( int nFontStyle )
  {
    
    if ( (nFontStyle & Font.BOLD) == Font.BOLD && (nFontStyle & Font.ITALIC) == Font.ITALIC)
      return "Bold Italic";
    
    if ( (nFontStyle & Font.BOLD) == Font.BOLD )
      return "Bold";
    
    if ( (nFontStyle & Font.ITALIC) == Font.ITALIC)
      return "Italic";
    
    return "Plain";
    
  }

  /**
   * Build the three font listbox models -- fonta names, styles and sizes
   *
   */
  private void buildFontModels()
  {
    Font[] aFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    Map<String,String> mapFontNames = new TreeMap<String, String>();
    
    for ( int x = 0; x < aFonts.length; x++)
      mapFontNames.put( aFonts[ x ].getFamily(), null );

    for ( String strFontName : mapFontNames.keySet() )
      m_modelFontNames.addElement( strFontName );

    m_modelFontStyles.addElement( "Plain" );
    m_modelFontStyles.addElement( "Bold" );
    m_modelFontStyles.addElement( "Italic" );
    m_modelFontStyles.addElement( "Bold Italic" );
    
    m_modelFontSizes.addElement( "3" );
    m_modelFontSizes.addElement( "5" );
    m_modelFontSizes.addElement( "8" );
    m_modelFontSizes.addElement( "10" );
    m_modelFontSizes.addElement( "12" );
    m_modelFontSizes.addElement( "13" );
    m_modelFontSizes.addElement( "14" );
    m_modelFontSizes.addElement( "15" );
    m_modelFontSizes.addElement( "18" );
    m_modelFontSizes.addElement( "24" );
    m_modelFontSizes.addElement( "36" );
    m_modelFontSizes.addElement( "48" );
    m_modelFontSizes.addElement( "60" );
    m_modelFontSizes.addElement( "72" );
    
  } // end buildFontModels


  // For testing only 
  public static void main( String[] args )
  {
    try
    {
      
      VwFontChooserPanel panel = new VwFontChooserPanel( new Font( "Courier New", Font.BOLD | Font.ITALIC, 72 ) );
      JFrame frame = new JFrame("FontDemo");

      //2. Optional: What happens when the frame closes?
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      //3. Create components and put them in the frame.
      //...create emptyLabel...
      frame.getContentPane().add(panel, BorderLayout.CENTER);

      //4. Size the frame.
      frame.pack();

      //5. Show it.
      frame.setVisible(true);

      return;
      
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }
}

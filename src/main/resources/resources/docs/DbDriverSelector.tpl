<viewDefinition>
  <panel name="DbDriverSelector" componentDelegate="com.enginuity.ui.runtime.components.PanelDelegate" x="0" y="0" width="643" height="278" resizeHorizontal="true" title="${dbsriver.selector.title}">
    <panel name="JPanel_8" type="javax.swing.JPanel" componentDelegate="com.enginuity.ui.runtime.components.PanelDelegate" x="27" y="16" width="594" height="247" resizeHorizontal="true">
      <titledBorder title="dbdriver.selector.choose.title" justify="Center">
        <font name="Lucida Grande" style="Plain" size="13"/>
      </titledBorder>
      <label name="JLabel_9" type="javax.swing.JLabel" componentDelegate="com.enginuity.ui.runtime.components.LabelDelegate" x="46" y="47" width="150" height="16" resizeHorizontal="true" text="dbdriver.selector.dialog.avail.drivers" horizontalAlignment="center"/>
      <label name="JLabel_12" type="javax.swing.JLabel" componentDelegate="com.enginuity.ui.runtime.components.LabelDelegate" x="259" y="47" width="278" height="16" resizeHorizontal="true" text="dbselector.dialog.url.label" horizontalAlignment="center"/>
      <textField name="userId" type="javax.swing.JTextField" componentDelegate="com.enginuity.ui.runtime.components.TextFieldComponentDelegate" x="175" y="141" width="132" height="28" required="true"/>
      <textField name="password" type="javax.swing.JTextField" componentDelegate="com.enginuity.ui.runtime.components.TextFieldComponentDelegate" x="175" y="188" width="132" height="28" required="true" resizeHorizontal="true"/>
      <comboBox name="availDriverList" type="com.enginuity.ui.JComboBoxHelper" componentDelegate="com.enginuity.ui.runtime.components.ComboBoxComponentDelegate" x="36" y="72" width="172" height="24" required="true" resizeHorizontal="true"/>
      <label name="JLabel_13" type="javax.swing.JLabel" componentDelegate="com.enginuity.ui.runtime.components.LabelDelegate" x="41" y="145" width="79" height="16" text="dbSelector.dialog.uid.label"/>
      <label name="JLabel_15" type="javax.swing.JLabel" componentDelegate="com.enginuity.ui.runtime.components.LabelDelegate" x="41" y="188" width="100" height="25" text="dbselrctor.dialog.pwd.label"/>
      <comboBox name="availJdbcUrls" type="com.enginuity.ui.JComboBoxHelper" componentDelegate="com.enginuity.ui.runtime.components.ComboBoxComponentDelegate" x="251" y="70" width="295" height="24" required="true" resizeHorizontal="true" editable="true"/>
    </panel>
  </panel>
</viewDefinition>
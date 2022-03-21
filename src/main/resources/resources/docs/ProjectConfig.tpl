<viewDefinition>
  <panel name="VwProjectConfig" componentDelegate="com.enginuity.ui.runtime.components.PanelDelegate" x="0" y="0" width="590" height="197" resizeHorizontal="true">
    <panel name="JPanel_1" type="javax.swing.JPanel" componentDelegate="com.enginuity.ui.runtime.components.PanelDelegate" x="15" y="17" width="552" height="161" resizeHorizontal="true">
      <titledBorder title="${dialog.projectConfig.title}">
        <font name="Lucida Grande" style="Plain" size="13"/>
      </titledBorder>
      <pushButton name="m_btnBrowse" type="javax.swing.JButton" componentDelegate="com.enginuity.ui.runtime.components.ButtonDelegate" x="431" y="85" width="104" height="29" text="${dialog.common.browse}"/>
      <textField name="name" type="javax.swing.JTextField" componentDelegate="com.enginuity.ui.runtime.components.TextFieldComponentDelegate" x="141" y="50" width="276" height="28" toolTip="${dialog.projConfig.projName.tooltip}" resizeHorizontal="true"/>
      <textField name="location" type="javax.swing.JTextField" componentDelegate="com.enginuity.ui.runtime.components.TextFieldComponentDelegate" x="141" y="85" width="276" height="28" toolTip="${dialog.projConfig.projLoca.tooltip}" resizeHorizontal="true"/>
      <label name="JLabel_2" type="javax.swing.JLabel" componentDelegate="com.enginuity.ui.runtime.components.LabelDelegate" x="19" y="55" width="118" height="16" text="${dialog.projConfig.projName}"/>
      <label name="JLabel_4" type="javax.swing.JLabel" componentDelegate="com.enginuity.ui.runtime.components.LabelDelegate" x="19" y="91" width="115" height="16" text="${dialog.projConfig.label.projLoc}"/>
    </panel>
  </panel>
</viewDefinition>
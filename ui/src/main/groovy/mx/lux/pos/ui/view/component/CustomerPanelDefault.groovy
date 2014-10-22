package mx.lux.pos.ui.view.component

import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.verifier.NotEmptyVerifier
import net.miginfocom.swing.MigLayout

import javax.swing.*
import java.awt.*
import java.util.List

class CustomerPanelDefault extends CustomerPanel {

  private static final String TXT_GENERAL_DATA = 'Datos Generales'
  private static final String TXT_SALUTATION_LABEL = 'Saludo'
  private static final String TXT_GENDER_LABEL = 'Sexo'
  private static final String TXT_NAME_LABEL = 'Nombre'
  private static final String TXT_DOB_LABEL = 'F. Nacimiento'
  private static final String TXT_LAST_NAME_LABEL = 'Apellido Paterno'
  private static final String TXT_LAST_NAME_2_LABEL = 'Apellido Materno'
  private static final String TXT_STREET_LABEL = 'Calle y Numero '
  private static final String TXT_STATE_LABEL = 'Estado'
  private static final String TXT_CITY_LABEL = 'Ciudad'
  private static final String TXT_BLOCK_LABEL = 'Colonia'
  private static final String TXT_ZIP_LABEL = 'Código Postal'
  private static final String TXT_SEARCH_ZIP_LABEL = 'Buscar'

  private JComboBox cmbSalutation
  private JComboBox cmbGender
  private JComboBox cmbState
  private JComboBox cmbCity
  private JComboBox cmbBlock
  private JComboBox cmbZipCode
  private JTextField txtFirstName
  private JSpinner spnDOB
  private JTextField txtLastName
  private JTextField txtLastName2
  private JTextField txtStreet
  private JTextField txtPhone
  private JTextField txtEmail
  private JComboBox cmbDomain

  private List<String> domainList
  private List<String> stateList

  CustomerPanelDefault( ) {
    super()
    this.init()
  }

  // Internal Methods
  protected void init( ) {
    this.stateList = new ArrayList<String>()
    this.stateList.add( 'State 1' )
    this.stateList.add( 'State 2' )
    this.stateList.add( 'State 3' )
    this.domainList = new ArrayList<String>()
    this.domainList.add( 'Domain 1' )
    this.domainList.add( 'Domain 2' )
    this.domainList.add( 'Domain 3' )
  }

  // UI Layout
  void buildUI( ) {
    sb.panel( this ) {
      borderLayout()
      panel( border: titledBorder( TXT_GENERAL_DATA ),
          layout: new MigLayout( 'wrap 4', '[][fill,grow][][fill,grow]' ),
          constraints: BorderLayout.PAGE_START
      ) {
        label( TXT_SALUTATION_LABEL )
        cmbSalutation = comboBox( items: Titles.instance.list(), itemStateChanged: { onTitleChanged() } )

        label( TXT_GENDER_LABEL )
        cmbGender = comboBox( items: GenderType.values() )

        label( TXT_NAME_LABEL )
        txtFirstName = textField( document: new UpperCaseDocument(), inputVerifier: new NotEmptyVerifier() )

        label( TXT_DOB_LABEL )
        spnDOB = spinner( model: spinnerDateModel() )
        spnDOB.editor = new JSpinner.DateEditor( spnDOB as JSpinner, 'dd-MM-yyyy' )

        label( TXT_LAST_NAME_LABEL )
        txtLastName = textField( document: new UpperCaseDocument(), inputVerifier: new NotEmptyVerifier() )

        label( TXT_LAST_NAME_2_LABEL )
        txtLastName2 = textField( document: new UpperCaseDocument() )
      }

      panel( border: titledBorder( 'Dirección' ),
          layout: new MigLayout( 'wrap 3', '[][fill,grow][]' ),
          constraints: BorderLayout.CENTER
      ) {
        label( TXT_STREET_LABEL )
        txtStreet = textField( document: new UpperCaseDocument(), inputVerifier: new NotEmptyVerifier(), constraints: 'span 2' )

        label( TXT_STATE_LABEL )
        cmbState = comboBox( /*items: stateList,*/ itemStateChanged: { onStateChanged() }, constraints: 'span 2' )

        label( TXT_CITY_LABEL )
        cmbCity = comboBox( itemStateChanged: { onCityChanged() }, constraints: 'span 2' )

        label( TXT_BLOCK_LABEL )
        cmbBlock = comboBox( itemStateChanged: { onBlockChanged() }, constraints: 'span 2' )

        label( TXT_ZIP_LABEL )
        cmbZipCode = comboBox( editable: true )
        button( TXT_SEARCH_ZIP_LABEL, actionPerformed: { onSearchZip() } )
      }

      panel( border: titledBorder( 'Contacto' ),
          layout: new MigLayout( '', '[][fill,180!][center,25!][fill,180!]' ),
          constraints: BorderLayout.PAGE_END
      ) {
        label( text: ContactType.HOME_PHONE )
        txtPhone = textField( constraints: 'wrap' )

        label( text: ContactType.EMAIL )
        txtEmail = textField()
        label( '@' )
        cmbDomain = comboBox( editable: true /*, items: domainList*/ )
      }
    }
  }

  // UI Management
  void assign( ) {}

  void disableUI( ) {
    super.disableUI()
  }

  void enableUI( ) {
    super.enableUI()
  }

  void refreshUI( ) {
    txtFirstName.text = this.getCustomer().name
    txtLastName.text = this.getCustomer().fathersName
    txtLastName2.text = this.getCustomer().mothersName
    Contact lastPhone = this.getCustomer().getPhone(0)
    txtPhone.text = ( lastPhone != null ? lastPhone.primary : '' )
    Contact email = this.getCustomer().getEmail(0)
    txtEmail.text = ( email != null ? email.getLocal() : '' )
    cmbDomain.selectedItem = ( email != null ? email.getDomain() : '' )
    cmbSalutation.selectedItem = this.getCustomer().getTitle()

  }

  Boolean validateInput( ) {
    return true
  }

  // UI Triggers
  void onBlockChanged( ) {
    this.logger.debug( String.format( 'Colonia: %s(%f)', this.cmbBlock.selectedItem ) )
  }

  void onCityChanged( ) {
    this.logger.debug( String.format( 'Ciudad: %s(%f)', this.cmbCity.selectedItem ) )
  }

  void onSearchZip( ) {
    this.logger.debug( 'Search Address based on ZIP' )
  }

  void onStateChanged( ) {
    this.logger.debug( String.format( 'Estado: %s', this.cmbState.selectedItem.toString() ) )
  }

  void onTitleChanged( ) {
    if (this.isUIEnabled()) {
      this.logger.debug( String.format( 'Titulo: %s', this.cmbSalutation.selectedItem.toString() ) )
    }
  }

}

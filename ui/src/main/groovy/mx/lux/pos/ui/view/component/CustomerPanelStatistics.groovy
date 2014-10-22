package mx.lux.pos.ui.view.component

import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.verifier.NotEmptyVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*

class CustomerPanelStatistics extends CustomerPanel {

  private static final Integer EDAD_DEFAULT = 25
  private static final Integer EDAD_MINIMA = 10
  private static final Integer EDAD_MAXIMA = 100

  protected static final String TXT_GENERAL_DATA = 'Datos Generales'
  protected static final String TXT_GENDER_LABEL = 'Sexo'
  protected static final String TXT_NAME_LABEL = 'Nombre'
  protected static final String TXT_LAST_NAME_LABEL = 'Apellido Paterno'
  protected static final String TXT_LAST_NAME_2_LABEL = 'Apellido Materno'
  protected static final String TXT_STATE_LABEL = 'Estado'
  protected static final String TXT_CITY_LABEL = 'Ciudad'
  protected static final String TXT_AGE_LABEL = 'Edad'

  protected JComboBox cmbGender
  protected JComboBox cmbState
  protected JTextField txtFirstName
  protected JTextField txtLastName
  protected JTextField txtLastName2
  protected JTextField txtEmail
  protected JComboBox cmbDomain
  protected JSpinner spinAge
  protected JTextField txtCity

  protected List<String> domainList
  protected List<String> stateList

  CustomerPanelStatistics( ) {
    super()
    this.init()
  }

  // Internal Methods
  protected void init( ) {
    // TODO: RLD define state list through settings
    this.stateList = new ArrayList<String>()
    this.stateList.add( 'Distrito Federal' )
    this.stateList.add( 'Durango' )
    this.stateList.add( 'Jalisco' )
    this.cmbState = new JComboBox(this.stateList)
    // TODO: RLD define domain list through settings
    this.domainList = new ArrayList<String>()
    this.domainList.add( 'gmail.com' )
    this.domainList.add( 'hotmail.com' )
    this.domainList.add( 'yahoo.com' )
    this.cmbDomain = new JComboBox( this.domainList )
  }

  // UI Layout
  protected void buildUI( ) {
    sb.panel( this ) {
      borderLayout()
      vbox() {
        this.buildDataPanel()
        this.buildAddressPanel()
      }
    }
  }

  protected JPanel buildDataPanel() {
    JPanel panel = sb.panel( border: sb.titledBorder( TXT_GENERAL_DATA ),
        layout: new MigLayout( 'wrap 4', '[][fill,grow][][fill,grow]100!' ),
    ) {
      label( TXT_NAME_LABEL )
      txtFirstName = textField( document: new UpperCaseDocument(),
          inputVerifier: new NotEmptyVerifier(),
          constraints: 'span 3'
      )

      label( TXT_LAST_NAME_LABEL )
      txtLastName = textField( document: new UpperCaseDocument(),
          constraints: 'span 3'
      )

      label( TXT_LAST_NAME_2_LABEL )
      txtLastName2 = textField( document: new UpperCaseDocument(),
          constraints: 'span 3'
      )

      label( TXT_AGE_LABEL )
      spinAge = spinner( constraints: 'wrap',
          model: spinnerNumberModel( value: EDAD_DEFAULT, minimum: EDAD_MINIMA, maximum: EDAD_MAXIMA )
      )

      label( TXT_GENDER_LABEL )
      cmbGender = comboBox( items: GenderType.values(),
          constraints: 'wrap'
      )

      label( text: ContactType.EMAIL )
      txtEmail = textField()
      label( '@' )
      comboBox( cmbDomain, editable: true )
    }
    return panel
  }

  protected JPanel buildAddressPanel() {
    JPanel panel = sb.panel( border:sb.titledBorder( 'Dirección' ),
        layout: new MigLayout( 'wrap 2', '[][fill,grow]150!' ),
    ) {
      label( TXT_CITY_LABEL )
      txtCity = textField( document: new UpperCaseDocument() )

      label( TXT_STATE_LABEL )
      comboBox(cmbState)

    }
    return panel
  }

  protected Contact getEmail() {
    Contact found = null
    for ( Contact c : this.getCustomer().contacts ) {
      if ( ContactType.EMAIL.equals( c.type ) ) {
        found = c
        break
      }
    }
    return found
  }

  // UI Management
  void assign( ) {
    Customer c = this.getCustomer()
    c.name = StringUtils.trimToEmpty( this.txtFirstName.text )
    c.fathersName = StringUtils.trimToEmpty( this.txtLastName.text )
    c.mothersName = StringUtils.trimToEmpty( this.txtLastName2.text )
    c.title = ''
    c.rfc = c.type.rfc
    c.age = (spinAge.value as Integer)
    c.dob = DateUtils.truncate( DateUtils.addYears( new Date(), -1 * c.age ), Calendar.YEAR )
    c.gender = cmbGender.selectedItem
    c.address.city = StringUtils.trimToEmpty( this.txtCity.text )
    c.address.state = StringUtils.trimToEmpty( this.cmbState.selectedItem as String )
    if ( StringUtils.isNotBlank( txtEmail.text ) ) {
      Contact mail = new Contact()
      mail.type = ContactType.EMAIL
      mail.primary = String.format( '%s@%s', StringUtils.trimToEmpty( this.txtEmail.text ),
          StringUtils.trimToEmpty( this.cmbDomain.selectedItem as String ) )
      if ( !c.contacts.contains( mail ) ) {
        c.contacts.add( mail )
      }
    } else {
      c.contacts.clear()
    }
  }

  void refreshUI( ) {
    Customer c = this.getCustomer()
    this.txtFirstName.text = StringUtils.trimToEmpty( c.name )
    this.txtLastName.text = StringUtils.trimToEmpty( c.fathersName )
    this.txtLastName2.text = StringUtils.trimToEmpty( c.mothersName )
    spinAge.value = c.age
    cmbGender.selectedItem = c.gender
    this.txtCity.text = c.address.city
    this.cmbState.selectedItem = c.address.state
    Contact mail = this.getEmail()
    if (mail != null) {
      String[] tokens = StringUtils.split(mail.primary, '@' )
      this.txtEmail.text = StringUtils.trimToEmpty( tokens[0] )
      this.cmbDomain.selectedItem = ( tokens.length > 1 ? StringUtils.trimToEmpty( tokens[1] ) : '' )
    } else {
      this.txtEmail.text = ''
      this.cmbDomain.selectedIndex = 0
    }
  }

  Boolean validateInput( ) {
    return true
  }

  // UI Triggers
}

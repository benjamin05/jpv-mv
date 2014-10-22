package mx.lux.pos.service.io

import mx.lux.pos.model.Acuse
import mx.lux.pos.repository.AcuseRepository
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.business.Registry
import mx.lux.pos.service.impl.ServiceFactory
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.DateFormat
import java.text.SimpleDateFormat

class AsynchronousNotificationDispatcher implements Runnable {

  private static final String FMT_LOG_MESSAGE = '%s     %s [%s] %s'
  private static final String FMT_LOG_DISPATCH_ACK = 'Dispatching: %s'
  private static final String FMT_LOG_PENDING_QUEUE = 'Processing %d pending notifications.'
  private static final String MSG_START_CYCLE = 'Start notification cycle'
  private static final String MSG_NOTHING_TO_PROCESS = 'Processing queue is empty.'
  private static final String FMT_LOG_ACK_RCVD = 'Acknowledgment: %s(%d) <- %s'
  private static final String FMT_LOG_ACK_FAILED = 'Failed Acknowledge: %s(%d) <- %s'

  private static final DateFormat df = new SimpleDateFormat( 'yyyy-MM-dd HH:mm:ss' )

  Logger logger = LoggerFactory.getLogger( this.getClass().getSimpleName() )

  private static AsynchronousNotificationDispatcher instance

  private AsynchronousNotificationDispatcher( ) { }

  static AsynchronousNotificationDispatcher getInstance( ) {
    if ( instance == null ) {
      instance = new AsynchronousNotificationDispatcher()
    }
    return instance
  }

  // Internal methods
  private void debug( String pMessage ) {
    if ( Registry.isAckDebugEnabled() ) {
      println( this.format( 'DEBUG', pMessage ) )
    }
  }

  private void dispatch( Acuse pNotification ) {
    this.debug( String.format( FMT_LOG_DISPATCH_ACK, pNotification.toString() ) )
    String url = Registry.getURL( pNotification.idTipo )
    if ( StringUtils.trimToNull( url ) != null ) {
      pNotification.intentos = pNotification.intentos + 1
      url += String.format( '?%s', pNotification.contenido )
      try {
        String response = url.toURL().text
        response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
        pNotification.folio = response
        pNotification.fechaAcuso = new Date()
        this.debug( String.format( FMT_LOG_ACK_RCVD, pNotification.idTipo, pNotification.id, response ) )
      } catch ( Exception e ) {
        this.info( String.format( FMT_LOG_ACK_FAILED, pNotification.idTipo, pNotification.id, e.getMessage() ) )
      }
      ServiceFactory.ioServices.saveAcknowledgement( pNotification )
    }
  }

  private String format( String pLogLevel, String pMessage ) {
    return String.format( FMT_LOG_MESSAGE,   df.format( new Date() ), pLogLevel, this.name, pMessage )
  }

  private Collection<Acuse> getPendingNotifications( ) {
    AcuseRepository db = RepositoryFactory.acknowledgements
    return db.findPending()
  }

  private void info( String pMessage ) {
    println( this.format( 'INFO ', pMessage ) )
  }

  private void pause( ) {
    Integer milis = ( Integer ) ( Registry.ackDelay * 1000.0 )
    try {
      sleep( milis );
    } catch ( InterruptedException e ) {}
  }

  // Public methods
  String getName( ) {
    return this.getClass().getSimpleName()
  }

  void run( ) {
    while ( true ) {
      this.debug( MSG_START_CYCLE )
      Collection<Acuse> pending = this.getPendingNotifications()
      if ( pending.size() > 0 ) {
        this.debug( String.format( FMT_LOG_PENDING_QUEUE, pending.size() ) )
        for ( Acuse notification : pending ) {
          this.dispatch( notification )
        }
      } else {
        this.debug( MSG_NOTHING_TO_PROCESS )
      }
      this.pause()
    }
  }

}

package mx.lux.pos.service.io

import mx.lux.pos.service.business.Registry
import mx.lux.pos.util.CustomDateUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ArchiveTask {

  private static final String FILE_ARCHIVE_DEFAULT = 'soi.%s'
  private static final String FMT_DATE_TIME = 'yyyy-MM-dd-HH-mm'
  private static final String EXT_ZIP = '.MAEM'
  private static final String SO_WINDOWS = 'Windows'

  private Logger logger = LoggerFactory.getLogger( this.getClass() )
  private String baseDir
  private String filePattern
  private String archiveFile

  // Internal methods
  protected String getArchiveFile( ) {
    String filename = this.archiveFile
    if ( filename == null ) {
      filename = String.format( FILE_ARCHIVE_DEFAULT, CustomDateUtils.format( new Date(), FMT_DATE_TIME ) )
    }
    //return Registry.archivePath + File.separator + filename + EXT_ZIP
    return Registry.getParametroOS("ruta_por_enviar") + File.separator + filename + EXT_ZIP
  }


  protected String getArchiveFileDropbox( ) {
      String filename = this.archiveFile
      if ( filename == null ) {
        filename = String.format( FILE_ARCHIVE_DEFAULT, CustomDateUtils.format( new Date(), FMT_DATE_TIME ) )
      }
      //return Registry.archivePathDropbox + File.separator + filename + EXT_ZIP
      return Registry.getParametroOS("ruta_por_enviar_dropbox") + File.separator + filename + EXT_ZIP
  }


  protected String getArchiveFileMessenger( ) {
    String filename = this.archiveFile
    if ( filename == null ) {
      filename = String.format( FILE_ARCHIVE_DEFAULT, CustomDateUtils.format( new Date(), FMT_DATE_TIME ) )
    }

      return Registry.getParametroOS("ruta_por_enviar_mensajero") + File.separator + filename + EXT_ZIP
    //return Registry.archivePathMessenger + File.separator + filename + EXT_ZIP
  }
  // Public methods
  void run( ) {
      System.out.println("ArchiveTask.run()")
    if ( ( this.filePattern != null ) && ( this.baseDir != null ) ) {
        System.out.println("ArchiveTask.run().if()")
      //String sSistemaOperativo = System.getProperty("os.name");
      //logger.debug(sSistemaOperativo);
      StringBuffer sb = new StringBuffer()
      StringBuffer sbChec = new StringBuffer()
      StringBuffer sbDrop = new StringBuffer()
      StringBuffer sbDropChec = new StringBuffer()
      StringBuffer sbMsgr = new StringBuffer()
      StringBuffer sbMsgrChec = new StringBuffer()
      //sb.append( String.format( "%s ", Registry.archiveCommand ) );
      sb.append( String.format( "%s ", Registry.getParametroOS("comando_zip") ) );
      if( Registry.getOperatingSystem().trim().startsWith( SO_WINDOWS ) ){
        sb.append( String.format( '"%s" ', this.getArchiveFile() ) );
        sb.append( String.format( '"%s" ', this.baseDir + File.separator + this.filePattern ) )
        sb.append( String.format( '"%s" ', this.baseDir+File.separator+"*.inv" ) )
        /*sbChec.append( String.format( '"%s" ', this.getArchiveFile() ) );
        sbChec.append( String.format( '"%s" ', this.baseDir + File.separator + this.filePattern ) )
        sbChec.append( String.format( '"%s" ', this.baseDir+File.separator+"*.rche" ) )*/
      } else {
        sb.append( String.format( '%s ', this.getArchiveFile() ) );
        sb.append( String.format( '%s ', this.baseDir + File.separator + this.filePattern ) )
        sb.append( String.format( '%s ', this.baseDir+File.separator+"*.inv" ) )
        /*sbChec.append( String.format( '%s ', this.getArchiveFile() ) );
        sbChec.append( String.format( '%s ', this.baseDir + File.separator + this.filePattern ) )
        sbChec.append( String.format( '%s ', this.baseDir+File.separator+"*.rche" ) )*/
        sbDrop.append( String.format( '%s ', this.getArchiveFileDropbox() ) );
        sbDrop.append( String.format( '%s ', this.baseDir + File.separator + this.filePattern ) )
        sbDrop.append( String.format( '%s ', this.baseDir+File.separator+"*.inv" ) )
        /*sbDropChec.append( String.format( '%s ', this.getArchiveFileDropbox() ) );
        sbDropChec.append( String.format( '%s ', this.baseDir + File.separator + this.filePattern ) )
        sbDropChec.append( String.format( '%s ', this.baseDir+File.separator+"*.rche" ) )*/
        sbMsgr.append( String.format( '%s ', this.getArchiveFileMessenger() ) );
        sbMsgr.append( String.format( '%s ', this.baseDir + File.separator + this.filePattern ) )
        sbMsgr.append( String.format( '%s ', this.baseDir+File.separator+"*.inv" ) )
        /*sbMsgrChec.append( String.format( '%s ', this.getArchiveFileMessenger() ) );
        sbMsgrChec.append( String.format( '%s ', this.baseDir + File.separator + this.filePattern ) )
        sbMsgrChec.append( String.format( '%s ', this.baseDir+File.separator+"*.inv" ) )*/
      }
      StringBuffer sb2 = new StringBuffer()
      for ( char c : sb.toString().toCharArray() ) {
        if ( ( c == '\\' ) || ( c == '/' ) ) {
          sb2.append( File.separator )
        } else {
          sb2.append( c )
        }
      }

      StringBuffer sb5 = new StringBuffer()
      for ( char c : sbChec.toString().toCharArray() ) {
        if ( ( c == '\\' ) || ( c == '/' ) ) {
          sb5.append( File.separator )
        } else {
          sb5.append( c )
        }
      }

      StringBuffer sb3 = new StringBuffer()
      for ( char c : sbDrop.toString().toCharArray() ) {
          if ( ( c == '\\' ) || ( c == '/' ) ) {
              sb3.append( File.separator )
          } else {
              sb3.append( c )
          }
      }

      StringBuffer sb6 = new StringBuffer()
      for ( char c : sbDropChec.toString().toCharArray() ) {
        if ( ( c == '\\' ) || ( c == '/' ) ) {
          sb6.append( File.separator )
        } else {
          sb6.append( c )
        }
      }

      StringBuffer sb4 = new StringBuffer()
      for ( char c : sbMsgr.toString().toCharArray() ) {
        if ( ( c == '\\' ) || ( c == '/' ) ) {
          sb4.append( File.separator )
        } else {
          sb4.append( c )
        }
      }

      StringBuffer sb7 = new StringBuffer()
      for ( char c : sbMsgrChec.toString().toCharArray() ) {
        if ( ( c == '\\' ) || ( c == '/' ) ) {
          sb7.append( File.separator )
        } else {
          sb7.append( c )
        }
      }

      String cmd = sb2.toString()
      String cmd1 = sb3.toString()
      String cmd2 = sb4.toString()
      String cmd3 = sb5.toString()
      String cmd4 = sb6.toString()
      String cmd5 = sb7.toString()
      logger.debug( String.format( "ZIP Command: <%s>", cmd ) )
      logger.debug( String.format( "ZIP Command: <%s>", cmd1 ) )
      logger.debug( String.format( "ZIP Command: <%s>", cmd2 ) )
      logger.debug( String.format( "ZIP Command: <%s>", cmd3 ) )
      logger.debug( String.format( "ZIP Command: <%s>", cmd4 ) )
      logger.debug( String.format( "ZIP Command: <%s>", cmd5 ) )

      File f = new File( this.getArchiveFile() )
      if ( f.exists() ) {
        f.delete()
      }

      String filename;
      if ( Registry.getOperatingSystem().startsWith("Linux") ) {
        filename = "empaqueta.sh"
      }else{
        filename = "empaqueta.bat"
      }

      try {
          /*
          File file = new File(filename)
          if (file.exists()) {
              file.delete()
          }
          PrintStream strOut = new PrintStream(file)
          StringBuffer sb1 = new StringBuffer()
          //sb1.append('CIERRE_HOME='+Registry.dailyClosePath)
          //if (Registry.getOperatingSystem().startsWith("Linux")) {
          sb1.append('CIERRE_HOME=' + Registry.getParametroOS("ruta_cierre"))
          sb1.append("\n")
          sb1.append('cd $CIERRE_HOME')
          sb1.append("\n")
          sb1.append(Registry.getParametroOS("comando_zip") + " " + this.getArchiveFile() + ' ' + this.filePattern + ' ' + "*.inv")
          //sb1.append('tar -cvf ' + this.getArchiveFile() + ' ' + this.filePattern + ' ' + "*.inv")
          sb1.append("\n")
          sb1.append(Registry.getParametroOS("comando_zip") + " " + this.getArchiveFileMessenger() + ' ' + this.filePattern + ' ' + "*.inv")
          sb1.append("\n")
          sb1.append(Registry.getParametroOS("comando_zip") + " " + this.getArchiveFileDropbox() + ' ' + this.filePattern + ' ' + "*.inv")
          strOut.println sb1.toString()
          strOut.close()

          String s = null
          file.setExecutable(true)
          file.setReadable(true)
          file.setWritable(true)
          if (Registry.getOperatingSystem().startsWith("Linux")) {
              Process p1 = Runtime.getRuntime().exec("chmod 777 empaqueta.sh");
              Process p = Runtime.getRuntime().exec("./empaqueta.sh");
          }else{
              //Pendiente Windows
              //Process p1 = Runtime.getRuntime().exec("chmod 777 empaqueta.sh");
              //Process p = Runtime.getRuntime().exec("./empaqueta.bat");
          }

              BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
              BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

              while ((s = stdInput.readLine()) != null) {
                  println(s + "\n");
              }
              while ((s = stdError.readLine()) != null) {
                  println(s + "\n");
              }
          //} else{

         // }*/

          // Genera archivos Paso
          //sb1.append(Registry.getParametroOS("comando_zip") + " " + this.getArchiveFile() + ' ' + this.filePattern + ' ' + "*.inv")
        if( Registry.getOperatingSystem().trim().startsWith( SO_WINDOWS ) ){
          String command = Registry.getParametroOS("comando_zip") + " " + this.getArchiveFile()+' '+ Registry.getParametroOS("ruta_cierre") + '/' + this.filePattern + ' ' + Registry.getParametroOS("ruta_cierre") + '/' + "*.inv";
          logger.debug(command)
          Process p1 = Runtime.getRuntime().exec(command);
          // Genera archivos Mensajero
          String command2 = Registry.getParametroOS("comando_zip") + " " + this.getArchiveFileDropbox() +' '+ Registry.getParametroOS("ruta_cierre") + '/' + this.filePattern + ' ' + Registry.getParametroOS("ruta_cierre") + '/' +"*.inv";
          logger.debug(command2)
          Process p2 = Runtime.getRuntime().exec(command2);
          /*Process p2 = Runtime.getRuntime().exec(command);
            command = Registry.getParametroOS("comando_zip") + " " + this.getArchiveFileMessenger() +' '+ Registry.getParametroOS("ruta_cierre") + '/' + this.filePattern + ' ' + Registry.getParametroOS("ruta_cierre") + '/' +"*.rche";
            logger.debug(command)
            Runtime.getRuntime().exec(command);*/
        } else {
            File file = new File( 'empaqueta.sh' )
            if ( file.exists() ) {
                file.delete()
            }
            PrintStream strOut = new PrintStream( file )
            StringBuffer sb1 = new StringBuffer()
            sb1.append('CIERRE_HOME='+Registry.dailyClosePath)
            sb1.append( "\n" )
            sb1.append('cd $CIERRE_HOME')
            sb1.append( "\n" )
            sb1.append('tar -cvf '+this.getArchiveFile()+' '+this.filePattern+' '+"*.inv")
            sb1.append( "\n" )
            sb1.append('tar -cvf '+this.getArchiveFileMessenger()+' '+this.filePattern+' '+"*.inv")
            sb1.append( "\n" )
            sb1.append('tar -cvf '+this.getArchiveFileDropbox()+' '+this.filePattern+' '+"*.inv")
            sb1.append( "\n" )
            /*sb1.append('tar -cvf '+this.getArchiveFile()+' '+this.filePattern+' '+"*.rche")
            sb1.append( "\n" )
            sb1.append('tar -cvf '+this.getArchiveFileDropbox()+' '+this.filePattern+' '+"*.rche")
            sb1.append( "\n" )*/
            strOut.println sb1.toString()
            strOut.close()

            String s = null
            file.setExecutable( true )
            file.setReadable( true )
            file.setWritable( true )
            Process p1 = Runtime.getRuntime().exec("chmod 777 empaqueta.sh");
            Process p = Runtime.getRuntime().exec("./empaqueta.sh");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                println(s+"\n");
            }
            while ((s = stdError.readLine()) != null) {
                println(s+"\n");
            }
        }

      } catch ( Exception e ) {
        logger.error( e.getMessage(), e )
      }
      //    AntBuilder ant = new AntBuilder()
      //      logger.debug( String.format( 'Zipping <%s> into <%s>', this.filePattern, this.getArchiveFile() ) )
      //      ant.zip( destfile: this.getArchiveFile(),
      //               basedir: this.baseDir,
      //               includes: this.filePattern
      //      )
      //      if (f.exists()) {
      //        logger.debug( String.format( 'Archive file: <%s> %,d bytes', f.getAbsolutePath(), f.size() ) )
      //      }
    } else {
      logger.debug( 'Nothing to archive.' )
    }

  }

  void setArchiveFile( String pFilename ) {
    this.archiveFile = StringUtils.trimToEmpty( pFilename )
  }

  void setBaseDir( String pBaseDir ) {
    this.baseDir = StringUtils.trimToEmpty( pBaseDir )
  }

  void setFilePattern( String pFilePattern ) {
    this.filePattern = StringUtils.trimToEmpty( pFilePattern )
  }

}
//
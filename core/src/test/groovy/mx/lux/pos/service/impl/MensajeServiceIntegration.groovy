package mx.lux.pos.service.impl

import mx.lux.pos.service.business.Registry
import mx.lux.pos.service.io.MessageImportTask
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration( 'classpath:spring-config.xml' )
class MensajeServiceIntegration extends Specification {

  def testMensajeImport() {
    setup:
    MessageImportTask task = new MessageImportTask()
    File toProcessFile = new File(task.getFilename())
    File processedFile = new File( Registry.processedFilesPath + File.separator + toProcessFile.name )
    if ( processedFile.exists() ) {
      if ( !toProcessFile.exists() ) {
        processedFile.renameTo(toProcessFile)
      }
      processedFile = new File( Registry.processedFilesPath + File.separator + toProcessFile.name )
      if (processedFile.exists()) {
        processedFile.delete()
      }
    }

    when:
    ServiceFactory.messages.importarArchivo()
    processedFile = new File( Registry.processedFilesPath + File.separator + toProcessFile.name )

    then:
    processedFile.exists()

  }


}

package mx.lux.pos.service.impl

import mx.lux.pos.service.SettingsService
import mx.lux.pos.service.business.Registry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional( readOnly = true )
class SettingsServiceImpl implements SettingsService {

  String getSiteSegment() {
    return Registry.siteSegment
  }

  Double getAdvancePct() {
    return Registry.advancePct
  }

  String getIncomingPath() {
    return Registry.inputFilePath
  }

  String getProcessedPath() {
    return Registry.processedFilesPath
  }

}

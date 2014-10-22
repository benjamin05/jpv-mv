package mx.lux.pos.ui.controller

import mx.lux.pos.ui.resources.ServiceManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SettingsController {

  private Logger log = LoggerFactory.getLogger(this.getClass())
  private static SettingsController instance
  private SettingsController() { }
  static SettingsController getInstance() {
    if (instance == null) {
      instance = new SettingsController()
    }
    return instance
  }

  String getSiteSegment() {
    return ServiceManager.settingsService.siteSegment
  }

  Double getAdvancePct() {
    return ServiceManager.settingsService.advancePct
  }

  String getIncomingPath() {
    return ServiceManager.settingsService.incomingPath
  }

  String getProcessedPath() {
    return ServiceManager.settingsService.processedPath
  }

}

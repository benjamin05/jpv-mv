package mx.lux.pos.ui.model


class HelpSearchItem {
    String example
    String description


    static toHelp( String example, String description ) {
      HelpSearchItem helpSearchItem = new HelpSearchItem(
        example: example,
        description: description
      )
      return helpSearchItem
    }
}

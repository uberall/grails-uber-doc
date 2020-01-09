package sample

import uberdoc.annotation.UberDocModel

@UberDocModel(internalOnly = true)
class Internal extends AbstractObject{

    String secretField
}

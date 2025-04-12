/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.exception

import moe.hhm.parfait.infra.i18n.I18nUtils

class BusinessException : Throwable {
    constructor(msgKey: String) : super(I18nUtils.getText(msgKey))
    constructor(msgKey: String, cause: Throwable) : super(I18nUtils.getText(msgKey), cause)
    constructor(msgKey: String, vararg args: String) : super(I18nUtils.getFormattedText(msgKey, args))
    constructor(msgKey: String, vararg args: String, cause: Throwable) : super(
        I18nUtils.getFormattedText(msgKey, args),
        cause
    )

    constructor(cause: Throwable) : super(cause)
}
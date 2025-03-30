package moe.eleb.parafit.domain.exception

open class BusinessException(message: String, cause: Throwable? = null) : Exception(message, cause)

class NotFoundException(message: String) : BusinessException(message)
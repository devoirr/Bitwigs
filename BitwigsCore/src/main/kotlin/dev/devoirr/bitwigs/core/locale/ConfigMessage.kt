package dev.devoirr.bitwigs.core.locale

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigMessage(
    val path: String,
    val defaultValue: String = "&fㅅ Сообщение не настроено, обратитесь к администрации."
)

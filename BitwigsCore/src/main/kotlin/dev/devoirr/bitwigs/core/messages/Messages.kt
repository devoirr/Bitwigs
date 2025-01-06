package dev.devoirr.bitwigs.core.messages

import dev.devoirr.bitwigs.core.config.Config

enum class Messages(private val path: String, private val defaultValue: String) {

    PREFIX_INFO("prefix.info", "&f빕 "),
    PREFIX_ERROR("prefix.error", "&fㅅ "),

    MESSAGES_RELOADED("messages-reloaded", "Сообщения перезагружены."),

    COMMAND_ONLY_FOR_PLAYERS("only-for-players", "Команда доступна только игрокам!"),
    NO_PERMISSION("no-permission", "У вас нет доступа к этой команде."),
    PLAYER_NOT_FOUND("player-not-found", "Игрок {name} не найден."),
    PLAYER_UNTELEPORTABLE("player-unteleportable", "Игрока {name} нельзя телепортировать."),
    COMMAND_BALANCE_RESULT_OWN("economy.balance.own", "Ваш баланс: {balance}"),
    COMMAND_BALANCE_RESULT_OTHER("economy.balance.other", "Баланс игрока {target}: {balance}"),
    COMMAND_BALANCE_ONLY_OWN("economy.balance.only-own", "Вы не можете просматривать баланс других игроков!"),
    COMMAND_BALANCE_EMPTY("economy.balance.empty", "на аккаунте нет денег"),

    COMMAND_ECONOMY_ADD_SUCCESS("economy.add-success", "Вы добавили {amount}{currency} на аккаунт игрока {target}!"),
    COMMAND_ECONOMY_SET_SUCCESS("economy.set-success", "Вы установили {amount}{currency} на аккаунт игрока {target}!"),
    COMMAND_ECONOMY_TAKE_SUCCESS("economy.take-success", "Вы забрали {amount}{currency} с аккаунта игрока {target}!"),
    COMMAND_ECONOMY_NOT_REGISTERED("economy.account-not-registered", "Аккаунт {target} не найден."),
    COMMAND_ECONOMY_NOT_ENOUGH_MONEY("economy.not-enough-money", "На аккаунте {target} недостаточно средств."),
    COMMAND_ECONOMY_CURRENCY_NOT_FOUND("economy.currency-not-found", "Валюта {input} не найдена!"),
    COMMAND_ECONOMY_AMOUNT_CANNOT_BE_NEGATIVE(
        "economy.amount-cannot-be-negative",
        "Сумма не может быть отрицательной."
    ),
    COMMAND_ECONOMY_AMOUNT_MUST_BE_POSITIVE("economy.amount-must-be-positive", "Сумма должна быть положительной."),

    COMMAND_WARP_NAME_REGEX(
        "warps.name-not-matching-regex",
        "Имя должно быть от 3-ех до 20-ти символов и содержать только латинские буквы."
    ),
    COMMAND_WARP_ALREADY_EXISTS("warps.already-exists", "Варп {name} уже существует."),
    COMMAND_WARP_CREATED("warps.created", "Варп {name} успешно создан."),
    COMMAND_WARP_NOT_FOUND("warps.not-found", "Варп {name} не найден."),
    COMMAND_WARP_DELETED("warps.deleted", "Варп {name} успешно удалён."),
    COMMAND_WARP_TELEPORTED_OTHER(
        "warps.teleported-other",
        "Вы телепортировали игрока {target} на варп {name}."
    ),
    COMMAND_WARP_TELEPORTED("warps.teleported", "-actionbar Вы телепортированы на варп {name}"),

    CHAT_MESSAGE_TOO_LONG("chat.too-long", "Максимальная длина сообщения: 100 символов."),
    CHAT_MESSAGE_TOO_SHORT("chat.too-short", "Минимальная длина сообщения: 1 символ"),
    CHAT_MESSAGE_REGEX("chat.regex", "В вашем сообщении содержатся запрещенные символы."),
    ;

    companion object {

        private val messages = mutableMapOf<Messages, String>()

        fun load(config: Config) {
            for (entry in entries) {
                if (config.get().getString(entry.path) == null) {
                    config.get().set(entry.path, entry.defaultValue)
                }

                messages[entry] = config.get().getString(entry.path)!!
            }

            config.save()
        }

    }

    fun getLiteral() = messages[this]!!

    fun get(): Message {
        return Message(messages[this]!!, false)
    }

    fun getInfo(): Message {
        return get().getInfo()
    }

    fun getError(): Message {
        return get().getError()
    }

}
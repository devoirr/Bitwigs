package dev.devoirr.bitwigs.core.locale

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.config.Config
import java.io.File

object Locale {

    @ConfigMessage("no-permission")
    lateinit var noPermission: Message

    @ConfigMessage("player-not-found")
    lateinit var playerNotFound: Message

    @ConfigMessage("player-unteleportable")
    lateinit var playerUnteleportable: Message

    @ConfigMessage("command-for-players")
    lateinit var commandForPlayers: Message

    @ConfigMessage("you-need-to-hold-an-item")
    lateinit var needToHoldAnItem: Message

    @ConfigMessage("must-look-at-block")
    lateinit var mustLookAtBlock: Message

    // ---- Action blocks ----
    @ConfigMessage("blocks.action.already-action-block")
    lateinit var alreadyActionBlock: Message

    @ConfigMessage("blocks.action.created")
    lateinit var actionBlockCreated: Message

    @ConfigMessage("blocks.action.type-not-found")
    lateinit var actionBlockTypeNotFound: Message

    @ConfigMessage("blocks.action.not-an-action-block")
    lateinit var notActionBlock: Message

    @ConfigMessage("blocks.action.removed")
    lateinit var actionBlockRemoved: Message
    // ---- Action blocks ----

    // ---- Dropping blocks ----
    @ConfigMessage("blocks.dropping.item-already-exists")
    lateinit var droppingBlockItemAlreadyExists: Message

    @ConfigMessage("blocks.dropping.item-saved")
    lateinit var droppingBlockItemSaved: Message

    @ConfigMessage("blocks.dropping.already-dropping-block")
    lateinit var alreadyDroppingBlock: Message

    @ConfigMessage("blocks.dropping.type-not-found")
    lateinit var droppingBlockTypeNotFound: Message

    @ConfigMessage("blocks.dropping.created")
    lateinit var droppingBlockCreated: Message

    @ConfigMessage("blocks.dropping.removed")
    lateinit var droppingBlockRemoved: Message

    @ConfigMessage("blocks.dropping.not-a-dropping-block")
    lateinit var notDroppingBlock: Message
    // ---- Dropping blocks ----

    // ---- Chat ----
    @ConfigMessage("chat.message-too-long")
    lateinit var chatMessageTooLong: Message

    @ConfigMessage("chat.message-too-short")
    lateinit var chatMessageTooShort: Message

    @ConfigMessage("chat.message-regex")
    lateinit var chatMessageRegex: Message
    // ---- Chat ----

    // ---- Balance ----
    @ConfigMessage("economy.balance.empty")
    lateinit var emptyBalance: Message

    @ConfigMessage("economy.balance.result-own")
    lateinit var balanceResultOwn: Message

    @ConfigMessage("economy.balance.result-other")
    lateinit var balanceResultOther: Message

    @ConfigMessage("economy.balance.only-own")
    lateinit var balanceOnlyOwn: Message
    // ---- Balance ----

    // ---- Economy ----
    @ConfigMessage("economy.currency-not-found")
    lateinit var currencyNotFound: Message

    @ConfigMessage("economy.transaction-must-be-positive")
    lateinit var transactionMustBePositive: Message

    @ConfigMessage("economy.transaction-cant-be-negative")
    lateinit var transactionCantBeNegative: Message

    @ConfigMessage("economy.account-not-registered")
    lateinit var accountNotRegistered: Message

    @ConfigMessage("economy.add-success")
    lateinit var transactionAddSuccess: Message

    @ConfigMessage("economy.take-success")
    lateinit var transactionTakeSuccess: Message

    @ConfigMessage("economy.set-success")
    lateinit var transactionSetSuccess: Message

    @ConfigMessage("economy.take-not-enough")
    lateinit var transactionTakeNotEnough: Message
    // ---- Economy ----

    // ---- Kits ----
    @ConfigMessage("kits.kit-already-exists")
    lateinit var kitAlreadyExists: Message

    @ConfigMessage("kits.kit-created")
    lateinit var kitCreated: Message

    @ConfigMessage("kits.kit-not-found")
    lateinit var kitNotFound: Message
    // ---- Kits ----

    // ---- Warps ----
    @ConfigMessage("warps.warp-already-exists")
    lateinit var warpAlreadyExists: Message

    @ConfigMessage("warps.warp-name-regex")
    lateinit var warpNameRegex: Message

    @ConfigMessage("warps.warp-created")
    lateinit var warpCreated: Message

    @ConfigMessage("warps.warp-deleted")
    lateinit var warpDeleted: Message

    @ConfigMessage("warps.warp-not-found")
    lateinit var warpNotFound: Message

    @ConfigMessage("warps.teleported")
    lateinit var warpTeleported: Message

    @ConfigMessage("warps.teleported-other")
    lateinit var warpTeleportedOther: Message

    @ConfigMessage("warps.limit-reached")
    lateinit var warpsLimit: Message

    @ConfigMessage("warps.can-only-remove-own")
    lateinit var warpsDeleteOnlyOwn: Message
    // ---- Warps ----

    fun load() {
        val plugin = BitwigsPlugin.instance
        val config = Config(File(plugin.dataFolder, "locale.yml"))

        var annotation: ConfigMessage?
        for (field in Locale::class.java.declaredFields) {
            annotation = field.getAnnotation(ConfigMessage::class.java)
            if (annotation == null)
                continue

            if (config.get().getString(annotation.path) != null) {
                config.get().getString(annotation.path)?.let {
                    field.set(this, Message(it))
                }
            } else {
                field.set(this, Message(annotation.defaultValue))
                config.get().set(annotation.path, annotation.defaultValue)
            }
        }

        config.save()
    }

}
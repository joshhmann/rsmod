package org.rsmod.content.interfaces.bank.scripts

import jakarta.inject.Inject
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifOpenMainModal
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onIfOpen
import org.rsmod.content.interfaces.bank.configs.bank_components
import org.rsmod.content.interfaces.bank.configs.bank_interfaces
import org.rsmod.content.interfaces.bank.configs.bank_varps
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Bank PIN security system implementation.
 *
 * Handles:
 * - PIN settings interface (setting, changing, deleting PIN)
 * - PIN keypad interface for PIN entry
 * - PIN state management (no pin, pending, active)
 * - Recovery delay settings
 */
class BankPinScript @Inject constructor(private val eventBus: EventBus) : PluginScript() {

    // Player extension properties for PIN state using varp delegates
    private var Player.pinState by intVarp(bank_varps.bankpin_state)
    private var Player.pinVerified by intVarp(bank_varps.bankpin_verified)
    private var Player.recoveryDelay by intVarp(bank_varps.bankpin_recovery_delay)
    private var Player.pinEntryMode by intVarp(bank_varps.bankpin_entry_mode)
    private var Player.pinEntryProgress by intVarp(bank_varps.bankpin_entry_progress)
    private var Player.pinEntryBuffer by intVarp(bank_varps.bankpin_entry_buffer)
    private var Player.pinValue by intVarp(bank_varps.bankpin_value)
    private var Player.pendingPin by intVarp(bank_varps.bankpin_pending)

    override fun ScriptContext.startup() {
        // Bank PIN Settings interface handlers
        onIfOpen(bank_interfaces.bankpin_settings) { player.onBankPinSettingsOpen() }

        // PIN Settings buttons - No PIN state
        onIfModalButton(bank_components.bankpin_set) { player.openPinKeypad(PinMode.SET) }
        onIfModalButton(bank_components.bankpin_delay0) { player.setPinRecoveryDelay(0) }
        onIfModalButton(bank_components.bankpin_delay1) { player.setPinRecoveryDelay(1) }

        // PIN Settings buttons - Has PIN state
        onIfModalButton(bank_components.bankpin_change) { player.openPinKeypad(PinMode.CHANGE) }
        onIfModalButton(bank_components.bankpin_delete) { player.requestPinDeletion() }

        // PIN Settings buttons - Pending PIN state
        onIfModalButton(bank_components.bankpin_cancel) { player.cancelPendingPin() }

        // PIN Keypad buttons
        onIfModalButton(bank_components.bankpin_keypad_digit1) { player.enterPinDigit(1) }
        onIfModalButton(bank_components.bankpin_keypad_digit2) { player.enterPinDigit(2) }
        onIfModalButton(bank_components.bankpin_keypad_digit3) { player.enterPinDigit(3) }
        onIfModalButton(bank_components.bankpin_keypad_digit4) { player.enterPinDigit(4) }
    }

    private fun Player.onBankPinSettingsOpen() {
        updatePinSettingsInterface()
    }

    private fun Player.updatePinSettingsInterface() {
        val state = pinState
        val delay = recoveryDelay

        // Update status text based on PIN state
        val statusText =
            when (state) {
                0 -> "You do not have a Bank PIN."
                1 -> "Your Bank PIN will be active in a few days."
                2 -> "Your Bank PIN is active."
                else -> "You do not have a Bank PIN."
            }
        ifSetText(bank_components.bankpin_statusoutput, statusText)

        // Update recovery delay text
        val delayText =
            when (delay) {
                0 -> "No delay"
                1 -> "7 days"
                else -> "$delay days"
            }
        ifSetText(bank_components.bankpin_delayoutput, delayText)

        // Update logout text
        ifSetText(bank_components.bankpin_logoutoutput, "Never")
    }

    private fun Player.openPinKeypad(mode: PinMode) {
        pinEntryMode = mode.ordinal
        pinEntryProgress = 0
        pinEntryBuffer = 0
        ifOpenMainModal(bank_interfaces.bankpin_keypad, eventBus = eventBus)
        updateKeypadInterface()
    }

    private fun Player.updateKeypadInterface() {
        val mode = PinMode.entries.getOrElse(pinEntryMode) { PinMode.VERIFY }
        val progress = pinEntryProgress

        val title =
            when (mode) {
                PinMode.SET ->
                    when (progress) {
                        0 -> "Please enter your new PIN"
                        1 -> "Please confirm your new PIN"
                        else -> "PIN set successfully"
                    }
                PinMode.CHANGE ->
                    when (progress) {
                        0 -> "Please enter your current PIN"
                        1 -> "Please enter your new PIN"
                        2 -> "Please confirm your new PIN"
                        else -> "PIN changed successfully"
                    }
                PinMode.VERIFY -> "Please enter your PIN"
                PinMode.DELETE ->
                    when (progress) {
                        0 -> "Please enter your PIN to delete it"
                        else -> "PIN deletion requested"
                    }
            }

        ifSetText(bank_components.bankpin_keypad_title, title)

        // Update digit hint (shows how many digits entered)
        val digitsEntered =
            when (progress) {
                0 -> 0
                else -> (progress % 10).coerceIn(0, 4)
            }
        val hint = "*".repeat(digitsEntered) + "_".repeat(4 - digitsEntered)
        ifSetText(bank_components.bankpin_keypad_digithint, hint)
    }

    private fun Player.enterPinDigit(digit: Int) {
        val progress = pinEntryProgress

        // Store digit in temporary buffer
        val currentEntry = pinEntryBuffer
        pinEntryBuffer = currentEntry * 10 + digit
        pinEntryProgress = progress + 1

        if (pinEntryProgress >= 4) {
            // PIN entry complete
            processPinEntry()
        } else {
            updateKeypadInterface()
        }
    }

    private fun Player.processPinEntry() {
        val mode = PinMode.entries.getOrElse(pinEntryMode) { PinMode.VERIFY }
        val enteredPin = pinEntryBuffer

        when (mode) {
            PinMode.SET -> {
                val pending = pendingPin
                if (pending == 0) {
                    // First entry
                    pendingPin = enteredPin
                    pinEntryProgress = 1
                    pinEntryBuffer = 0
                    updateKeypadInterface()
                } else if (pending == enteredPin) {
                    // Confirmed - set PIN
                    pinValue = enteredPin
                    pinState = 2 // ACTIVE
                    pinEntryProgress = 0
                    pinEntryBuffer = 0
                    pendingPin = 0
                    mes("Your Bank PIN has been set successfully.")
                    closePinInterfaces()
                } else {
                    // Mismatch
                    mes("The PINs you entered do not match. Please try again.")
                    pendingPin = 0
                    pinEntryProgress = 0
                    pinEntryBuffer = 0
                    updateKeypadInterface()
                }
            }
            PinMode.CHANGE -> {
                // TODO: Implement PIN change flow
                mes("PIN change feature coming soon.")
                closePinInterfaces()
            }
            PinMode.VERIFY -> {
                if (enteredPin == pinValue) {
                    pinVerified = 1
                    mes("PIN verified successfully.")
                    closePinInterfaces()
                } else {
                    mes("Incorrect PIN. Please try again.")
                    pinEntryProgress = 0
                    pinEntryBuffer = 0
                    updateKeypadInterface()
                }
            }
            PinMode.DELETE -> {
                if (enteredPin == pinValue) {
                    // Clear PIN
                    pinValue = 0
                    pinState = 0 // NO_PIN
                    mes("Your Bank PIN has been deleted.")
                    closePinInterfaces()
                } else {
                    mes("Incorrect PIN. Please try again.")
                    pinEntryProgress = 0
                    pinEntryBuffer = 0
                    updateKeypadInterface()
                }
            }
        }
    }

    private fun Player.closePinInterfaces() {
        // Close keypad and return to settings
        ifClose(eventBus)
    }

    private fun Player.setPinRecoveryDelay(days: Int) {
        recoveryDelay = days
        mes("Your recovery delay has been set to $days days.")
        updatePinSettingsInterface()
    }

    private fun Player.requestPinDeletion() {
        pinEntryMode = PinMode.DELETE.ordinal
        pinEntryProgress = 0
        pinEntryBuffer = 0
        ifOpenMainModal(bank_interfaces.bankpin_keypad, eventBus = eventBus)
        updateKeypadInterface()
    }

    private fun Player.cancelPendingPin() {
        pendingPin = 0
        pinState = 0 // NO_PIN
        mes("Your pending Bank PIN has been cancelled.")
        updatePinSettingsInterface()
    }

    /** Check if player needs to enter PIN before accessing bank. */
    fun requiresPinEntry(player: Player): Boolean {
        return player.pinState == 2 && player.pinVerified == 0
    }

    /** Open PIN keypad for verification before bank access. */
    fun openPinVerification(player: Player) {
        player.pinEntryMode = PinMode.VERIFY.ordinal
        player.pinEntryProgress = 0
        player.pinEntryBuffer = 0
        player.ifOpenMainModal(bank_interfaces.bankpin_keypad, eventBus = eventBus)
        player.updateKeypadInterface()
    }

    /** Reset PIN verification state (call on logout). */
    fun resetPinVerification(player: Player) {
        player.pinVerified = 0
    }

    private enum class PinMode {
        SET,
        CHANGE,
        VERIFY,
        DELETE,
    }
}

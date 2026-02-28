package org.rsmod.api.player.worn

import org.rsmod.api.config.refs.objs
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.isAnyType
import org.rsmod.game.inv.isType

public object EquipmentChecks {
    public fun isSmokeStaff(obj: InvObj?): Boolean =
        obj.isAnyType(objs.smoke_battlestaff, objs.mystic_smoke_staff)

    public fun isSoulreaperAxe(obj: InvObj?): Boolean = obj.isType(objs.soulreaper_axe)

    public fun isTumekensShadow(obj: InvObj?): Boolean = false

    public fun isTwistedBow(obj: InvObj?): Boolean = obj.isType(objs.twisted_bow)

    public fun isDragonHunterCrossbow(obj: InvObj?): Boolean = false

    public fun isCrystalBow(obj: InvObj?): Boolean = false

    public fun isCrystalHelm(obj: InvObj?): Boolean = false

    public fun isCrystalBody(obj: InvObj?): Boolean = false

    public fun isCrystalLegs(obj: InvObj?): Boolean = false

    public fun isObsidianSet(helm: InvObj?, top: InvObj?, legs: InvObj?): Boolean = false

    public fun isVirtusMask(obj: InvObj?): Boolean = false

    public fun isVirtusRobeTop(obj: InvObj?): Boolean = false

    public fun isVirtusRobeBottom(obj: InvObj?): Boolean = false

    public fun isVoidMeleeHelm(obj: InvObj?): Boolean = false

    public fun isVoidRangerHelm(obj: InvObj?): Boolean = false

    public fun isVoidMageHelm(obj: InvObj?): Boolean = false

    public fun isVoidTop(obj: InvObj?): Boolean = isRegularVoidTop(obj) || isEliteVoidTop(obj)

    public fun isRegularVoidTop(obj: InvObj?): Boolean = false

    public fun isEliteVoidTop(obj: InvObj?): Boolean = false

    public fun isVoidRobe(obj: InvObj?): Boolean = isRegularVoidRobe(obj) || isEliteVoidRobe(obj)

    public fun isRegularVoidRobe(obj: InvObj?): Boolean = false

    public fun isEliteVoidRobe(obj: InvObj?): Boolean = false

    public fun isVoidGloves(obj: InvObj?): Boolean = false

    public fun isDharokSet(helm: InvObj?, top: InvObj?, legs: InvObj?, weapon: InvObj?): Boolean =
        helm.isAnyType(
            objs.dharoks_helm_100,
            objs.dharoks_helm_75,
            objs.dharoks_helm_50,
            objs.dharoks_helm_25,
        ) &&
            top.isAnyType(
                objs.dharoks_platebody_100,
                objs.dharoks_platebody_75,
                objs.dharoks_platebody_50,
                objs.dharoks_platebody_25,
            ) &&
            legs.isAnyType(
                objs.dharoks_platelegs_100,
                objs.dharoks_platelegs_75,
                objs.dharoks_platelegs_50,
                objs.dharoks_platelegs_25,
            ) &&
            weapon.isAnyType(
                objs.dharoks_greataxe_100,
                objs.dharoks_greataxe_75,
                objs.dharoks_greataxe_50,
                objs.dharoks_greataxe_25,
            )

    public fun isToragSet(helm: InvObj?, top: InvObj?, legs: InvObj?, weapon: InvObj?): Boolean =
        helm.isAnyType(
            objs.torags_helm_100,
            objs.torags_helm_75,
            objs.torags_helm_50,
            objs.torags_helm_25,
        ) &&
            top.isAnyType(
                objs.torags_platebody_100,
                objs.torags_platebody_75,
                objs.torags_platebody_50,
                objs.torags_platebody_25,
            ) &&
            legs.isAnyType(
                objs.torags_platelegs_100,
                objs.torags_platelegs_75,
                objs.torags_platelegs_50,
                objs.torags_platelegs_25,
            ) &&
            weapon.isAnyType(
                objs.torags_hammers_100,
                objs.torags_hammers_75,
                objs.torags_hammers_50,
                objs.torags_hammers_25,
            )

    public fun isAhrimSet(helm: InvObj?, top: InvObj?, legs: InvObj?, weapon: InvObj?): Boolean =
        helm.isAnyType(
            objs.ahrims_hood_100,
            objs.ahrims_hood_75,
            objs.ahrims_hood_50,
            objs.ahrims_hood_25,
        ) &&
            top.isAnyType(
                objs.ahrims_robetop_100,
                objs.ahrims_robetop_75,
                objs.ahrims_robetop_50,
                objs.ahrims_robetop_25,
            ) &&
            legs.isAnyType(
                objs.ahrims_robeskirt_100,
                objs.ahrims_robeskirt_75,
                objs.ahrims_robeskirt_50,
                objs.ahrims_robeskirt_25,
            ) &&
            weapon.isAnyType(
                objs.ahrims_staff_100,
                objs.ahrims_staff_75,
                objs.ahrims_staff_50,
                objs.ahrims_staff_25,
            )

    public fun isJusticiarSet(helm: InvObj?, top: InvObj?, legs: InvObj?): Boolean = false
}

package org.rsmod.api.npc.interact

import jakarta.inject.Inject
import org.rsmod.api.npc.events.interact.AiObjContentEvents
import org.rsmod.api.npc.events.interact.AiObjDefaultEvents
import org.rsmod.api.npc.events.interact.AiObjEvents
import org.rsmod.api.npc.events.interact.ApEvent
import org.rsmod.api.npc.events.interact.OpEvent
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionObj
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType

public class AiObjInteractions
@Inject
constructor(private val objTypes: ObjTypeList, private val eventBus: EventBus) {
    public fun interactOp(
        npc: Npc,
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ) {
        val opTrigger = hasOpTrigger(obj, op, type)
        val interaction =
            InteractionObj(target = obj, op = op, hasOpTrigger = opTrigger, hasApTrigger = false)
        npc.interaction = interaction
        npc.walk(obj.coords)
    }

    public fun interactAp(npc: Npc, obj: Obj, op: InteractionOp) {
        val apRange = npc.visType.attackRange
        val interaction =
            InteractionObj(
                target = obj,
                op = op,
                hasOpTrigger = false,
                hasApTrigger = true,
                startApRange = apRange,
            )
        npc.interaction = interaction
        npc.walk(obj.coords)
    }

    public fun opTrigger(
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ): OpEvent? {
        val typeEvent = obj.toOp(op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = obj.toContentOp(type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val defaultEvent = obj.toDefaultOp(op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasOpTrigger(
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ): Boolean = opTrigger(obj, op, type) != null

    public fun apTrigger(
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ): ApEvent? {
        val typeEvent = obj.toAp(op)
        if (eventBus.contains(typeEvent::class.java, type.id)) {
            return typeEvent
        }

        val contentEvent = obj.toContentAp(type.contentGroup, op)
        if (eventBus.contains(contentEvent::class.java, type.contentGroup)) {
            return contentEvent
        }

        val defaultEvent = obj.toDefaultAp(op)
        if (eventBus.contains(defaultEvent::class.java, defaultEvent.id)) {
            return defaultEvent
        }

        return null
    }

    public fun hasApTrigger(
        obj: Obj,
        op: InteractionOp,
        type: UnpackedObjType = objTypes[obj],
    ): Boolean = apTrigger(obj, op, type) != null

    private fun Obj.toOp(op: InteractionOp): AiObjEvents.Op =
        when (op) {
            InteractionOp.Op1 -> AiObjEvents.Op1(this)
            InteractionOp.Op2 -> AiObjEvents.Op2(this)
            InteractionOp.Op3 -> AiObjEvents.Op3(this)
            InteractionOp.Op4 -> AiObjEvents.Op4(this)
            InteractionOp.Op5 -> AiObjEvents.Op5(this)
            InteractionOp.Op6 -> AiObjEvents.Op6(this)
            InteractionOp.Op7 -> AiObjEvents.Op7(this)
            InteractionOp.Op8 -> AiObjEvents.Op8(this)
            InteractionOp.Op9 -> AiObjEvents.Op9(this)
            InteractionOp.Op10 -> AiObjEvents.Op10(this)
        }

    private fun Obj.toContentOp(contentGroup: Int, op: InteractionOp): AiObjContentEvents.Op =
        when (op) {
            InteractionOp.Op1 -> AiObjContentEvents.Op1(this, contentGroup)
            InteractionOp.Op2 -> AiObjContentEvents.Op2(this, contentGroup)
            InteractionOp.Op3 -> AiObjContentEvents.Op3(this, contentGroup)
            InteractionOp.Op4 -> AiObjContentEvents.Op4(this, contentGroup)
            InteractionOp.Op5 -> AiObjContentEvents.Op5(this, contentGroup)
            InteractionOp.Op6 -> AiObjContentEvents.Op6(this, contentGroup)
            InteractionOp.Op7 -> AiObjContentEvents.Op7(this, contentGroup)
            InteractionOp.Op8 -> AiObjContentEvents.Op8(this, contentGroup)
            InteractionOp.Op9 -> AiObjContentEvents.Op9(this, contentGroup)
            InteractionOp.Op10 -> AiObjContentEvents.Op10(this, contentGroup)
        }

    private fun Obj.toDefaultOp(op: InteractionOp): AiObjDefaultEvents.Op =
        when (op) {
            InteractionOp.Op1 -> AiObjDefaultEvents.Op1(this)
            InteractionOp.Op2 -> AiObjDefaultEvents.Op2(this)
            InteractionOp.Op3 -> AiObjDefaultEvents.Op3(this)
            InteractionOp.Op4 -> AiObjDefaultEvents.Op4(this)
            InteractionOp.Op5 -> AiObjDefaultEvents.Op5(this)
            InteractionOp.Op6 -> AiObjDefaultEvents.Op6(this)
            InteractionOp.Op7 -> AiObjDefaultEvents.Op7(this)
            InteractionOp.Op8 -> AiObjDefaultEvents.Op8(this)
            InteractionOp.Op9 -> AiObjDefaultEvents.Op9(this)
            InteractionOp.Op10 -> AiObjDefaultEvents.Op10(this)
        }

    private fun Obj.toAp(op: InteractionOp): AiObjEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> AiObjEvents.Ap1(this)
            InteractionOp.Op2 -> AiObjEvents.Ap2(this)
            InteractionOp.Op3 -> AiObjEvents.Ap3(this)
            InteractionOp.Op4 -> AiObjEvents.Ap4(this)
            InteractionOp.Op5 -> AiObjEvents.Ap5(this)
            InteractionOp.Op6 -> AiObjEvents.Ap6(this)
            InteractionOp.Op7 -> AiObjEvents.Ap7(this)
            InteractionOp.Op8 -> AiObjEvents.Ap8(this)
            InteractionOp.Op9 -> AiObjEvents.Ap9(this)
            InteractionOp.Op10 -> AiObjEvents.Ap10(this)
        }

    private fun Obj.toContentAp(contentGroup: Int, op: InteractionOp): AiObjContentEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> AiObjContentEvents.Ap1(this, contentGroup)
            InteractionOp.Op2 -> AiObjContentEvents.Ap2(this, contentGroup)
            InteractionOp.Op3 -> AiObjContentEvents.Ap3(this, contentGroup)
            InteractionOp.Op4 -> AiObjContentEvents.Ap4(this, contentGroup)
            InteractionOp.Op5 -> AiObjContentEvents.Ap5(this, contentGroup)
            InteractionOp.Op6 -> AiObjContentEvents.Ap6(this, contentGroup)
            InteractionOp.Op7 -> AiObjContentEvents.Ap7(this, contentGroup)
            InteractionOp.Op8 -> AiObjContentEvents.Ap8(this, contentGroup)
            InteractionOp.Op9 -> AiObjContentEvents.Ap9(this, contentGroup)
            InteractionOp.Op10 -> AiObjContentEvents.Ap10(this, contentGroup)
        }

    private fun Obj.toDefaultAp(op: InteractionOp): AiObjDefaultEvents.Ap =
        when (op) {
            InteractionOp.Op1 -> AiObjDefaultEvents.Ap1(this)
            InteractionOp.Op2 -> AiObjDefaultEvents.Ap2(this)
            InteractionOp.Op3 -> AiObjDefaultEvents.Ap3(this)
            InteractionOp.Op4 -> AiObjDefaultEvents.Ap4(this)
            InteractionOp.Op5 -> AiObjDefaultEvents.Ap5(this)
            InteractionOp.Op6 -> AiObjDefaultEvents.Ap6(this)
            InteractionOp.Op7 -> AiObjDefaultEvents.Ap7(this)
            InteractionOp.Op8 -> AiObjDefaultEvents.Ap8(this)
            InteractionOp.Op9 -> AiObjDefaultEvents.Ap9(this)
            InteractionOp.Op10 -> AiObjDefaultEvents.Ap10(this)
        }
}

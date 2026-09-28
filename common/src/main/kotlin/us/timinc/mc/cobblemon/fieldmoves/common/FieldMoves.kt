package us.timinc.mc.cobblemon.fieldmoves.common

import com.cobblemon.mod.common.api.spawning.spawner.FishingSpawnerFactory
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory
import us.timinc.mc.cobblemon.fieldmoves.common.influences.CuteCharm
import us.timinc.mc.cobblemon.fieldmoves.common.influences.SynchronizedNature
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod

const val MOD_ID = "cobblemon_field_moves"

object FieldMoves : AbstractMod<FieldMoves.Config>(MOD_ID, Config::class.java) {
    class Config : AbstractConfig() {
        // todo: dlt responsibility, pull up the discussion at 22:00 27-09-2026 your time in contributors
        val addDropsToBundles: Boolean = false
        val mustBeFirst: Boolean = true
        val cuteCharmChance: Double = 0.66
        val synchronizeChance: Double = 1.0
    }

    init {
        PlayerSpawnerFactory.influenceBuilders.addAll(listOf(::CuteCharm, ::SynchronizedNature))
        FishingSpawnerFactory.positionInfluenceBuilders.add { ctx ->
            listOf(
                CuteCharm(ctx.player),
                SynchronizedNature(ctx.player)
            )
        }
    }
}

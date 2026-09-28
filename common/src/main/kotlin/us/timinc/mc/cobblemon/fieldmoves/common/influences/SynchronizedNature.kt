package us.timinc.mc.cobblemon.fieldmoves.common.influences

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.pokemon.Nature
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.fieldmoves.common.FieldMoves
import kotlin.random.Random.Default.nextDouble

class SynchronizedNature(val player: ServerPlayer) : SpawningInfluence {
    companion object {
        const val ABILITY = "synchronize"
    }

    override fun affectAction(action: SpawnAction<*>) {
        if (action !is PokemonSpawnAction) return
        if (action.props.nature != null) return

        val nature = getSynchronizedNature(player)?.name?.path ?: return

        if (nextDouble() < FieldMoves.config.synchronizeChance) {
            FieldMoves.debugger.debug("Setting wild ${action.props.species} to nature $nature")
            action.props.nature = nature
        }
    }

    private fun getSynchronizedNature(player: ServerPlayer): Nature? {
        val playerPartyStore = Cobblemon.storage.getParty(player)
        if (FieldMoves.config.mustBeFirst) {
            val firstPartyMember = playerPartyStore.firstOrNull()
            if (firstPartyMember?.ability?.name != ABILITY) {
                FieldMoves.debugger.debug("First party member does not have synchronize for ${player.name.string}")
                return null
            }
            return firstPartyMember.nature
        }

        val synchronizePartyMember = playerPartyStore.find { it.ability.name == ABILITY }
        if (synchronizePartyMember == null) {
            FieldMoves.debugger.debug("No party member has synchronize for ${player.name.string}")
            return null
        }

        FieldMoves.debugger.debug(
            "${player.name.string}'s ${synchronizePartyMember.species.name} has synchronize and nature ${synchronizePartyMember.nature}",
        )
        return synchronizePartyMember.nature
    }
}

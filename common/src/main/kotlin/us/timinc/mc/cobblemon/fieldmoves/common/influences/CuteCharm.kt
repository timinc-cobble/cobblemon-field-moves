package us.timinc.mc.cobblemon.fieldmoves.common.influences

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.pokemon.Gender
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.fieldmoves.common.FieldMoves
import kotlin.random.Random.Default.nextDouble

class CuteCharm(val player: ServerPlayer) : SpawningInfluence {
    companion object {
        const val ABILITY = "cutecharm"

        val GENDER_BENDER =
            mapOf(
                Gender.MALE to Gender.FEMALE,
                Gender.FEMALE to Gender.MALE,
            )
    }

    override fun affectAction(action: SpawnAction<*>) {
        if (action !is PokemonSpawnAction) return
        if (action.props.gender != null) return

        val gender = getCuteCharmGender(player) ?: return
        val invertedGender =
            GENDER_BENDER[gender] ?: run {
                FieldMoves.debugger.debug("Gender $gender doesn't have an opposite, cute charm has no effect")
                return
            }

        val species = action.props.species?.let(PokemonSpecies::getByName) ?: return
        if (species.maleRatio == 1F || species.maleRatio == 0F) {
            FieldMoves.debugger.debug("Spawning species of $species has a fixed gender, cute charm can't affect")
            return
        }

        if (nextDouble() >= FieldMoves.config.cuteCharmChance) {
            FieldMoves.debugger.debug("Rolled for cute charm but missed.")
            return
        }

        FieldMoves.debugger.debug("Setting wild ${action.props.species} to gender $invertedGender")
        action.props.gender = invertedGender
    }

    private fun getCuteCharmGender(player: ServerPlayer): Gender? {
        val playerPartyStore = Cobblemon.storage.getParty(player)
        if (FieldMoves.config.mustBeFirst) {
            val firstPartyMember = playerPartyStore.firstOrNull()
            if (firstPartyMember?.ability?.name != ABILITY) {
                FieldMoves.debugger.debug("First party member does not have cute charm for ${player.name.string}")
                return null
            }
            return firstPartyMember.gender
        }

        val cuteCharmPartyMember = playerPartyStore.find { it.ability.name == ABILITY }
        if (cuteCharmPartyMember == null) {
            FieldMoves.debugger.debug("No party member has cute charm for ${player.name.string}")
            return null
        }

        FieldMoves.debugger.debug(
            "${player.name.string}'s ${cuteCharmPartyMember.species.name} has cute charm and gender ${cuteCharmPartyMember.gender}",
        )
        return cuteCharmPartyMember.gender
    }
}

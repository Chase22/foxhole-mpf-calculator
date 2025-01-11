import de.chasenet.foxhole.model.Faction
import de.chasenet.foxhole.model.ItemCategory
import kotlinx.browser.localStorage

private const val PLAYER_FACTION = "player-faction"

private fun selectedItemKey(category: ItemCategory) = "selected-item-${category.name}"

fun getSavedSelectedItem(category: ItemCategory): String = localStorage.getItem(selectedItemKey(category)) ?: ""

fun saveSelectedItem(
    category: ItemCategory,
    itemName: String,
) {
    localStorage.setItem(selectedItemKey(category), itemName)
}

fun getPlayerFaction(): Faction = localStorage.getItem(PLAYER_FACTION)?.let(Faction::valueOf) ?: Faction.colonial

fun savePlayerFaction(faction: Faction) {
    localStorage.setItem(PLAYER_FACTION, faction.name)
}

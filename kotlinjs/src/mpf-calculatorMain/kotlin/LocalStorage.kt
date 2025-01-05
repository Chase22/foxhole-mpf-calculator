import de.chasenet.foxhole.model.ItemCategory
import kotlinx.browser.localStorage

private fun selectedItemKey(category: ItemCategory) = "selected-item-${category.name}"

fun getSavedSelectedItem(category: ItemCategory): String = localStorage.getItem(selectedItemKey(category)) ?: ""

fun saveSelectedItem(
    category: ItemCategory,
    itemName: String,
) {
    localStorage.setItem(selectedItemKey(category), itemName)
}

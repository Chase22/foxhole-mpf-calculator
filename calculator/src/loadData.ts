import {Item, LogiItem, mapLogiItemToItem} from "./Models";
// @ts-ignore
import foxhole from './foxhole.json'

function loadData(): ReadonlyArray<Item> {
    return (foxhole as ReadonlyArray<LogiItem>).map(mapLogiItemToItem).filter(item => item.isMpfCraftable)
}

export default loadData
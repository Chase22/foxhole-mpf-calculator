export function also<T>(value: T, body: (obj: T) => void): T {
    body(value)
    return value
}


export function createElement<K extends keyof HTMLElementTagNameMap>(tagName: K, block: (elem: HTMLElementTagNameMap[K]) => void): HTMLElementTagNameMap[K] {
    return also(document.createElement(tagName), block)
}

export function appendChild<K extends keyof HTMLElementTagNameMap, T extends Node>(element: T, tagName: K, block: (elem: HTMLElementTagNameMap[K]) => void): T {
    element.appendChild(createElement(tagName, block))
    return element
}
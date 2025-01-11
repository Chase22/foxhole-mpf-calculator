import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLCollection
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

fun <T> Element.addEventFlow(
    type: String,
    mapper: (Event) -> T,
): Flow<T> =
    callbackFlow {
        val callback =
            EventListener {
                launch {
                    send(mapper(it))
                }
            }

        addEventListener(type, callback)

        awaitClose { removeEventListener(type, callback) }
    }

fun <T, R> Iterable<T>.unsafeMap() = map { it.unsafeCast<R>() }

inline fun <R> HTMLCollection.asListOf() = asList().unsafeMap<Element, R>()

fun <T : HTMLElement> Document.getElementById(id: String): T? = getElementById(id)?.unsafeCast<T>()

fun <T : HTMLElement> Document.getElementsByClassName(className: String): List<T> = getElementsByClassName(className).asListOf<T>()

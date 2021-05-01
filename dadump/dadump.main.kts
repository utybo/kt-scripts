@file:CompilerOptions("-jvm-target", "11") // Required for Path.of
@file:DependsOn("org.jsoup:jsoup:1.13.1")

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

val initialUrl = args[0]

/**
 * Selector for the main <img> tag.
 */
val imgSelector = "._1izoQ"

/**
 * Selector for the title
 */
val titleSelector = "._2p6cd"

/**
 * Selector for the author
 */
val authorSelector = ".g5Yvc"

/**
 * Regex used to match any substring of <a> tag's inner text
 */
val nextRegex = Regex("NEXT|Next|next")

/**
 * Regex used to match text right before <a> tags
 */
val lenientNextRegex = Regex("(NEXT|Next|next).{0,10}$")

/**
 * Expected prefixes for the next <a> hrefs
 */
val nextUrlPrefix: List<String> = listOf("http://fav.me/", "https://www.deviantart.com/")

/**
 * Time (in milliseconds) to wait between each GET + image download.
 */
val timeBetweenImages = 500L

val outputDir = "downloaded"


Files.createDirectories(Path.of(outputDir))

process(initialUrl)

fun process(initialUrl: String) {
    val queue: Queue<String> = LinkedList()
    queue.add(initialUrl)
    var counter = 0
    while (queue.isNotEmpty()) {
        val url = queue.remove()
        val doc = Jsoup.connect(url).get()

        val title = doc.select(titleSelector)[0].text()
        println("Title: $title")

        val author = doc.select(authorSelector)[0].text()
        println("    Author: $author")

        val imageUrl = doc.select(imgSelector)[0].attr("src")
        if (imageUrl == null || imageUrl.isEmpty())
            error("Failed to find or load image URL")

        val next = doc.findNextUrl()
        println("    Next URL: $next")

        downloadToOutput("${counter++}_${title.sanitize()}", imageUrl)

        if (next != null)
            queue.add(next)
        Thread.sleep(timeBetweenImages)
    }
}

fun isPotentialNextUrl(elt: Element): Boolean =
    nextUrlPrefix.any { elt.attr("href").startsWith(it) } && elt.text().contains(nextRegex)

fun Document.findNextUrl(): String? {
    // Easiest case: <a> tag with NEXT|Next|next in it
    val url = select("a")
        .firstOrNull { isPotentialNextUrl(it) }
        ?.attr("href")
    if (url != null)
        return url

    // Harder case: a link with a NEXT|Next|next text somewhere behind it
    val lonelyUrl = select("a")
        .filter { nextUrlPrefix.any { prefix -> it.attr("href").startsWith(prefix) } }
        .firstOrNull {
            // Explicit type declarations, these can be null if not found
            val prevElt: Element? = it.previousElementSibling()
            val prevNode: Node? = it.previousSibling()
            // println("prevElt: $prevElt ||| prevNode: $prevNode")
            (prevElt != null && prevElt.hasText() && prevElt.text().contains(lenientNextRegex)) ||
                    (prevNode is TextNode && prevNode.text().contains(lenientNextRegex))

        }?.attr("href")
    return lonelyUrl
}

fun String.sanitize(): String {
    // Non alphanumeric characters which are allowed in file names
    val allowedSpecialCharacters = setOf('.', '-', '(', ')', '_', ' ')
    return this.map {
        if (it in 'a'..'z' || it in 'A'..'Z' || it in '0'..'9' || it in allowedSpecialCharacters) it
        else '_'
    }.joinToString("")
}

val URI.extension: String
    get() {
        path.substringAfterLast('/').let { remoteName ->
            val index = remoteName.indexOf('.')
            if (index == -1)
                return ""
            return remoteName.substringAfterLast('.')
        }
    }

fun downloadToOutput(fileNameBase: String, urlString: String) {
    println("    Downloading -> $fileNameBase")
    val url = URI(urlString)
    val extension = url.extension
    val fileName = fileNameBase + (if (extension.isNotEmpty()) ".$extension" else "")
    val res =
        HttpClient.newHttpClient().send(HttpRequest.newBuilder(url).build(), HttpResponse.BodyHandlers.ofByteArray())
    Files.write(Path.of(outputDir, fileName), res.body())
    println("    OK!")
}

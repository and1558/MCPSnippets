package com.tristian.changelog.gui

import com.google.common.collect.Queues
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.lang.IllegalStateException
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.regex.Pattern


/**
 *
 * @author Tristian
 * im trying to keep this all in one class so that you guys dont have to download multiple..
 *
 */
class Changelog {


    companion object Constants {

        /**
         * What opacity should the background of the changelog be?
         */
        const val CHANGELOG_BACKGROUND_OPACITY: Float = 1.0f


        /**
         * What color should each header title's text be?
         * (the headers are the day entries)
         */
        const val CHANGELOG_HEADER_COLOR: Int = 0x00000


        /**
         * What should be the color of the day's text?
         */
        const val CHANGELOG_ENTRY_TEXT_COLOR: Int = 0x00000

        /**
         * Padding left of the changelog entries
         * @see ChangelogEntry.getFormattedContents
         */
        const val CHANGELOG_ENTRY_PADDING_LEFT: Int = 2


        /**
         * @see URL
         * Change me. you can figure it out yourself how urls work in this programming language.. You can find an example file in the repo tho..
         * This can either be a file path, or a github commit api url
         * (i.e https://api.github.com/repos/chrislgarry/Apollo-11/commits)
         */
        val CHANGELOG_URL = URL("https://raw.githubusercontent.com/TrigonometricDev/MCPSnippets/main/src/main/kotlin/com/tristian/changelog/changelog.json")


        /**
         * @see ChangelogEntry
         * @see ChangelogMode
         * What mode do you want your changelog to be formatted in.
         */
        val CHANGELOG_MODE = ChangelogMode.BULLETS


        //============================DO NOT CHANGE BEYOND THIS POINT OR NOT WORKING!11============================

        /**
         * The filetypes that this changelog will support reading.
         */
        val SUPPORTED_FILE_EXTENSIONS = listOf("yml", "json")

        /**
         * Used to detect whether or not it's a github api url that needs to be read.
         */
        private val GH_API_PATTERN = Regex("(https?:\\/\\/)?api\\.github\\.com\\/repos\\/.+\\/.+\\/commits")

    }

    /**
     * Changelog contents
     * holds all of the entries and their datas.
     */
    private val changelogContents: ConcurrentLinkedQueue<ChangelogEntry> = Queues.newConcurrentLinkedQueue<ChangelogEntry>()


    fun render() {

    }


    init {

        val path = CHANGELOG_URL.path
        val flag = path.matches(GH_API_PATTERN)

        println(path)
        /**
         * The file extension doesn't matter if we're reading from a github api.
         */
        var ext = ""
        assert(SUPPORTED_FILE_EXTENSIONS.any {
            run {
                // kill two birds with one stone
                if (path.endsWith(it)) {
                    ext = it
                    return@run true
                }
                false
            }
        } || flag /* guess it's github.. */)

        when (ext) {
            "json" -> readChangelogAsJson() // issa json file
            "yml" -> TODO("lazy")
            else -> throw IllegalStateException("No file type found. And no path found.")
        }
    }

    private fun readChangelogAsJson() {

        val jsonElement: JsonElement? = let {
            try {
                return@let JsonParser().parse(JsonReader(run {
                    try {

                        /**
                         * Return the thing with the thing opened in a thing streamer thinger.
                         */
                        return@run CHANGELOG_URL.openStream().reader()
                    } catch (exception: IOException) {
                        error("Your url is wrong.")
                    }
                }))
            } catch (exception: IllegalStateException) {
                println("your json sucks")
            }
            null
        }
        jsonElement?.asJsonObject.runCatching {
            this?.entrySet()?.forEach {
                this@Changelog.changelogContents.addAll(
                    listOf(ChangelogEntry(
                        it.key,
                        it.value.asJsonArray.map { ele ->
                            ele.asString
                        })))
            }
        }
            .onFailure { println("Your json is invalid. ") }
            .onSuccess { println("Read your changelog's contents (here they are): ${this.changelogContents}") }

    }

    /**
     * Changelog entry
     *
     * @property title The header title of this changelog entry.
     * @property contents all of the contents.
     * @constructor Create changelog entry.
     */
    data class ChangelogEntry(val title: String, val contents: Collection<String>) {

        /**
         * Get formatted contents for rendering in the changelog.
         * This turns the contents into a
         *
         * @return
         */
        fun getFormattedContents(): Collection<String> {
            return contents.map { "${CHANGELOG_MODE.delim} $it" }
        }

        override fun toString(): String {
            return "ChangelogEntry(title='$title', contents=$contents)"
        }

    }


    /**
     * The Changelog mode
     * @param delim The thing used to prefix a string of data.
     */
    enum class ChangelogMode(val delim: String) {
        BULLETS("\u2022"),  // •,
        DASHES("-"),        // what do you think
        ASTERISKS("*"),     // what do you think
        COMMAS(",")         // eh
    }
}

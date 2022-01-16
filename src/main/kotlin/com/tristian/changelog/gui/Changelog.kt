package com.tristian.changelog.gui

import com.google.common.collect.Queues
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue


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
        val CHANGELOG_HEADER_COLOR: Int = java.awt.Color.red.rgb


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
        private val SUPPORTED_FILE_EXTENSIONS = listOf("yml", "json")

        /**
         * Used to detect whether or not it's a github api url that needs to be read.
         */
        private val GH_API_PATTERN = Regex("(https?:\\/\\/)?api\\.github\\.com\\/repos\\/.+\\/.+\\/commits")

        // mc stuffs
        private val mc: Minecraft = Minecraft.getMinecraft()
        private val fr: FontRenderer = mc.fontRenderer

    }

    /**
     * Changelog contents
     * holds all of the entries and their datas.
     */
    private val changelogContents: ConcurrentLinkedQueue<ChangelogEntry> = Queues.newConcurrentLinkedQueue<ChangelogEntry>()


    fun render(x: Int, y: Int) {

        // todo turn this into O(n) (or better) instead of n^2
        var sum = 1

//        todo make the buttons.
        changelogContents.forEachIndexed { index, it ->

            val title = it.title
            println(title)
            GlStateManager.disableBlend()
            GlStateManager.pushMatrix()
            GlStateManager.translate(0.0, 0.0, 0.0)
            val titleY = y + (sum * fr.FONT_HEIGHT) + fr.FONT_HEIGHT // give it some leeway
            fr.drawString(title, x, titleY, CHANGELOG_HEADER_COLOR)
            it.getFormattedContents().forEachIndexed { ind, s ->
                fr.drawString(s, x, titleY + (ind * fr.FONT_HEIGHT) + fr.FONT_HEIGHT, CHANGELOG_ENTRY_TEXT_COLOR)
                ++sum // keep track of how many times we render something, so that we can adjust our height.
                // make sure we adjust.
            }
            GlStateManager.popMatrix()
            GlStateManager.enableBlend()
            ++sum
        }
//        finally render a backwards and forwards button.

    }


    init {

        val path = CHANGELOG_URL.path
        val flag = path.matches(GH_API_PATTERN)

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

    /**
     * Read changelog as json.
     * Take some json, we convert it to an objects,
     * we take the keys and values that they have (titles and entries)
     * and then shove them up our queue.
     *
     */
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
        /**
         * put it into our queue.
         */
        jsonElement?.asJsonArray.runCatching {
            this?.get(0)?.asJsonObject?.entrySet()?.forEach {
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
         * This turns the contents into a big fat nice list.
         * also the leftpad doesnt work for some reason
         * @return the contents but nicer
         */
        fun getFormattedContents(): Collection<String> {
            return contents.map { StringUtils.leftPad("${CHANGELOG_MODE.delim} $it", CHANGELOG_ENTRY_PADDING_LEFT) }
        }

        override fun toString(): String {
            return "ChangelogEntry(title='$title', contents=$contents, formattedContents=${getFormattedContents()}})"
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

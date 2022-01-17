package com.tristian.changelog.gui

import com.google.common.collect.Queues
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.IOException
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min


/**
 *
 * @author Tristian
 * im trying to keep this all in one class so that you guys dont have to download multiple..
 *
 */
class Changelog {


    companion object Constants {

        /**
         * What color should the background of the changelog be?
         */
        val CHANGELOG_BACKGROUND_COLOR: Int = Color(255, 255, 255, 30).rgb


        /**
         * What color should each header title's text be?
         * (the headers are the day entries)
         */
        val CHANGELOG_HEADER_COLOR: Int = Color.blue.rgb


        /**
         * What should be the color of the day's text?
         */
        val CHANGELOG_ENTRY_TEXT_COLOR: Int = Color.yellow.rgb

        /**
         * Padding left of the changelog entries
         * @see ChangelogEntry.getFormattedContents
         */
        const val CHANGELOG_ENTRY_PADDING_LEFT: Int = 2


        /**
         * @see URL
         * Change me to adjust your changelog. you can figure it out yourself how urls work in this programming language.. You can find an example file in the repo tho..
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
         * The file types that this changelog will support reading.
         */
        private val SUPPORTED_FILE_EXTENSIONS = listOf("yml", "json")

        /**
         * Used to detect whether or not it's a github api url that needs to be read.
         */
        private val GH_API_PATTERN = Regex("(https?://)?api\\.github\\.com/repos/.+/.+/commits")

        /**
         * the width of the changelog will be decided by the biggest string width.
         */
        private var WIDTH: Int = 0


        // mc stuffs
        private val mc: Minecraft = Minecraft.getMinecraft()
        private val fr: FontRenderer = mc.fontRenderer

        /**
         * The height of the changelog will be decided by the given y of the render and the display height.
         */
        private var HEIGHT: Int = mc.displayHeight


        /**
         * Change in y, this is for scrolling stuff.
         */
        private var dy: Int = 0

    }

    /**
     * Changelog contents
     * holds all of the entries and their datas.
     */
    private val changelogContents: ConcurrentLinkedQueue<ChangelogEntry> = Queues.newConcurrentLinkedQueue<ChangelogEntry>()


    fun render(x: Int, y: Int) {

        dy -= Mouse.getEventDWheel()

        var sum = 1


        Gui.drawRect(x, y, x + WIDTH, y + HEIGHT, CHANGELOG_BACKGROUND_COLOR)

        // todo turn this into O(n) (or better) instead of n^2

        changelogContents.forEachIndexed { index, it ->

            val title = it.title

            var titleY = min(y + (sum * fr.FONT_HEIGHT),
                (y + (sum * fr.FONT_HEIGHT)) - dy)

            if (index == changelogContents.size - 1) titleY = max(y, titleY) // make sure there's always at least one thing visible.


            GlStateManager.pushMatrix()
            GlStateManager.disableBlend() //setup
            GlStateManager.translate(0.0, 0.0, 0.0)

            GL11.glEnable(GL11.GL_SCISSOR_TEST)

            // alow for scroll111
            scissorHelper(x, y, x + WIDTH, y + HEIGHT)

            // draw the header of the day.
            fr.drawString(title, x, titleY, CHANGELOG_HEADER_COLOR)

            it.getFormattedContents().forEachIndexed { ind, s ->
                val contentY = titleY + (ind * fr.FONT_HEIGHT) + fr.FONT_HEIGHT
                fr.drawString(s, x, contentY, CHANGELOG_ENTRY_TEXT_COLOR)
                ++sum // keep track of how many times we render something, so that we can adjust our y level..
            }


            // set uhh down
            GL11.glDisable(GL11.GL_SCISSOR_TEST)
            GlStateManager.popMatrix()
            GlStateManager.enableBlend()

            ++sum // just for good measure.
        }

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
                            WIDTH = max(WIDTH, fr.getStringWidth(ele.asString) + 8)
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
         * pads left based off of the given value to.
         * @return the contents but nicer
         */
        fun getFormattedContents(): Collection<String> {
            return contents.map { ("${" ".repeat(CHANGELOG_ENTRY_PADDING_LEFT)}${CHANGELOG_MODE.delim} $it") }
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

    /**
     * Scissor helper Function (from #user-made-snippets)
     *
     */
    fun scissorHelper(x1: Int, y1: Int, x2: Int, y2: Int) {
        var x2 = x2
        var y2 = y2
        x2 -= x1
        y2 -= y1
        val mc = Minecraft.getMinecraft()
        val resolution = ScaledResolution(mc)
        GL11.glScissor(x1 * resolution.scaleFactor,
            mc.displayHeight - y1 * resolution.scaleFactor - y2 * resolution.scaleFactor,
            x2 * resolution.scaleFactor,
            y2 * resolution.scaleFactor
        )
    }

}

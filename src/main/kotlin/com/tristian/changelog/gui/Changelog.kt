package com.tristian.changelog.gui

import java.net.URL


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
         * @see URL
         * Change me. you can figure it out yourself how urls work in this programming language.. You can find an example file in the repo tho..
         * This can either be a file path, or a github commit api url
         * (i.e https://api.github.com/repos/chrislgarry/Apollo-11/commits)
         */
        val CHANGELOG_URL = URL("https://raw.githubusercontent.com/TrigonometricDev/MCPSnippets/main/src/main/kotlin/com/tristian/changelog/changelog.json")

        /**
         * The filetypes that this changelog will support reading.
         */
        val SUPPORTED_FILE_EXTENSIONS = listOf("yml", "json")


        /**
         * @see ChangelogEntry
         * @see ChangelogMode
         * What mode do you want your changelog to be formatted in.
         */
        val CHANGELOG_MODE = ChangelogMode.BULLETS
    }

    fun render() {
    }


    init {
    }

    /**
     * Changelog entry
     *
     * @property title The header title of this changelog entry.
     * @property contents all of the contents.
     * @constructor Create changelog entry.
     */
    data class ChangelogEntry(val title: String, val contents: Collection<String>) {

        fun getFormattedContents() {

        }
    }

    /**
     * Changelog mode
     *
     * @constructor
     *
     * @param delim The thing used to prefix a string of data.
     */
    enum class ChangelogMode(delim: String) {
        BULLETS("\u2022"),  // •,
        DASHES("-"),        // what do you think
        ASTERISKS("*"),     // what do you think
        COMMAS(",")         // eh

    }
}

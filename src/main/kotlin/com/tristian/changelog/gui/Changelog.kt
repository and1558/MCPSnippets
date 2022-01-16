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
         * Change me. you can figure it out yourself.. You can find an example file in the repo tho..
         */
        val CHANGELOG_FILE_LOCATION = URL("https://github.io/jsonfile/whatever")

        /**
         * If you want to use the whole repository this works too...
         */
        var GIT_COMMIT_API_LINK = URL("https://api.github.com/repos/chrislgarry/Apollo-11/commits")


        /**
         * The filetypes that this changelog will support reading.
         */
        val SUPPORTED_FILE_EXTENSIONS = listOf("yml", "json")
    }

    fun render() {
    }


    init {
    }
}

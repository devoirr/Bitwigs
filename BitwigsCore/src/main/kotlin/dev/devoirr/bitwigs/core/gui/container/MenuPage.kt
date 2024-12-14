package dev.devoirr.bitwigs.core.gui.container

data class MenuPage(val list: MutableList<MenuButton> = mutableListOf(), var itemsPerPage: Int) {

    fun isFull() = list.size >= itemsPerPage

}

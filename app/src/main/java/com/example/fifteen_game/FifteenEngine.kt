package com.example.fifteen_game

interface FifteenEngine {
    fun transitionState(oldState: List<Int>, cell: Int): List<Int>
    fun isWin(state: List<Int>): Boolean
    fun getInitialState(): List<Int>
    fun isStepPossible(inputList: List<Int>, numberForMove: Int): Boolean

    companion object: FifteenEngine {
        val FINAL_STATE =  List(16){ it + 1 }

        override fun getInitialState(): List<Int> {
            var playingChips: List<Int>
            do {
                playingChips = List(16){ it + 1 }.toMutableList().shuffled()
            } while (!isSolvable(playingChips))
            return playingChips
        }

        private fun isSolvable(list: List<Int>): Boolean {
            val inversions = countInversions(list)
            val blankRow = blankRowFromBottom(list)
            return if (list.size % 2 == 0) {
                (inversions % 2 == 0) != (blankRow % 2 == 0)
            } else {
                inversions % 2 == 0
            }
        }

        private fun blankRowFromBottom(list: List<Int>): Int {
            val gridSize = 4
            val blankIndex = list.indexOf(0)
            return gridSize - (blankIndex / gridSize)
        }

        private fun countInversions(list: List<Int>): Int {
            var inversions = 0
            for (i in list.indices) {
                for (j in i + 1 until list.size) {
                    if (list[i] != 0 && list[j] != 0 && list[i] > list[j]) {
                        inversions++
                    }
                }
            }
            return inversions
        }

        override fun transitionState(oldState: List<Int>, cell: Int): List<Int> {
            if(isStepPossible(oldState, cell)) {
                val indexFromMove = oldState.indexOf(cell)
                val indexToMove = oldState.indexOf(16)
                val newState = oldState.toMutableList()
                newState[indexFromMove] = newState[indexToMove].also { newState[indexToMove] = newState[indexFromMove]}
                return newState.toList()
            } else {
                return oldState
            }

        }

        override fun isWin(playingChips: List<Int> ): Boolean {
            return playingChips == FINAL_STATE
        }

        override fun isStepPossible(inputList: List<Int>, numberForMove: Int): Boolean {
            val indexToMove = inputList.indexOf(numberForMove)
            val emptyPlaceIndex = inputList.indexOf(16)
            val neighbors = listOf(
                emptyPlaceIndex - 4,
                emptyPlaceIndex + 4,
                emptyPlaceIndex - 1,
                emptyPlaceIndex + 1
            ).filter { it in inputList.indices }
            return indexToMove in neighbors
        }
    }
}
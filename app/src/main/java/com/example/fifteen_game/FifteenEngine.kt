package com.example.fifteen_game

interface FifteenEngine {
    fun transitionState(oldState: List<Int>, cell: Int): List<Int>
    fun isWin(state: List<Int>): Boolean
    fun getInitialState(): List<Int>
    fun isStepPossible(inputList: List<Int>, numberForMove: Int): Boolean

    companion object : FifteenEngine {
        val FINAL_STATE = List(8) { it + 1 }  + 0 // [1, 2, 3, 4, 5, 6, 7, 8, 0]

        override fun getInitialState(): List<Int> {
            var playingChips: List<Int>
            do {
                playingChips = (List(8) { it + 1 } + 0 ).shuffled()  // Номера от 1 до 8 и 0
            } while (!isSolvable(playingChips))
            return playingChips
        }

        private fun isSolvable(list: List<Int>): Boolean {
            val inversions = countInversions(list)
            val blankRow = blankRowFromBottom(list)
            return inversions % 2 == 0
        }

        private fun blankRowFromBottom(list: List<Int>): Int {
            val gridSize = 3  // 3 для 3x3
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
            if (isStepPossible(oldState, cell)) {
                val indexFromMove = oldState.indexOf(cell)
                val indexToMove = oldState.indexOf(0)  // Пустое место обозначаем нулем
                val newState = oldState.toMutableList()
                newState[indexFromMove] = 0  // Пустое место
                newState[indexToMove] = cell  // Перемещаем ячейку
                return newState.toList()
            } else {
                return oldState  // Движение невозможно
            }
        }

        override fun isWin(playingChips: List<Int>): Boolean {
            return playingChips == FINAL_STATE
        }

        override fun isStepPossible(inputList: List<Int>, numberForMove: Int): Boolean {
            val indexToMove = inputList.indexOf(numberForMove)
            val emptyPlaceIndex = inputList.indexOf(0)  // Пустое место обозначаем нулем
            val neighbors = listOf(
                emptyPlaceIndex - 3,  // Сосед сверху
                emptyPlaceIndex + 3,  // Сосед снизу
                emptyPlaceIndex - 1,  // Сосед слева
                emptyPlaceIndex + 1   // Сосед справа
            ).filter { it in inputList.indices }
            return indexToMove in neighbors
        }
    }
}
package com.example.kifmatome

class Player(gameInfo: GameInfo){

    var tesu = 0

    var allBoard = mutableListOf<List<Int>>()
    var allBlackKomadai = mutableListOf<List<Int>>()
    var allWhiteKomadai = mutableListOf<List<Int>>()

    init {
        val board = arrayListOf<Int>(
            -2, -3, -4, -7, -8, -7, -4, -3, -2,
            0, -6, 0, 0, 0, 0, 0, -5, 0,
            -1, -1, -1, -1, -1, -1, -1, -1, -1,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 1, 1, 1, 1, 1,
            0, 5, 0, 0, 0, 0, 0, 6, 0,
            2, 3, 4, 7, 8, 7, 4, 3, 2
        )
        val blackKomadai = arrayListOf<Int>(0, 0, 0, 0, 0, 0, 0)
        val whiteKomadai = arrayListOf<Int>(0, 0, 0, 0, 0, 0, 0)

        allBoard.add(board.clone() as List<Int>)
        allBlackKomadai.add(blackKomadai.clone() as List<Int>)
        allWhiteKomadai.add(whiteKomadai.clone() as List<Int>)

        for(i in 1..gameInfo.moveList.size-1) {
            val nowPos = gameInfo.moveList.get(i).nowPos
            val nextpos = gameInfo.moveList.get(i).nextPos
            val piece = gameInfo.moveList.get(i).piece

            // 移動先に駒がある -> 駒台の値を増やす
            if(board[nextpos] != 0) {
                val idx = pieceToKomadaiIdx(board[nextpos])
                if(i%2 == 0) {
                    whiteKomadai[idx]++
                }else{
                    blackKomadai[idx]++
                }
            }

            // 打 -> 駒台の値を減らす
            if(nowPos == -1) {
                val idx = pieceToKomadaiIdx(piece)
                if(i%2 == 0) {
                    whiteKomadai[idx]--
                }else{
                    blackKomadai[idx]--
                }
            }

            // 打でなければ，移動前のマスを0にする
            if(nowPos != -1){
                board[nowPos] = 0
            }

            // 駒を移動する
            if(i%2 == 0){
                board[nextpos] = -piece
            }else{
                board[nextpos] = piece
            }

            allBoard.add(board.clone() as List<Int>)
            allBlackKomadai.add(blackKomadai.clone() as List<Int>)
            allWhiteKomadai.add(whiteKomadai.clone() as List<Int>)
        }
    }

    fun pieceToKomadaiIdx(piece: Int): Int {
        var piece = piece
        if (piece < 0) {
            piece *= -1
        }
        val pitceIdx = piece % 8 - 1
        var idx = -1
        when(pitceIdx) {
            0 -> idx = 0
            1 -> idx = 1
            2 -> idx = 2
            3 -> idx = 3
            4 -> idx = 5
            5 -> idx = 6
            6 -> idx = 4
        }
        return idx
    }
}
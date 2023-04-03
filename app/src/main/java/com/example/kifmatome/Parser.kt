package com.example.kifmatome

class Parser() {
    fun parse(lines: List<String>): GameInfo {
        val info = GameInfo()
        info.moveList = mutableListOf<Move>()

        var move = Move(-333, -333, -333, -333)
        move.sashite = "開始局面"

        var comment = ""

        var j = -1
        for (line in lines) {
            j++

            if(line.length == 0){
                continue
            }

            val lineTrim = line.trimStart()
            val str = lineTrim.substring(0, 1)
            if (str == "#") {
                // プログラムが読み飛ばすコメント
            } else if (str == "*") {
                comment += trimKifComment(line)
            } else if (isNum(str) == true) {
                if (comment.length != 0) {
                    comment = comment.substring(0, comment.length - 1)
                }
                move.comment = comment
                info.moveList.add(move)
                move = Move(-333, -333, -333, -333)
                generateSashiteInfo(info, move, line)
                comment = ""
            } else {
                // キーワード
                var i = 0
                while (i < line.length - 1) {
                    if (line.substring(i, i + 1) == "：") {
                        break
                    }
                    i++
                }
                // "：" not in line
                if (i == line.length - 1) {
                    continue
                }
                val keyword = line.substring(0, i)
                val value = line.substring(i + 1, line.length)
                when (keyword) {
                    "先手" -> info.senteName = value
                    "後手" -> info.goteName = value
                    "棋戦" -> info.gameName = value
                    "開始日時" -> info.date = value
                }
            }
        }

        return info
    }

    fun trimKifComment(str: String): String {
        var tmp = str.substring(1)
        tmp += "\n"
        return tmp
    }

    fun generateSashiteInfo(info: GameInfo, move: Move, s: String): Move {
        var i = 0
        var str = s
        str = str.trimStart()

        while (true) {
            val tmp = str.substring(i, i + 1)
            if (isNum(tmp)) {
                i++
            } else {
                break
            }
        }

        val tesuStr = str.substring(0, i)
        val tesu = tesuStr.toInt()
        move.moveNum = tesu

        str = str.substring(i + 1)
        i = 0
        while (true) {
            val tmp = str.substring(i, i + 1)
            if (tmp == "(") {
                break
            } else {
                i++
            }
        }
        val sashite = str.substring(0, i)
        if (isSashite(sashite)) {
            val nextPosStr = sashite.substring(0, 2)
            var nextPos = generateNextPos(nextPosStr)
            if (nextPos == -1) {
                nextPos = info.moveList.get(info.moveList.size-1).nextPos
                move.nextPos = nextPos
            } else {
                nextPos = posToIndex(nextPos)
                move.nextPos = nextPos
            }

            var pieceStr = sashite.substring(2)
            pieceStr = pieceStr.trimEnd()
            var count = 0
            for (j in 0..(pieceStr.length - 1)) {
                if (pieceStr.substring(j, j + 1) == "打") {
                    break
                }
                count++
            }
            pieceStr = pieceStr.substring(0, count)
            val piece = pieceToNum(pieceStr)
            move.piece = piece
            move.sashite = sashite
        } else {
            move.sashite = sashite
            return move
        }

        str = str.substring(i + 1)
        i = 0
        while (true) {
            val tmp = str.substring(i, i + 1)
            if (tmp == ")") {
                break
            } else {
                i++
            }
        }
        val nowPosStr = str.substring(0, i)
        val nowPos = posToIndex(generateNowPos(nowPosStr))
        move.nowPos = nowPos

        return move
    }

    fun isSashite(str: String): Boolean {
        val list = listOf("１", "２", "３", "４", "５", "６", "７", "８", "９", "同")
        val tmp = str.substring(0, 1)
        return list.contains(tmp)
    }

    fun generateNextPos(str: String): Int {
        var nextPos: Int
        var tmp = str.substring(0, 1)

        if (tmp == "同") {
            return -1
        } else {
            nextPos = 10 * strToNum(tmp)
            tmp = str.substring(1, 2)
            nextPos += strToNum(tmp)
            return nextPos
        }
    }

    fun strToNum(str: String): Int {
        when (str) {
            "１" -> return 1
            "２" -> return 2
            "３" -> return 3
            "４" -> return 4
            "５" -> return 5
            "６" -> return 6
            "７" -> return 7
            "８" -> return 8
            "９" -> return 9
            "一" -> return 1
            "二" -> return 2
            "三" -> return 3
            "四" -> return 4
            "五" -> return 5
            "六" -> return 6
            "七" -> return 7
            "八" -> return 8
            "九" -> return 9
        }
        return -1
    }

    fun pieceToNum(str: String): Int {
        when (str) {
            "歩" -> return 1
            "香" -> return 2
            "桂" -> return 3
            "銀" -> return 4
            "角" -> return 5
            "飛" -> return 6
            "金" -> return 7
            "王" -> return 8
            "玉" -> return 8
            "と" -> return 9
            "歩成" -> return 9
            "成香" -> return 10
            "香成" -> return 10
            "杏" -> return 10
            "成桂" -> return 11
            "桂成" -> return 11
            "圭" -> return 11
            "成銀" -> return 12
            "銀成" -> return 12
            "全" -> return 12
            "角成" -> return 13
            "馬" -> return 13
            "飛成" -> return 14
            "竜" -> return 14
            "龍" -> return 14
        }
        return -1
    }

    fun generateNowPos(str: String): Int {
        if (isNum(str)) {
            return str.toInt()
        } else {
            return -1
        }
    }

    fun isNum(str: String): Boolean {
        return try {
            str.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun posToIndex(i: Int): Int {
        if (i == -1) {
            return -1
        }
        val pos = (9 - i % 10) * 10 + (10 - i / 10)
        val tmp = ((8 - (pos / 10)) * 8 + (pos % 10 - 1) + (8 - (pos / 10)))
        return tmp
    }
}
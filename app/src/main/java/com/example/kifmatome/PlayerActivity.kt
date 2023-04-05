package com.example.kifmatome

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import java.nio.charset.Charset
class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // アクションバーを非表示にする
        supportActionBar?.hide()

        val filePath = intent.getStringExtra("filePath")

        lateinit var gameInfo: GameInfo

        val uri = Uri.parse(filePath)
        val inputStream = uri?.let { contentResolver.openInputStream(it) }
        if (inputStream != null) {
            try {
                val lines = inputStream.bufferedReader(Charset.forName("SJIS")).use { it.readLines() }
                val parser = Parser()
                gameInfo = parser.parse(lines)
            }catch(e:Exception){
                // 読み込みエラー
                AlertDialog.Builder(this)
                    .setTitle("エラー")
                    .setMessage("このファイルは読み込むことができませんでした")
                    .setPositiveButton("OK", { dialog, which ->
                    })
                    .show()
                return
            }
        }

        val tournamentView = findViewById<TextView>(R.id.tournament_view)
        val sashiteView = findViewById<TextView>(R.id.sashite_view)
        val blackNameView = findViewById<TextView>(R.id.black_name_view)
        val whiteNameView = findViewById<TextView>(R.id.white_name_view)
        val kifCommentView = findViewById<TextView>(R.id.kif_comment_view)

        tournamentView.text = gameInfo.gameName
        sashiteView.text = "開始局面"
        blackNameView.text = gameInfo.senteName
        whiteNameView.text = gameInfo.goteName
        kifCommentView.text = gameInfo.moveList.get(0).comment

        val player = Player(gameInfo)

        val listener = View.OnClickListener {
            when(it.id) {
                R.id.start_button -> {
                    updateBoard(player, player.tesu, 0)
                    updateKomadai(player, player.tesu, 0)
                    player.tesu = 0
                }
                R.id.prev_button -> {
                    if(player.tesu > 0) {
                        updateBoard(player, player.tesu, player.tesu-1)
                        updateKomadai(player, player.tesu, player.tesu-1)
                        player.tesu--
                    }
                }
                R.id.next_button -> {
                    if(player.tesu < gameInfo.moveList.size-1) {
                        updateBoard(player, player.tesu, player.tesu+1)
                        updateKomadai(player, player.tesu, player.tesu+1)
                        player.tesu++
                    }
                }
                R.id.last_button -> {
                    updateBoard(player, player.tesu, gameInfo.moveList.size-1)
                    updateKomadai(player, player.tesu, gameInfo.moveList.size-1)
                    player.tesu = gameInfo.moveList.size-1
                }
            }
            sashiteView.text = gameInfo.moveList.get(player.tesu).sashite
            kifCommentView.text = gameInfo.moveList.get(player.tesu).comment
        }

        findViewById<Button>(R.id.setting_button).setOnClickListener(listener)
        findViewById<Button>(R.id.start_button).setOnClickListener(listener)
        findViewById<Button>(R.id.prev_button).setOnClickListener(listener)
        findViewById<Button>(R.id.next_button).setOnClickListener(listener)
        findViewById<Button>(R.id.last_button).setOnClickListener(listener)
    }

    fun updateBoard(player: Player, beforeTesu: Int, afterTesu: Int) {
        val beforeBoard = player.allBoard.get(beforeTesu)
        val afterBoard = player.allBoard.get(afterTesu)

        for(i in 0..beforeBoard.size-1){
            if(beforeBoard[i] != afterBoard[i]){
                val view = idxToSquareView(i)
                val bmp = pieceToBitmap(afterBoard[i])
                view.setImageBitmap(bmp)
            }
        }
    }

    fun updateKomadai(player: Player, beforeTesu: Int, afterTesu: Int) {
        val beforeBlackKomadai = player.allBlackKomadai.get(beforeTesu)
        val beforeWhiteKomadai = player.allWhiteKomadai.get(beforeTesu)
        val afterBlackKomadai = player.allBlackKomadai.get(afterTesu)
        val afterWhitekomadai = player.allWhiteKomadai.get(afterTesu)

        for(i in 0..beforeBlackKomadai.size-1){
            if(beforeBlackKomadai[i] != afterBlackKomadai[i]){
                val komadaiView = idxToKomadaiView(i+1)
                val komadaiNumView = idxToKomadaiNumView(i+1)
                if(afterBlackKomadai[i] == 0){
                    komadaiView.setImageBitmap(null)
                    komadaiNumView.text = null
                }else {
                    val pieceIdx = idxToPieceIdx(i)
                    val bmp = pieceToBitmap(pieceIdx)
                    komadaiView.setImageBitmap(bmp)
                    komadaiNumView.text = afterBlackKomadai[i].toString()
                }
            }
            if(beforeWhiteKomadai[i] != afterWhitekomadai[i]){
                val komadaiView = idxToKomadaiView(-(i+1))
                val komadaiNumView = idxToKomadaiNumView(-(i+1))
                if(afterWhitekomadai[i] == 0){
                    komadaiView.setImageBitmap(null)
                    komadaiNumView.text = null
                }else{
                    val pieceIdx = idxToPieceIdx(i)
                    val bmp = pieceToBitmap(-pieceIdx)
                    komadaiView.setImageBitmap(bmp)
                    komadaiNumView.text = afterWhitekomadai[i].toString()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // layoutのサイズを動的に変える

        val shogiLayout = findViewById<LinearLayout>(R.id.shogi_layout)
        val boardLayout = findViewById<FrameLayout>(R.id.board_layout)
        val blackKomadaiLayout = findViewById<LinearLayout>(R.id.black_komadai_layout)
        val whiteKomadaiLayout = findViewById<LinearLayout>(R.id.white_komadai_layout)

        val boardLayoutWidth = shogiLayout.width * 53 / 67
        val boardLayoutHeight = boardLayoutWidth * 500 / 458
        val komadaiLayoutWidth = shogiLayout.width * 7 / 67
        val komadaiLayoutHeight = boardLayoutHeight

        val boardLayoutParam = LinearLayout.LayoutParams(boardLayoutWidth, boardLayoutHeight)
        val komadaiViewParam = LinearLayout.LayoutParams(komadaiLayoutWidth, komadaiLayoutHeight)

        boardLayout.layoutParams = boardLayoutParam
        blackKomadaiLayout.layoutParams = komadaiViewParam
        whiteKomadaiLayout.layoutParams = komadaiViewParam

        // boardLayoutのpaddingを動的に設定
        val boardLinearLayout = findViewById<LinearLayout>(R.id.board_linear_layout)
        val padding = boardLayoutWidth / 75
        boardLinearLayout.setPadding(padding)
    }

    fun idxToSquareView(idx: Int): ImageView {
        lateinit var view: ImageView
        when(idx){
            0 -> view = findViewById(R.id.square_view_0)
            1 -> view = findViewById(R.id.square_view_1)
            2 -> view = findViewById(R.id.square_view_2)
            3 -> view = findViewById(R.id.square_view_3)
            4 -> view = findViewById(R.id.square_view_4)
            5 -> view = findViewById(R.id.square_view_5)
            6 -> view = findViewById(R.id.square_view_6)
            7 -> view = findViewById(R.id.square_view_7)
            8 -> view = findViewById(R.id.square_view_8)
            9 -> view = findViewById(R.id.square_view_9)
            10 -> view = findViewById(R.id.square_view_10)
            11 -> view = findViewById(R.id.square_view_11)
            12 -> view = findViewById(R.id.square_view_12)
            13 -> view = findViewById(R.id.square_view_13)
            14 -> view = findViewById(R.id.square_view_14)
            15 -> view = findViewById(R.id.square_view_15)
            16 -> view = findViewById(R.id.square_view_16)
            17 -> view = findViewById(R.id.square_view_17)
            18 -> view = findViewById(R.id.square_view_18)
            19 -> view = findViewById(R.id.square_view_19)
            20 -> view = findViewById(R.id.square_view_20)
            21 -> view = findViewById(R.id.square_view_21)
            22 -> view = findViewById(R.id.square_view_22)
            23 -> view = findViewById(R.id.square_view_23)
            24 -> view = findViewById(R.id.square_view_24)
            25 -> view = findViewById(R.id.square_view_25)
            26 -> view = findViewById(R.id.square_view_26)
            27 -> view = findViewById(R.id.square_view_27)
            28 -> view = findViewById(R.id.square_view_28)
            29 -> view = findViewById(R.id.square_view_29)
            30 -> view = findViewById(R.id.square_view_30)
            31 -> view = findViewById(R.id.square_view_31)
            32 -> view = findViewById(R.id.square_view_32)
            33 -> view = findViewById(R.id.square_view_33)
            34 -> view = findViewById(R.id.square_view_34)
            35 -> view = findViewById(R.id.square_view_35)
            36 -> view = findViewById(R.id.square_view_36)
            37 -> view = findViewById(R.id.square_view_37)
            38 -> view = findViewById(R.id.square_view_38)
            39 -> view = findViewById(R.id.square_view_39)
            40 -> view = findViewById(R.id.square_view_40)
            41 -> view = findViewById(R.id.square_view_41)
            42 -> view = findViewById(R.id.square_view_42)
            43 -> view = findViewById(R.id.square_view_43)
            44 -> view = findViewById(R.id.square_view_44)
            45 -> view = findViewById(R.id.square_view_45)
            46 -> view = findViewById(R.id.square_view_46)
            47 -> view = findViewById(R.id.square_view_47)
            48 -> view = findViewById(R.id.square_view_48)
            49 -> view = findViewById(R.id.square_view_49)
            50 -> view = findViewById(R.id.square_view_50)
            51 -> view = findViewById(R.id.square_view_51)
            52 -> view = findViewById(R.id.square_view_52)
            53 -> view = findViewById(R.id.square_view_53)
            54 -> view = findViewById(R.id.square_view_54)
            55 -> view = findViewById(R.id.square_view_55)
            56 -> view = findViewById(R.id.square_view_56)
            57 -> view = findViewById(R.id.square_view_57)
            58 -> view = findViewById(R.id.square_view_58)
            59 -> view = findViewById(R.id.square_view_59)
            60 -> view = findViewById(R.id.square_view_60)
            61 -> view = findViewById(R.id.square_view_61)
            62 -> view = findViewById(R.id.square_view_62)
            63 -> view = findViewById(R.id.square_view_63)
            64 -> view = findViewById(R.id.square_view_64)
            65 -> view = findViewById(R.id.square_view_65)
            66 -> view = findViewById(R.id.square_view_66)
            67 -> view = findViewById(R.id.square_view_67)
            68 -> view = findViewById(R.id.square_view_68)
            69 -> view = findViewById(R.id.square_view_69)
            70 -> view = findViewById(R.id.square_view_70)
            71 -> view = findViewById(R.id.square_view_71)
            72 -> view = findViewById(R.id.square_view_72)
            73 -> view = findViewById(R.id.square_view_73)
            74 -> view = findViewById(R.id.square_view_74)
            75 -> view = findViewById(R.id.square_view_75)
            76 -> view = findViewById(R.id.square_view_76)
            77 -> view = findViewById(R.id.square_view_77)
            78 -> view = findViewById(R.id.square_view_78)
            79 -> view = findViewById(R.id.square_view_79)
            80 -> view = findViewById(R.id.square_view_80)
        }
        return view
    }

    fun idxToKomadaiView(idx: Int): ImageView {
        lateinit var view: ImageView

        when (idx) {
            -7 -> view = findViewById(R.id.white_komadai_view_6)
            -6 -> view = findViewById(R.id.white_komadai_view_5)
            -5 -> view = findViewById(R.id.white_komadai_view_4)
            -4 -> view = findViewById(R.id.white_komadai_view_3)
            -3 -> view = findViewById(R.id.white_komadai_view_2)
            -2 -> view = findViewById(R.id.white_komadai_view_1)
            -1 -> view = findViewById(R.id.white_komadai_view_0)
            1 -> view = findViewById(R.id.black_komadai_view_0)
            2 -> view = findViewById(R.id.black_komadai_view_1)
            3 -> view = findViewById(R.id.black_komadai_view_2)
            4 -> view = findViewById(R.id.black_komadai_view_3)
            5 -> view = findViewById(R.id.black_komadai_view_4)
            6 -> view = findViewById(R.id.black_komadai_view_5)
            7 -> view = findViewById(R.id.black_komadai_view_6)
        }

        return view
    }

    fun idxToKomadaiNumView(idx: Int): TextView {
        lateinit var view: TextView

        when (idx) {
            -7 -> view = findViewById(R.id.white_komadai_num_view_6)
            -6 -> view = findViewById(R.id.white_komadai_num_view_5)
            -5 -> view = findViewById(R.id.white_komadai_num_view_4)
            -4 -> view = findViewById(R.id.white_komadai_num_view_3)
            -3 -> view = findViewById(R.id.white_komadai_num_view_2)
            -2 -> view = findViewById(R.id.white_komadai_num_view_1)
            -1 -> view = findViewById(R.id.white_komadai_num_view_0)
            1 -> view = findViewById(R.id.black_komadai_num_view_0)
            2 -> view = findViewById(R.id.black_komadai_num_view_1)
            3 -> view = findViewById(R.id.black_komadai_num_view_2)
            4 -> view = findViewById(R.id.black_komadai_num_view_3)
            5 -> view = findViewById(R.id.black_komadai_num_view_4)
            6 -> view = findViewById(R.id.black_komadai_num_view_5)
            7 -> view = findViewById(R.id.black_komadai_num_view_6)
        }

        return view
    }

    fun pieceToBitmap(piece: Int): Bitmap? {

        if (piece == 0){
            return null
        }

        lateinit var bmp: Bitmap
        when (piece) {
            -14 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wry)
            -13 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wum)
            -12 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wng)
            -11 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wnk)
            -10 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wny)
            -9 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wto)
            -8 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wgy)
            -7 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wki)
            -6 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.whi)
            -5 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wka)
            -4 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wgi)
            -3 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wke)
            -2 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wya)
            -1 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.wfu)
            1 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bfu)
            2 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bya)
            3 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bke)
            4 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bgi)
            5 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bka)
            6 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bhi)
            7 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bki)
            8 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bgy)
            9 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bto)
            10 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bny)
            11 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bnk)
            12 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bng)
            13 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bum)
            14 -> bmp = BitmapFactory.decodeResource(resources, R.drawable.bry)
        }

        return bmp
    }

    fun idxToPieceIdx(idx: Int): Int {
        when(idx) {
            0 -> return 1
            1 -> return 2
            2 -> return 3
            3 -> return 4
            4 -> return 7
            5 -> return 5
            6 -> return 6
        }
        return -1
    }
}

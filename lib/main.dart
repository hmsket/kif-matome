import 'package:flutter/material.dart';

import 'package:kif_matome/kif_listview.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

enum addMenu {addTab, addKif}
enum sortMenu {sortTab, sortKif}

class _MyHomePageState extends State<MyHomePage> {

  void popupAddMenuSelected(addMenu selectedAddMenu) {
    switch(selectedAddMenu) {
      case addMenu.addTab:
        showDialog(context: context, builder: (context){
          return AlertDialog(
            title: Text('タブを追加'),
            content: TextField(decoration: InputDecoration(hintText: 'タブ名を入力してください'),),
            actions: [
              TextButton(onPressed: () => Navigator.pop(context), child: Text('キャンセル')),
              TextButton(onPressed: () => Navigator.pop(context), child: Text('OK')),
            ],
          );
        });
        break;
      case addMenu.addKif:
        break;
      default:
        break;
    }
  }

  void popupSortMenuSelected(sortMenu selectedSortMenu) {
    switch(selectedSortMenu) {
      case sortMenu.sortTab:
        break;
      case sortMenu.sortKif:
        break;
      default:
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      initialIndex: 0, // 最初に表示するタブ
      length: 8, // タブの数
      child: Scaffold(
        appBar: AppBar(
          title: const Text('棋譜まとめ'),
          actions: <Widget>[
            PopupMenuButton<addMenu>(
              icon: Icon(Icons.add),
              onSelected: popupAddMenuSelected,
              itemBuilder: (BuildContext context) => <PopupMenuEntry<addMenu>>[
                const PopupMenuItem<addMenu>(
                  value: addMenu.addTab,
                  child: Text('タブを追加'),
                ),
                  const PopupMenuItem<addMenu>(
                  value: addMenu.addKif,
                  child: Text('棋譜を追加'),
                ),
              ],
            ),
            PopupMenuButton<sortMenu>(
              icon: Icon(Icons.swap_vert),
              onSelected: popupSortMenuSelected,
              itemBuilder: (BuildContext context) => <PopupMenuEntry<sortMenu>>[
                const PopupMenuItem<sortMenu>(
                  value: sortMenu.sortTab,
                  child: Text('タブを並び替え'),
                ),
                  const PopupMenuItem<sortMenu>(
                  value: sortMenu.sortKif,
                  child: Text('棋譜を並び替え'),
                ),
              ],
            ),
          ],

          bottom: const TabBar(
            isScrollable: true, // スクロールを有効化
            tabs: <Widget>[
              Tab(text: '角換わり腰掛け銀'),
              Tab(text: 'サッカー'),
              Tab(text: 'テニス'),
              Tab(text: 'バスケ'),
              Tab(text: '剣道'),
              Tab(text: '柔道'),
              Tab(text: '水泳'),
              Tab(text: '卓球'),
            ],
          ),
        ),
        body: const TabBarView(
          children: <Widget>[
            KifListview(),
            KifListview(),
            KifListview(),
            KifListview(),
            KifListview(),
            KifListview(),
            KifListview(),
            KifListview(),
          ],
        ),
      ),
    );
  }
}

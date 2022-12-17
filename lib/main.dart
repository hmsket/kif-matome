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

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      initialIndex: 0, // 最初に表示するタブ
      length: 8, // タブの数
      child: Scaffold(
        appBar: AppBar(
          title: const Text('棋譜まとめ'),
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

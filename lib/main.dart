import 'package:flutter/material.dart';

import 'package:kif_matome/kif_listview.dart';
import 'package:kif_matome/db_helper.dart';
import 'package:kif_matome/my_tab.dart';

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

class _MyHomePageState extends State<MyHomePage> with TickerProviderStateMixin {
  final _editController = TextEditingController();
  TabController? _tabController;
  List<String> _tabList = [];

  TabController _createNewTabController() => TabController(
    vsync: this,
    length: _tabList.length,
  );

  @override
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this, length: _tabList.length);
    getTabList().then((value) {
      setState(() {
        _tabList = value;
        _tabController = _createNewTabController();
      });
    },);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('棋譜まとめ'),
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

        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabs: _tabList
              .map(
                (t) => Tab(
                  text: t,
                ),
              )
              .toList(),
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: _tabList.map((tab) {
          return createTab(tab);
        }).toList(),
      )
    );
  }

  void popupAddMenuSelected(addMenu selectedAddMenu) {
    switch(selectedAddMenu) {
      case addMenu.addTab:
        showDialog(context: context, builder: (context){
          return AlertDialog(
            title: Text('タブを追加'),
            content: TextField(controller: _editController, decoration: InputDecoration(hintText: 'タブ名を入力してください'),),
            actions: [
              TextButton(onPressed: () => Navigator.pop(context), child: Text('キャンセル')),
              TextButton(onPressed: () => addTab(_editController.text), child: Text('OK')),
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

  void addTab(String tabName) async {
    int tabId = await DBhelper.instance.getMaxTabId() + 1;
    int tabOrder = await DBhelper.instance.getMaxTabOrder() + 1;
    MyTab tab = MyTab(tabId, tabName, tabOrder);
    DBhelper.instance.insertTab(tab);
    Navigator.pop(context);
    
    getTabList().then((value) {
      setState(() {
        _tabList = value;
        _tabController = _createNewTabController();
      });
    },);
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

  Future<List<String>> getTabList() async {
    List<MyTab> allTabs = await DBhelper.instance.getAllTabs();
    allTabs.sort((a, b) => a.order.compareTo(b.order));
    List<String> tabs = [];
    allTabs.forEach((element) {
      tabs.add(element.name);
    });
    return tabs;
  }

  Widget createTab(String tab){
    return KifListview();
  }
  
}
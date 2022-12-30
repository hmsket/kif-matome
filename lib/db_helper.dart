import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:kif_matome/my_tab.dart';

import 'package:sqflite/sqflite.dart';

class DBhelper {
  static final DBhelper instance = DBhelper._createInstance();
  static Database? db;

  DBhelper._createInstance();

  // databaseをオープンしてインスタンス化する
  Future<Database> get database async {
    return db ??= await _initDB();
  }

  // データベースをオープンする
  Future<Database> _initDB() async {
    var databasesPath = await getDatabasesPath();
    String path = databasesPath + '/kifmatome.db';

    return await openDatabase(
      path,
      version: 1,
      onCreate: _onCreate,
    );
  }
  
  // テーブルが存在しなかったら作成する
  Future _onCreate(Database db, int version) async {
    await db.execute(
      'CREATE TABLE IF NOT EXISTS tabs ('
      ' tab_id INTEGER PRIMARY KEY,'
      ' tab_name TEXT,'
      ' tab_order INTEGER'
      ');',
    );
    await db.execute(
      'CREATE TABLE IF NOT EXISTS kifs ('
      ' tab_id INTEGER,'
      ' kif_id INTEGER,'
      ' kif_order INTEGER,'
      ' kif_image TEXT,'
      ' kif_title TEXT,'
      ' kif_tournament TEXT,'
      ' kif_date TEXT,'
      ' kif_sente TEXT,'
      ' kif_gote TEXT,'
      ' kif_path TEXT,'
      ' PRIMARY KEY ("tab_id", "kif_id")'
      ');',
    );
  }

  // tabsテーブルのデータをすべて取得する
  Future<List<MyTab>> getAllTabs() async {
    final db = await database;
    List<Map> records = await db.rawQuery('SELECT * FROM "tabs";');
    return records.map((Map m){
      int id = m['tab_id'];
      String name = m['tab_name'];
      int order = m['tab_order'];
      return MyTab(id, name, order);
    }).toList();
    // Map<String, Object?> mapRead = records.first;
    // String tmp = mapRead['MAX(tab_id)'].toString();
    // int maxTabId = int.parse(tmp);
    // return records;
  }

// // _idをキーにして1件のデータを読み込む
//   Future<Cats> catData(int id) async {
//     final db = await instance.database;
//     var cat = [];
//     cat = await db.query(
//       'cats',
//       columns: columns,
//       where: '_id = ?',                     // 渡されたidをキーにしてcatsテーブルを読み込む
//       whereArgs: [id],
//     );
//       return Cats.fromJson(cat.first);      // 1件だけなので.toListは不要
//   }

  // tab_idの最大値を取得する
  Future<int> getMaxTabId() async {
    final db = await database;
    List<Map<String, Object?>> records = await db.rawQuery('SELECT MAX(tab_id) FROM "tabs";');
    Map<String, Object?> mapRead = records.first;
    String tmp = mapRead['MAX(tab_id)'].toString();

    // NOTE : nullをtoString()でString型にしてるから，=='null'にしてる
    // TODO : ↑はスマートじゃないから改善したい
    if(tmp == 'null'){
      return -1;
    }else{
      int maxTabId = int.parse(tmp);
      return maxTabId;
    }
  }

  // tab_orderの最大値を取得する
  Future<int> getMaxTabOrder() async {
    final db = await database;
    List<Map<String, Object?>> records = await db.rawQuery('SELECT MAX(tab_order) FROM "tabs";');
    Map<String, Object?> mapRead = records.first;
    String tmp = mapRead['MAX(tab_order)'].toString();
    if(tmp == 'null'){
      return -1;
    }else{
      int maxTabOrder = int.parse(tmp);
      return maxTabOrder;
    }
  }

  // タブを追加する
  Future insertTab(MyTab tab) async {
    final db = await database;
    return await db.insert(
      'tabs',
      tab.toMap()
    );
  }

//   // データをupdateする
//   Future update(Cats cats) async {
//     final db = await database;
//     return await db.update(
//       'cats',
//       cats.toJson(),
//       where: '_id = ?',                   // idで指定されたデータを更新する
//       whereArgs: [cats.id],
//     );
//   }

// // データを削除する
//   Future delete(int id) async {
//     final db = await instance.database;
//     return await db.delete(
//       'cats',
//       where: '_id = ?',                   // idで指定されたデータを削除する
//       whereArgs: [id],
//     );
//   }
}

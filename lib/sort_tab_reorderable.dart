import 'package:flutter/material.dart';

class MyReorderableListview extends StatefulWidget {
  List<String> sortTabList;

  MyReorderableListview({required this.sortTabList});
  
  @override
  State<MyReorderableListview> createState() => _MyWidgetState();

  List<String> getSortTabs () {
    return sortTabList;
  }
}

class _MyWidgetState extends State<MyReorderableListview> {
  @override
  Widget build(BuildContext context) {
    return ReorderableListView(
      shrinkWrap: true,
      children: [
        for(int i=0; i<widget.sortTabList.length; i++)
          ListTile(
            key: Key('$i'),
            title: Text(widget.sortTabList[i]),
          )
      ],
      onReorder:(oldIndex, newIndex) {
        setState(() {
          if (oldIndex < newIndex) {
            newIndex -= 1;
          }
          String item = widget.sortTabList.removeAt(oldIndex);
          widget.sortTabList.insert(newIndex, item);
        });
      },
    );
  }
}

import 'package:flutter/material.dart';

import 'package:kif_matome/shogi_board.dart';

class KifListview extends StatelessWidget {
  const KifListview({super.key});

  @override
  Widget build(BuildContext context) {
    final kifs = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'];

    return Container(
      child: (
        ListView.builder(
          itemCount: kifs.length,
          itemBuilder: (context, index) {
            return GestureDetector(
              onTap: (() {
                Navigator.push(context, MaterialPageRoute(builder:(context) => ShogiBoard(),));
              }),
              child: 
                Column(
                  children: [
                    Row(
                      children: [
                        Container(
                          alignment: Alignment.topLeft,
                          width: 100,
                          child: Image.asset('images/initialBoard.png'),
                        ),
                        Column(
                          children: [
                            Text('Title', style: TextStyle(fontSize: 25),),
                            Text('Tournament'),
                            Text('Date'),
                            Text('Sente'),
                            Text('Gote'),
                          ],
                        )
                      ],
                    ),
                    Divider(height: 1,),
                  ],
                )
              );
          },
        )
      ),
    );
  }
}
import 'package:flutter/material.dart';

class ShogiBoard extends StatelessWidget {
  const ShogiBoard({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Second Screen'),
      ),
      body: Container(
        child: Column(
          children: [
            Row(
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    children: [
                      Image.asset('./images/komadai.png'),
                      Divider(height: 1,),
                      Text('△\n加\n藤\n一\n二\n三', style: TextStyle(fontSize: 10),),
                    ],
                  ), 
                ),
                VerticalDivider(width: 1,),
                Expanded(flex: 8, child:
                  Stack(
                    children: [
                      Image.asset('./images/board.png'),
                      GridView.count(
                        padding: EdgeInsets.all(5.3),
                        mainAxisSpacing: 4.7,
                        crossAxisSpacing: 1,
                        crossAxisCount: 9,
                        children: [
                          Image.asset('./images/koma/wya.png'),
                          Image.asset('./images/koma/wke.png'),
                          Image.asset('./images/koma/wgi.png'),
                          Image.asset('./images/koma/wki.png'),
                          Image.asset('./images/koma/wgy.png'),
                          Image.asset('./images/koma/wki.png'),
                          Image.asset('./images/koma/wgi.png'),
                          Image.asset('./images/koma/wke.png'),
                          Image.asset('./images/koma/wya.png'),
                          Spacer(),
                          Image.asset('./images/koma/whi.png'),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Image.asset('./images/koma/wka.png'),
                          Spacer(),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Image.asset('./images/koma/wfu.png'),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Image.asset('./images/koma/bfu.png'),
                          Spacer(),
                          Image.asset('./images/koma/bka.png'),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Spacer(),
                          Image.asset('./images/koma/bhi.png'),
                          Spacer(),
                          Image.asset('./images/koma/bya.png'),
                          Image.asset('./images/koma/bke.png'),
                          Image.asset('./images/koma/bgi.png'),
                          Image.asset('./images/koma/bki.png'),
                          Image.asset('./images/koma/bgy.png'),
                          Image.asset('./images/koma/bki.png'),
                          Image.asset('./images/koma/bgi.png'),
                          Image.asset('./images/koma/bke.png'),
                          Image.asset('./images/koma/bya.png'),
                        ],
                        shrinkWrap: true,
                      ),
                    ],
                  ),
                ),
                VerticalDivider(width: 1,),
                Expanded(
                  flex: 1,
                  child: Column(
                    children: [
                      Text('▲\n加\n藤\n一\n二\n三', style: TextStyle(fontSize: 10),),
                      Divider(height: 1,),
                      Image.asset('./images/komadai.png'),
                    ],
                  ) 
                ),
              ],
            ),
            Divider(height: 1,),
            Row(
              children: [
                Expanded(flex: 1, child: Text('棋戦')),
                VerticalDivider(width: 1,),
                Expanded(flex: 1, child: Text('指し手')),
              ],
            ),
            Divider(height: 1,),
            Text('コメント'),
            Divider(height: 1,),
            Row(
              children: [
                Expanded(flex: 1, child: ElevatedButton(onPressed: (() {}), child: Text('設定')),),
                Expanded(flex: 1, child: ElevatedButton(onPressed: (() {}), child: Text('◀◀')),),
                Expanded(flex: 1, child: ElevatedButton(onPressed: (() {}), child: Text('◀')),),
                Expanded(flex: 1, child: ElevatedButton(onPressed: (() {}), child: Text('▶')),),
                Expanded(flex: 1, child: ElevatedButton(onPressed: (() {}), child: Text('▶▶')),),
              ],
            )
          ],
        ),
      ),
    );
  }
}
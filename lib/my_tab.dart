class MyTab {
  int id;
  String name;
  int order;

  MyTab(this.id, this.name, this.order);

  Map<String, dynamic> toMap() {
    return {
      'tab_id': id,
      'tab_name': name,
      'tab_order': order,
    };
  }
}
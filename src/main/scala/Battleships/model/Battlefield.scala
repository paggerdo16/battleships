package Battleships.model

case class Battlefield (var size :Int) {
  val fields = construct(size)

  def construct (size : Int, sizeHolder : Int = size, holder : List[Position] =List(Position(0,0))):List[Position] = {
    require(sizeHolder > 0, "warum bloß ? I bin a zaubara.")
    size match {
      case 0 => holder.filter(_ != Position(0,0))
      case otherwise =>
        var i = 0
        var list = List(Position(0,0))
        while (i < sizeHolder) {
          list = list ::: List(Position(size,i+1))
          i=i+1
        }
        construct(size-1,sizeHolder,holder ::: list)
    }
  }
}
//not needed and not used !
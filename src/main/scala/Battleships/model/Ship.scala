package Battleships.model

case class Ship (length : Int , startPos : Position , direction : String) {
  val BattlePos = create(length, startPos,  direction)
  val hits = List(Position(0,0))
  require(length>0, "schnaml a schiff gsegn was gleich 0 oda kürza is ?? Na glab nit")

  def create(length : Int , startPos : Position , direction : String):List[Position] ={
    var list = List(startPos)
    var i = length-1
    direction match {
      case "horizontal" =>
        while(i > 0) {
          list = list ::: List(Position(startPos.getX + i,startPos.getY))
          i = i - 1
        }
        list
      case "vertical" =>
        while(i > 0) {
          list = list ::: List(Position(startPos.getX,startPos.getY + i))
          i = i -1
        }
        list
    }
  }
}
//not needed and not used
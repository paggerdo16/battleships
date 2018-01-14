package Battleships.model

case class Player (playername : String) {
  var name = playername
  var shots = List(Position(0, 0))
  var takenshots = 0 //besser als length von shots zu nehmen

  def getShot(turn: Int) = { //function fürn slider
    getElem(this.shots, turn)
  }

  def setName(playname : String): Unit ={
    name = playname
  }

  def shoot (shotPos: Position, flotte : Fleet) : Int = {
    var result = 3 //wenn nicht geändert dann daneben 0---> bereits getroffen 1---> treffer 2 ----> zerstört 3 ---> daneben
    require(shotPos != Position(0,0),"Diese Koordinate ist reserviert wird für Fehlerbugging verwendet gibt es im Spiel nicht")
    if(existIn(shotPos,this.shots)) 0 //println("Auf Koordinate " + shotPos + " wurde bereits gefeuert.") //funktion terminiert
    else { //Treffer existiert noch nicht wir durchsuchen die Liste
      var i = 0
      while(i < flotte.shipsPos.length) { //auch wenn getroffen sucht weiter
        var PosList = getElemListPos(flotte.shipsPos,i)
        if(findElemPos(shotPos,PosList) != Position(0,0)){
          result = 1 // /println("Das Schiff " + i + " wurde an der Koordinate " + shotPos + " getroffen." + PosList.length)
          if(PosList.length == 1) result = 2 //println("Ein Schiff wurde zerstört !") //Wenn zum Zeitpunkt des Treffers die länge der betroffenen Liste eins ist ist danach das Schiff zerstört ;)
          flotte.removeHit(shotPos) //Wenn Treffer wird Koordinate entfernt
        }
        i += 1
      }
      takenshots += 1 //egal ob getroffen oder nicht takenshots wird um 1 erhöht
      shots = shots ::: List(shotPos) //der Schuss wird der Liste angefügt
      result
    }
  }

  def existIn (elem1 : Any, elem2 : List[Any]): Boolean = {
    elem2.length match{
      case 0 => "Dominik" == "isanicerdude"
      case otherwhise => {
        if (elem1 == elem2.head) 1==1
        else existIn(elem1,elem2.tail)
      }
    }
  }

  def getElem(list: List[Any],index:Int): Any={
    index match{
      case 0 => list.head
      case _=> getElem(list.tail,index-1)
    }
  }

  def getElemListPos(list: List[List[Position]],index:Int): List[Position] ={ //Es muss dasverwendet werden sonst knallts
    index match{
      case 0 => list.head
      case _=> getElemListPos(list.tail,index-1)
    }
  }

  def findElemPos (elem1 : Position, elem2 : List[Position]): Position = { //Wenn mit ergebnis in diesem Fall Position weitergearbeitet werden wird MUSS DER Return Type Pos sein deswegen ähnliche funks.
    elem2.length match{
      case 0 => Position(0,0)
      case otherwhise => {
        if (elem1 == elem2.head) elem2.head
        else findElemPos(elem1,elem2.tail)
      }
    }
  }
}
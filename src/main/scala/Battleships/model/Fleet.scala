package Battleships.model

case class Fleet (ships : List[List[Position]]) {
  var shipsPos = ships
  //überschreibt unsere schiffsPositionen und nimmt die Position heraus kann damit als HitPointListe verwendet werden
  def removeHit (Pos : Position) : Unit = { //Funktion entfernt Treffer aus gegebener List Liste von Positionen
    shipsPos = shipsPos.map(x => x.filter(_ != Pos))
  }
  def addShip (list : List[List[Position]]) :Unit = {
    shipsPos = shipsPos ::: list
  }
}


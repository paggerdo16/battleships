import javafx.event.ActionEvent
import javafx.scene.layout.{AnchorPane, GridPane, Pane}
import javafx.scene._
import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.input.MouseEvent

import Battleships.model.{Fleet, Player, Position}

class welcomeFXController extends Initializable{

  //our Anchorpanes
  @FXML private var rootpane: AnchorPane = _
  @FXML private var setupgame : AnchorPane = _
  @FXML private var game: AnchorPane = _

  //Our Setupfields and Button
  @FXML private var player1Name: TextField = _
  @FXML private var player2Name: TextField = _
  @FXML private var battleShips: TextField = _
  @FXML private var cruisers: TextField = _
  @FXML private var submarines: TextField = _
  @FXML private var confirm: Button = _
  @FXML private var setupError : Label = _

  //OUR GAME COMPONENTS
  @FXML private var player : Label = _
  @FXML private var battleGrid : GridPane =_

  //PLACERS
  @FXML private var placeBattle : Button = _
  @FXML private var placeCruiser: Button = _
  @FXML private var placeSubmarine : Button = _
  @FXML private var dirBtn : Button = _
  @FXML private var dirPane : Pane = _

  //BattleGridPanes and needed fields
  @FXML private var gameStart : AnchorPane = _
  @FXML private var player1_Grid : GridPane = _
  @FXML private var player2_Grid : GridPane = _
  @FXML private var turnLabel : Label = _

  //Blender
  @FXML private var blenderAnch : AnchorPane = _
  @FXML private var blender : Pane = _

  //DEBUG
  @FXML private var DEBUG_E : Button = _
  @FXML private var DEBUG_B : Button = _
  @FXML private var DEBUG_A : AnchorPane = _

  //Save our setupinfoin these vars
  var battleField_Size : Int = _
  var battleShips_Amount : Int = _
  var cruisers_Amount : Int = _
  var submarines_Amount : Int = _

  //GAME VARS IMPORTANT
  var length : Int = 0
  var setupStatus : Int = 0
  var shipDirection : Int = -1

  //PLAYER1 VARS
  var player1 = Player("Player1")
  var player1_battleships : Int = 0
  var player1_cruisers : Int = 0
  var player1_submarines : Int = 0
  var player1_fleet : Fleet = new Fleet(List(List(Position(0,0))))
  var player1_zerstoert : Int = 0

  //PLAYER2 VARS
  var player2 = Player("Player2")
  var player2_battleships : Int = 0
  var player2_cruisers : Int = 0
  var player2_submarines : Int = 0
  var player2_fleet : Fleet = new Fleet(List(List(Position(0,0))))
  var player2_zerstoert : Int = 0

  override def initialize(url: URL, rb: ResourceBundle): Unit = initGame()
  def initGame():Unit ={
    //HIDE OUR OTHER STATES
    setupgame.setVisible(false)
    game.setVisible(false)
    game.setManaged(false)
    //game Starting
    gameStart.setVisible(false)
    gameStart.setManaged(false)
    //Disable our Blenders
    blenderAnch.setVisible(false) //SIND JZ DISABLED bind ma noch a funktion dran
    blenderAnch.setManaged(false)

    DEBUG_A.setManaged(false)
    DEBUG_A.setVisible(false)
  }

  @FXML private def startSetup(event: ActionEvent): Unit = {
    println("Loading Setup")
    rootpane.getChildren.clear()
    setupgame.setVisible(1==1)
    setupgame.setManaged(1==1)
  }

  @FXML private def startgame(event: ActionEvent): Unit ={
    println("Loading Game")
    val setupString = battleShips.getText + cruisers.getText + submarines.getText //contains all our strings so if statement is shorter ;)
    if(setupString.isEmpty ||
      !isAllDigits(setupString) ||
      battleShips.getText.toInt + cruisers.getText.toInt + submarines.getText.toInt == 0 ||
      player1Name.getText.isEmpty ||
      player2Name.getText.isEmpty ||
      battleShips.getText.toInt * 5 + cruisers.getText.toInt * 3 + submarines.getText.toInt * 2 > 49 ||
      player1Name.getText.length > 10 ||
      player2Name.getText.length > 10) setupError.setText("Only numbers above 0, names shorter than 10 chars and all fields filled!")
    else{
      //FETCH OUR SETTINGS
      battleShips_Amount = battleShips.getText.toInt
      cruisers_Amount = cruisers.getText.toInt
      submarines_Amount = submarines.getText.toInt

      //SET Player NAMES
      player1.setName(player1Name.getText)
      player2.setName(player2Name.getText)

      //NOW SETUP THE GAME
      setupgame.getChildren.clear()
      game.setVisible(true)
      game.setManaged(true)
      setupStatus = 1
      length = 5 //select the Battleship

      //SET PLAYER1 VARS
      player1_battleships= battleShips_Amount
      player1_cruisers= cruisers_Amount
      player1_submarines= submarines_Amount

      //Set Player2VARS
      player2_battleships= battleShips_Amount
      player2_cruisers= cruisers_Amount
      player2_submarines= submarines_Amount

      //Set STAT INFO
      player.setText(player1.name + " it´s your turn!")

      //SET TEXT OF OUR BUTTONS
      placeBattle.setText("Battleships: " + battleShips_Amount.toString)
      placeCruiser.setText("Cruisers: " + cruisers_Amount.toString)
      placeSubmarine.setText("Submarines: " + submarines_Amount.toString)
    }
  }

  @FXML private def changeDir(event: ActionEvent): Unit ={
    shipDirection match {
      case -1 => {
        dirBtn.setText("right")
        dirPane.setStyle("dirBtn2")
        shipDirection +=1
      }
      case 0 => {
        dirBtn.setText("left")
        shipDirection +=1
      }
      case 1 => {
        dirBtn.setText("down")
        shipDirection += 1
      }
      case 2 => {
        dirBtn.setText("up")
        shipDirection += 1
      }
      case 3 => {
        dirBtn.setText("right")
        shipDirection = 0
      }
    }
  }

  @FXML private def getcord(event: MouseEvent): Unit ={
    if(setupStatus == 1 || setupStatus == 3) {
      if(!shipPlaceCheck()) println("Please choose another ship") //returns false if the chosen ship amount is 0
      else{
        player.setText("Place your Ship")
        var node : Node = event.getPickResult.getIntersectedNode
        var x = GridPane.getColumnIndex(node)
        var y = GridPane.getRowIndex(node)
        var selectedNode : Node = getNode(x,y,battleGrid)
        //WE got now our Starting node now its time to calculate the direction a and fetch the POS
        var Ship =List(Position(x+1,y+1))
        shipDirection match{
          case -1 => {
            println("Please select a Direction first")
          }
          case 0 => {
            var i = x + length -1
            if(i>6) println("This wont fit")
            else{
              while(i > x) {
                var tinynode = getNode(if(i == 0) null else i, y, battleGrid)
                Ship = Ship ::: List(Position(i+1,y+1))
                i = i - 1
              }
              if(setupStatus == 1) {
                if(hasSimiliarEntitites(Ship,player1_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else{
                  Ship.foreach(coords => getNode(if(coords.x-1 == 0) null else coords.x-1, if(coords.y-1 == 0) null else coords.y-1 ,battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
              else if(setupStatus == 3) {
                if (hasSimiliarEntitites(Ship, player2_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else {
                  Ship.foreach(coords => getNode(if (coords.x - 1 == 0) null else coords.x - 1, if (coords.y - 1 == 0) null else coords.y - 1, battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
            }
          }
          case 1 => {
            var i = x - length +1
            if(i<0)println("This wont fit")
            else {
              while (i < x) {
                var tinynode = getNode(if (i == 0) null else i, y, battleGrid)
                Ship = Ship ::: List(Position(i + 1, y + 1))
                i = i + 1
              }
              if (setupStatus == 1) {
                if (hasSimiliarEntitites(Ship, player1_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else {
                  Ship.foreach(coords => getNode(if (coords.x - 1 == 0) null else coords.x - 1, if (coords.y - 1 == 0) null else coords.y - 1, battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
              else if (setupStatus == 3) {
                if (hasSimiliarEntitites(Ship, player2_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else {
                  Ship.foreach(coords => getNode(if (coords.x - 1 == 0) null else coords.x - 1, if (coords.y - 1 == 0) null else coords.y - 1, battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
            }
          }
          case 2 => {
            var i = y + length -1
            if(i > 6) println("This wont fit")
            else{
              while(i > y) {
                var tinynode = getNode(x, if(i == 0) null else i, battleGrid)
                Ship = Ship ::: List(Position(x+1,i+1))
                i = i - 1
              }
              if(setupStatus == 1) {
                if(hasSimiliarEntitites(Ship,player1_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else{
                  Ship.foreach(coords => getNode(if(coords.x-1 == 0) null else coords.x-1, if(coords.y-1 == 0) null else coords.y-1 ,battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
              else if(setupStatus == 3) {
                if (hasSimiliarEntitites(Ship, player2_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else {
                  Ship.foreach(coords => getNode(if (coords.x - 1 == 0) null else coords.x - 1, if (coords.y - 1 == 0) null else coords.y - 1, battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
            }
          }
          case 3 => {
            var i = y - length +1
            if(i < 0) println("This wont fit")
            else{
              while(i < y) {
                var tinynode = getNode(x, if(i == 0) null else i, battleGrid)
                Ship = Ship ::: List(Position(x+1,i+1))
                i = i + 1
              }
              if(setupStatus == 1) {
                if(hasSimiliarEntitites(Ship,player1_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else{
                  Ship.foreach(coords => getNode(if(coords.x-1 == 0) null else coords.x-1, if(coords.y-1 == 0) null else coords.y-1 ,battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
              else if(setupStatus == 3) {
                if (hasSimiliarEntitites(Ship, player2_fleet.shipsPos)) println("Do steht schn a Schiff du Pferd")
                else {
                  Ship.foreach(coords => getNode(if (coords.x - 1 == 0) null else coords.x - 1, if (coords.y - 1 == 0) null else coords.y - 1, battleGrid).setStyle("-fx-background-color: #36403B"))
                  player1_fleet.addShip(List(Ship)) //duplicate Code otherwhise ship will not be added watch how code gets executed!
                  shipReduction() //afterwards is ESSENTIAL DO NOT TOUCH PLS
                }
              }
            }
          }
        }
      }
    }
    else println("Select a Ship first")
  }

  def shipPlaceCheck(): Boolean = {
    if (setupStatus == 1) {
      length match {
        case 5 => if (player1_battleships > 0) true else false
        case 3 => if (player1_cruisers > 0) true else false
        case 2 => if (player1_submarines > 0) true else false
      }
    }
    else {
      length match {
        case 5 => if (player2_battleships > 0) true else false
        case 3 => if (player2_cruisers > 0) true else false
        case 2 => if (player2_submarines > 0) true else false
      }
    }
  }

  def shipReduction () : Unit = {
    if(setupStatus == 1) { //Setupstatus is one taht means player one is setting up
      length match {
        case 5 => {
          if (player1_battleships > 0) {
            player1_battleships -= 1
            placeBattle.setText("Battleships: " + player1_battleships.toString)
          }
        }
        case 3 => {
          if (player1_cruisers > 0){
            player1_cruisers -= 1
            placeCruiser.setText("Cruisers: " + player1_cruisers.toString)
          }
        }
        case 2 =>{
          if(player1_submarines > 0) {
            player1_submarines -= 1
            placeSubmarine.setText("Submarines: " + player1_submarines.toString)
          }
        }
      }
    if(player1_submarines+player1_cruisers+player1_battleships ==0) changePlayerSetup()
    }
    else{ //setupstatus is not one and this function gets called means player 2 is setting up
      length match {
        case 5 => if (player2_battleships > 0) {
          player2_battleships -= 1
          placeBattle.setText("Battleships: " + player2_battleships.toString)
        }
        case 3 => if (player2_cruisers > 0) {
          player2_cruisers -= 1
          placeCruiser.setText("Cruisers: " + player2_cruisers.toString)
        }
        case 2 => if (player2_submarines > 0) {
          player2_submarines -= 1
          placeSubmarine.setText("Submarines: " + player2_submarines.toString)
        }
      }
      if(player2_submarines+player2_cruisers+player2_battleships == 0) {
        (player1_fleet.shipsPos)
        prepGame()
      }
    }
  }

  //EXECUTED A PLACEMENT BUTTON GETS CLICKED
  @FXML private def placeShip(event: ActionEvent): Unit = {
    var node = event.getSource().toString.take(22)
    if(node == "Button[id=placeBattle,") length = 5
    if(node == "Button[id=placeCruiser") length = 3
    if(node == "Button[id=placeSubmari") length = 2
    player.setText("Select Starting Point")
  }

  def changePlayerSetup() : Unit = {
    setupStatus = 3
    player.setText(player2.name + " it´s your turn!")
    placeBattle.setText("Battleships: " + player2_battleships.toString)
    placeCruiser.setText("Cruisers: " + player2_cruisers.toString)
    placeSubmarine.setText("Submarines: " + player2_submarines.toString)
    //Color THAT SHIT
    battleGrid.getChildren.forEach(node => node.setStyle("-fx-background-color: #62BCFA"))
  }

  def prepGame() : Unit = {
    //DISABLE OUR PLACEMENT BUTTONS -Wir kenntn de a in an container packen wären nur 2 zeilen
    game.setVisible(false)
    game.setManaged(false)
    //fifty fifty wer startet
    val starter = scala.util.Random
    var gameStatus = starter.nextInt(2) //kann 0 oder eins annehmen //wir könnten damit alle wenn gerade player 1 wenn ungerade player 2 für wer ist gerade dran
    startgame(gameStatus)
  }

  //just a small helper for small people
  def isAllDigits(x: String) = x forall Character.isDigit

  def getNode(column: Any,row : Any, grid : GridPane) : Node = {
    var children = grid.getChildren
    var result : Node = null
    children.forEach(node=>if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) result = node)
    result
  }

  def isEven(number: Int) = number % 2 == 0

  def startgame (starter : Int) : Unit = {
    gameStart.setManaged(true)
    gameStart.setVisible(true)
    if(isEven(starter)){ //player 1 ones turn so player 2 Grid is active
      turnLabel.setText(player1.name + " starts!")
      player1_Grid.setManaged(false)
      player1_Grid.setVisible(false)
      player2_Grid.setManaged(true)
      player2_Grid.setVisible(true)
    }
    if(!isEven(starter)){ //player 2 ones turn so player 1 Grid is active
      turnLabel.setText(player2.name + " starts!")
      player1_Grid.setManaged(true)
      player1_Grid.setVisible(true)
      player2_Grid.setManaged(false)
      player2_Grid.setVisible(false)
    }
  }

  @FXML private def shootGridP1(event: MouseEvent): Unit ={
    var node : Node = event.getPickResult.getIntersectedNode
    var x = GridPane.getColumnIndex(node)
    var y = GridPane.getRowIndex(node)
    player1.shoot(Position(x+1,y+1),player2_fleet) match {
      case 0 => {
        println("Shoot again you already shot there")
      }
      case 1 => {
        println("You hit a ship")
        node.setStyle("-fx-background-color: #C43235")
      }
      case 2 => {
        println("You destroyed a ship!")
        node.setStyle("-fx-background-color: #C43235")
        player1_zerstoert += 1
        if(player1_zerstoert == battleShips_Amount+submarines_Amount+cruisers_Amount) {
          end(0)
        }
      }
      case 3 => {
        println("You missed")
        node.setStyle("-fx-background-color: #36403B")
        player1_Grid.setManaged(false)
        player1_Grid.setVisible(false)
        player2_Grid.setManaged(true)
        player2_Grid.setVisible(true)
        turnLabel.setText("It´s " + player2.name + " turn")
      }
    }
  }

  @FXML private def shootGridP2(event: MouseEvent): Unit ={
    var node : Node = event.getPickResult.getIntersectedNode
    var x = GridPane.getColumnIndex(node)
    var y = GridPane.getRowIndex(node)
    player2.shoot(Position(x+1,y+1),player1_fleet) match {
      case 0 => {
        println("Shoot again you already shot there")
      }
      case 1 => {
        println("You hit a ship")
        node.setStyle("-fx-background-color: #C43235")
      }
      case 2 => {
        println("You destroyed a ship!")
        node.setStyle("-fx-background-color: #C43235")
        player2_zerstoert += 1
        if(player2_zerstoert == battleShips_Amount+submarines_Amount+cruisers_Amount){
          end(1)
        }
      }
      case 3 => {
        println("You missed")
        node.setStyle("-fx-background-color: #36403B")
        //versteck die das jetzige gridpane zeig das neue gg
        player1_Grid.setManaged(true)
        player1_Grid.setVisible(true)
        player2_Grid.setManaged(false)
        player2_Grid.setVisible(false)
        turnLabel.setText("It´s " + player1.name + " turn")
      }
    }
  }

  def end(i : Int):Unit = {
    player1_Grid.setManaged(false)
    player1_Grid.setVisible(false)
    player2_Grid.setManaged(false)
    player2_Grid.setVisible(false)
    if(i == 0){
      turnLabel.setText(player1.name + " won!")
    }
    else{
      turnLabel.setText(player2.name + " won!")
    }
  }
  @FXML def DEBUGB(event: ActionEvent): Unit = {
    gameStart.setManaged(true)
    gameStart.setVisible(true)
    rootpane.setManaged(false)
    rootpane.setVisible(false)
    game.setManaged(false)
    game.setVisible(false)
  }

  @FXML def DEBUGE(event: ActionEvent): Unit = {
    game.setManaged(true)
    game.setVisible(true)
    rootpane.setManaged(false)
    rootpane.setVisible(false)
    gameStart.setManaged(false)
    gameStart.setVisible(false)
  }
  //HELPING FUNCTIONS
  def existIn (elem1 : Any, elem2 : List[Any]): Boolean = {
    elem2.length match{
      case 0 => false
      case otherwhise => {
        if (elem1 == elem2.head) true
        else existIn(elem1,elem2.tail)
      }
    }
  }

  def existInListList (elem1 : Position, elem2 : List[List[Position]]): Boolean = {
    elem2.length match{
      case 0 => false
      case otherwhise => {
        if(existIn(elem1,elem2.head)) true
        else existInListList(elem1,elem2.tail)
      }
    }
  }

  def hasSimiliarEntitites (elem1 : List[Position],elem2 : List[List[Position]]) : Boolean = {
    elem1.length match{
      case 0 => false
      case otherwhise => {
        if(existInListList(elem1.head,elem2)) true
        else hasSimiliarEntitites(elem1.tail,elem2)
      }
    }
  }

}



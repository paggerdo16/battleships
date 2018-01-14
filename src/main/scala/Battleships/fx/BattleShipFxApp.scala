package Battleships.fx
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

object BattleShipGame {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[BattleShipFX], args: _*)
  }
}

class BattleShipFX extends Application {
  val root = FXMLLoader.load[Parent](getClass.getResource("welcome.fxml"))
  val css = "Battleships/fx/stylesheet.css"

  override def start(stage: Stage) = {
    stage.setTitle("Battleships")
    stage.setScene(new Scene(root))
    stage.getScene.getStylesheets.add(css)
    stage.setResizable(false) //damit ma uns alle ka scheiße antun müssn
    stage.show()
  }

}
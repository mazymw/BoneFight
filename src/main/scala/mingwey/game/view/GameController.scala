package mingwey.game.view
import mingwey.game.MainApp
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class GameController(){


  object ProjectileMotionApp extends JFXApp {
    // Constants for projectile motion
    val g = 9.81  // Acceleration due to gravity
    val v0 = 50.0 // Initial velocity (m/s)
    val angle = 45.0 // Launch angle (degrees)

    // Convert angle to radians
    val angleRad = Math.toRadians(angle)

    // Initial velocity components
    val v0x = v0 * Math.cos(angleRad)
    val v0y = v0 * Math.sin(angleRad)

    // Create a circle to represent the projectile
    val projectile = new Circle {
      radius = 5
      fill = Color.Red
      centerX = 0
      centerY = 0
    }

    // Timeline to animate the projectile
    val animation = new Timeline {
      cycleCount = Timeline.Indefinite
      autoReverse = false
      keyFrames = Seq(
        new KeyFrame(Duration(0), new EventHandler[ActionEvent] {
          override def handle(event: ActionEvent): Unit = {
            projectile.centerX = 0
            projectile.centerY = 0
          }
        }),
        new KeyFrame(Duration(10000), new EventHandler[ActionEvent] {
          override def handle(event: ActionEvent): Unit = {
            val t = 10.0 // Time in seconds (adjust as needed)
            val x = v0x * t
            val y = v0y * t - 0.5 * g * t * t
            projectile.centerX = x
            projectile.centerY = y
          }
        })
      )
    }

    stage = new JFXApp.PrimaryStage {
      title = "Projectile Motion"
      scene = new Scene(800, 600) {
        fill = Color.White
        content = projectile
      }
    }

    // Start the animation
    animation.play()
  }





}


